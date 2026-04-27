package kseoni.ch.roguera.graphics.ui.parts;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.ui.layout.BorderStyle;
import kseoni.ch.roguera.graphics.ui.layout.Region;
import lombok.Getter;

/**
 * Прямоугольная рамка с углами/линиями выбранного {@link BorderStyle} и опциональным
 * встроенным в верхнюю кромку заголовком (`─ Title ──────`).
 *
 * Без знания о контенте — это чистая отрисовка обвязки. Контент рисует {@code Panel}
 * поверх в content-области ({@link #region}.inset(1)).
 */
@Getter
public class UIFrame {

    private final Region region;
    private final BorderStyle style;
    private final String title;

    private final TextColor borderFg;
    private final TextColor titleFg;
    private final TextColor bg;

    public UIFrame(Region region, BorderStyle style, String title) {
        this(region, style, title, Palette.OVERLAY, Palette.MAUVE, Palette.BASE);
    }

    public UIFrame(Region region, BorderStyle style, String title,
                   TextColor borderFg, TextColor titleFg, TextColor bg) {
        this.region = region;
        this.style = style;
        this.title = title;
        this.borderFg = borderFg;
        this.titleFg = titleFg;
        this.bg = bg;
    }

    public void render(RenderLayer layer) {
        if (region.width() < 2 || region.height() < 2) {
            return;
        }

        int left = region.x();
        int top = region.y();
        int right = region.right() - 1;
        int bottom = region.bottom() - 1;

        // Заливка фона внутреннего региона + кромок (чтобы сквозь рамку не светил
        // backdrop, если у Panel.bg отличается от Palette.BASE).
        layer.fill(region, ' ', borderFg, bg);

        // Горизонтальные линии
        for (int col = left + 1; col < right; col++) {
            layer.setChar(col, top, style.horizontal, borderFg, bg);
            layer.setChar(col, bottom, style.horizontal, borderFg, bg);
        }
        // Вертикальные линии
        for (int row = top + 1; row < bottom; row++) {
            layer.setChar(left, row, style.vertical, borderFg, bg);
            layer.setChar(right, row, style.vertical, borderFg, bg);
        }
        // Углы
        layer.setChar(left, top, style.topLeft, borderFg, bg);
        layer.setChar(right, top, style.topRight, borderFg, bg);
        layer.setChar(left, bottom, style.bottomLeft, borderFg, bg);
        layer.setChar(right, bottom, style.bottomRight, borderFg, bg);

        // Встроенный заголовок: ─ Title ─...
        if (title != null && !title.isEmpty()) {
            String label = " " + title + " ";
            int titleX = left + 2;
            int maxLen = Math.max(0, region.width() - 4);
            if (label.length() > maxLen) {
                label = label.substring(0, maxLen);
            }
            layer.putString(titleX, top, label, titleFg, bg);
        }
    }
}
