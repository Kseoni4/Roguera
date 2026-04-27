package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.layout.BorderStyle;
import kseoni.ch.roguera.graphics.ui.layout.Region;
import kseoni.ch.roguera.graphics.ui.parts.UIFrame;
import lombok.Getter;

/**
 * Базовая UI-панель: рамка ({@link UIFrame}) + контентная область внутри.
 *
 * Подкласс реализует {@link #renderContent(RenderLayer, Region)} — рисует свой
 * контент в переданной content-области (с inset 1 от границ панели).
 */
public abstract class Panel {

    @Getter
    private final Region region;

    private final UIFrame frame;

    protected Panel(Region region, BorderStyle style, String title) {
        this.region = region;
        this.frame = new UIFrame(region, style, title);
    }

    protected Panel(Region region, UIFrame frame) {
        this.region = region;
        this.frame = frame;
    }

    /** Регион для контента — внутренность рамки (без границ). */
    public Region contentRegion() {
        return region.inset(1);
    }

    /** Полный рендер: сначала рамка, потом контент. Финальный refresh вызывает Window. */
    public final void render() {
        RenderLayer layer = Window.get().getRenderLayer(TGLayer.UI);
        frame.render(layer);
        renderContent(layer, contentRegion());
    }

    protected abstract void renderContent(RenderLayer layer, Region content);
}
