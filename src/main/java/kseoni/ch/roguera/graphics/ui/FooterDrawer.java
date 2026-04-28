package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.layout.Region;

/**
 * Двухстрочный footer: тонкий разделитель сверху + сегментированные подсказки
 * клавиш снизу. Сегменты разделены `│`; имена клавиш — MAUVE+BOLD, описания —
 * OVERLAY.
 */
public class FooterDrawer {

    private static final TextColor BG       = Palette.BASE;
    private static final TextColor SEP_FG   = Palette.SURFACE1;
    private static final TextColor LABEL_FG = Palette.OVERLAY;
    private static final TextColor KEY_FG   = Palette.MAUVE;

    private static final String SEPARATOR = "  │  ";

    private final RenderLayer layer;
    private final Region region;

    public FooterDrawer(Region region) {
        this.layer = Window.get().getRenderLayer(TGLayer.UI);
        this.region = region;
    }

    public void draw() {
        if (region.height() < 2) return;

        // Верхняя строка — тонкий разделитель.
        int sepY = region.y();
        for (int x = region.x(); x < region.right(); x++) {
            layer.setChar(x, sepY, '─', SEP_FG, BG);
        }

        // Нижняя строка — фон + подсказки.
        int hintsY = region.y() + region.height() - 1;
        layer.fill(new Region(region.x(), hintsY, region.width(), 1), ' ', LABEL_FG, BG);

        int x = region.x() + 2;
        x = putKey(x, hintsY, "↑↓←→");
        x = putLabel(x, hintsY, " move");
        x = putSep(x, hintsY);
        x = putKey(x, hintsY, "g");
        x = putLabel(x, hintsY, " regen");
        x = putSep(x, hintsY);
        x = putKey(x, hintsY, "r");
        x = putLabel(x, hintsY, " redraw");
        x = putSep(x, hintsY);
        x = putKey(x, hintsY, "q");
        putLabel(x, hintsY, " quit");
    }

    private int putKey(int x, int y, String text) {
        layer.putString(x, y, text, KEY_FG, BG, SGR.BOLD);
        return x + text.length();
    }

    private int putLabel(int x, int y, String text) {
        layer.putString(x, y, text, LABEL_FG, BG);
        return x + text.length();
    }

    private int putSep(int x, int y) {
        layer.putString(x, y, SEPARATOR, SEP_FG, BG);
        return x + SEPARATOR.length();
    }
}
