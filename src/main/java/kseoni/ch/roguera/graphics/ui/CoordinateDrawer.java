package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.TextCharacter;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;

public class CoordinateDrawer implements Drawer<Cell> {

    private final RenderLayer uiLayer;

    public CoordinateDrawer(){
        uiLayer = Window.get().getRenderLayer(TGLayer.UI);
    }

    @Override
    public void draw(Cell object, Position globalRelative) {
        uiLayer.drawSpriteOn(object.getObject().getTextSprite(), object.getPosition());
    }

    @Override
    public void refresh() {

    }
}
