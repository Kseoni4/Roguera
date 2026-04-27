package kseoni.ch.roguera.graphics.ui.layout;

import lombok.Getter;

/**
 * Раскладка экрана на 5 зон: header / sidebar-left / main / sidebar-right / footer.
 * Размеры боковых панелей и footer-а — константы; подвинуть позже под настройки.
 */
@Getter
public class Layout {

    private static final int HEADER_HEIGHT = 1;
    private static final int FOOTER_HEIGHT = 2;
    private static final int SIDEBAR_WIDTH = 28;

    private final Region screen;
    private final Region header;
    private final Region sidebarLeft;
    private final Region main;
    private final Region sidebarRight;
    private final Region footer;

    public Layout(int cols, int rows) {
        this.screen = new Region(0, 0, cols, rows);

        int midY = HEADER_HEIGHT;
        int midH = rows - HEADER_HEIGHT - FOOTER_HEIGHT;

        this.header = new Region(0, 0, cols, HEADER_HEIGHT);
        this.sidebarLeft = new Region(0, midY, SIDEBAR_WIDTH, midH);
        this.main = new Region(SIDEBAR_WIDTH, midY, cols - 2 * SIDEBAR_WIDTH, midH);
        this.sidebarRight = new Region(cols - SIDEBAR_WIDTH, midY, SIDEBAR_WIDTH, midH);
        this.footer = new Region(0, rows - FOOTER_HEIGHT, cols, FOOTER_HEIGHT);
    }
}
