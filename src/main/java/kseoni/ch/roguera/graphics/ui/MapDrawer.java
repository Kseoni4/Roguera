package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.graphics.ui.layout.Region;
import kseoni.ch.roguera.map.Cell;

/**
 * Рисует ячейки карты на BACKGROUND-слое внутри переданного {@link Region}.
 * Координаты world → screen: (x * 2, y) + offset региона.
 *
 * {@link #clear()} заливает только map-регион, не затрагивая header / sidebar / footer.
 */
public class MapDrawer implements Drawer<Cell> {

    private final RenderLayer mapLayer;
    private final Region region;

    public MapDrawer(Region region) {
        this.mapLayer = Window.get().getRenderLayer(TGLayer.BACKGROUND);
        this.region = region;
    }

    private Position toScreen(Position world) {
        return new Position(region.x() + world.getX() * 2, region.y() + world.getY());
    }

    @Override
    public void draw(Cell object, Position relativePosition) {
        Position screen = toScreen(object.getPosition().getRelativePosition(relativePosition));
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
        mapLayer.drawSpriteOn(
                object.getObject().getTextSprite(),
                toScreen(object.getPosition())
        );
    }

    @Override
    public void refresh() {
        Window.get().refresh();
    }

    /** Заливает регион карты Palette.BASE на BACKGROUND-слое. UI-слой (рамка, header) не трогается. */
    @Override
    public void clear() {
        mapLayer.fill(region, ' ', Palette.SUBTEXT, Palette.BASE);
    }
}
