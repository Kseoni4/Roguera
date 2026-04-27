package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.parts.UIPart;

public class UIDrawer implements Drawer<UIPart> {

    private RenderLayer uiLayer;

    public UIDrawer() {
        uiLayer = Window.get().getRenderLayer(TGLayer.UI);
    }

    @Override
    public void draw(UIPart object, Position relativePosition) {
        uiLayer.drawSpriteLine(object.getPartSprite(),
                object.getPartPosition(),
                object.getPartPosition().getRelativePosition(relativePosition));
    }

    @Override
    public void draw(UIPart object) {
        uiLayer.drawSpriteOn(object.getPartSprite(),
                object.getPartPosition());
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
