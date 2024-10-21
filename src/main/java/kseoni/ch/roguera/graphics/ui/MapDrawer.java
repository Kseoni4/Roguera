package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.map.Cell;

public class MapDrawer implements Drawer<Cell>{
    private final RenderLayer mapLayer;

    public MapDrawer(){
        mapLayer = Window.get().getRenderLayer(TGLayer.BACKGROUND);
    }

    @Override
    public void draw(Cell object, Position relativePosition) {
        if (object.getObject().getName().equals("Player"))
            System.out.println("Draw object " + object);

        mapLayer.drawSpriteOn(object.getObject().getTextSprite(),
                object.getPosition().getRelativePosition(relativePosition));
    }

    @Override
    public void draw(Cell object) {
        mapLayer.drawSpriteOn(object.getObject().getTextSprite(), object.getPosition());
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
