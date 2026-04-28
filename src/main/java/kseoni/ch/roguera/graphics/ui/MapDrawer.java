package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.graphics.ui.layout.Camera;
import kseoni.ch.roguera.graphics.ui.layout.Region;
import kseoni.ch.roguera.map.Cell;

/**
 * Рисует ячейки карты на BACKGROUND-слое внутри viewport-региона через
 * {@link Camera}. Ячейки за пределами viewport отбрасываются — карта
 * скроллится при движении игрока (см. {@link Camera#follow(Position)}).
 */
public class MapDrawer implements Drawer<Cell> {

    private final RenderLayer mapLayer;
    private final Region region;
    private final Camera camera;

    public MapDrawer(Region region, Camera camera) {
        this.mapLayer = Window.get().getRenderLayer(TGLayer.BACKGROUND);
        this.region = region;
        this.camera = camera;
    }

    @Override
    public void draw(Cell object, Position relativePosition) {
        Position world = object.getPosition().getRelativePosition(relativePosition);
        Position screen = camera.worldToScreen(world);

        if (!camera.isVisible(screen.getX(), screen.getY())
                || !camera.isVisible(screen.getX() + 1, screen.getY())) {
            return;
        }

        TextSprite sprite = object.getObject().getTextSprite();
        mapLayer.drawSpriteOn(sprite, screen);

        boolean fillBoth = object.isWall()
                && sprite.getSpriteChar() == AssetPool.get().getAsset("wall_h");

        mapLayer.drawSpriteOn(
                new TextSprite(fillBoth ? sprite.getSpriteChar() : ' ',
                        sprite.getSpriteColor(TextSprite.ColorLayer.FOREGROUND),
                        sprite.getSpriteColor(TextSprite.ColorLayer.BACKGROUND)),
                new Position(screen.getX() + 1, screen.getY())
        );
    }

    @Override
    public void draw(Cell object) {
        Position screen = camera.worldToScreen(object.getPosition());
        if (!camera.isVisible(screen.getX(), screen.getY())) return;
        mapLayer.drawSpriteOn(object.getObject().getTextSprite(), screen);
    }

    @Override
    public void refresh() {
        Window.get().refresh();
    }

    /** Заливает viewport карты Palette.BASE на BACKGROUND-слое. */
    @Override
    public void clear() {
        mapLayer.fill(region, ' ', Palette.SUBTEXT, Palette.BASE);
    }
}
