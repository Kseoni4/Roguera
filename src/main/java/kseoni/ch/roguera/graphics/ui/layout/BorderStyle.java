package kseoni.ch.roguera.graphics.ui.layout;

/**
 * Пресеты Unicode box-drawing символов для рамок UI-панелей.
 *
 * Семантика по умолчанию (рекомендация для проекта):
 *  - ROUNDED: внутри-игровые помещения и пассивные UI-блоки.
 *  - SHARP: статические UI-панели (статус, лог, footer).
 *  - HEAVY: активная/сфокусированная панель.
 *  - DOUBLE: модальные диалоги (инвентарь, торговец).
 */
public enum BorderStyle {
    ROUNDED('╭', '╮', '╰', '╯', '─', '│'),
    SHARP  ('┌', '┐', '└', '┘', '─', '│'),
    HEAVY  ('┏', '┓', '┗', '┛', '━', '┃'),
    DOUBLE ('╔', '╗', '╚', '╝', '═', '║');

    public final char topLeft;
    public final char topRight;
    public final char bottomLeft;
    public final char bottomRight;
    public final char horizontal;
    public final char vertical;

    BorderStyle(char tl, char tr, char bl, char br, char h, char v) {
        this.topLeft = tl;
        this.topRight = tr;
        this.bottomLeft = bl;
        this.bottomRight = br;
        this.horizontal = h;
        this.vertical = v;
    }
}
