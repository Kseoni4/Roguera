package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;

public class MapDrawer implements Drawer<Cell>{

    private final static int HEADER_OFFSET_Y = 1;

    private final RenderLayer mapLayer;

    public MapDrawer(){
        mapLayer = Window.get().getRenderLayer(TGLayer.BACKGROUND);
    }

    private Position toScreen(Position world) {
        return new Position(world.getX() * 2, world.getY());
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
//        relativePosition = relativePosition.getRelativePosition(0, HEADER_OFFSET_Y);
//        mapLayer.drawSpriteOn(object.getObject().getTextSprite(),
//                object.getPosition().getRelativePosition(relativePosition));
    }

    @Override
    public void draw(Cell object) {
        mapLayer.drawSpriteOn(
                object.getObject().getTextSprite(),
                object.getPosition().getRelativePosition(0, HEADER_OFFSET_Y));
    }

    @Override
    public void refresh() {
        Window.get().refresh();
    }

    @Override
    public void clear() {
        Window.get().clearScreen();
    }
}
