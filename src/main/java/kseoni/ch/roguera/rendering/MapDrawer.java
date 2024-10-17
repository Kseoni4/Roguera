package kseoni.ch.roguera.rendering;

import kseoni.ch.roguera.map.Cell;

public class MapDrawer implements Drawer<Cell>{
    private final RenderLayer mapLayer;

    public MapDrawer(){
        mapLayer = Window.get().getRenderLayer(TGLayer.BACKGROUND);
    }

    @Override
    public void draw(Cell object) {
        mapLayer.drawSpriteOn(object.getObject().getTextSprite(), object.getPosition());
    }

    @Override
    public void refresh() {
        Window.get().refresh();
    }
}
