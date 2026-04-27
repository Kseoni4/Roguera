package kseoni.ch.roguera.graphics.ui.layout;

/**
 * Прямоугольная область в координатах терминала (cols × rows).
 * Immutable, без зависимости от Lanterna.
 */
public record Region(int x, int y, int width, int height) {

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean contains(int px, int py) {
        return px >= x && px < right() && py >= y && py < bottom();
    }

    /** Вернуть регион, уменьшенный на by с каждой стороны. */
    public Region inset(int by) {
        return inset(by, by, by, by);
    }

    public Region inset(int top, int right, int bottom, int left) {
        return new Region(
                x + left,
                y + top,
                Math.max(0, width - left - right),
                Math.max(0, height - top - bottom)
        );
    }
}
