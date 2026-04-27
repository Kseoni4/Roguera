package kseoni.ch.roguera.graphics.ui.layout;

import kseoni.ch.roguera.graphics.sprites.AssetPool;

/**
 * Пресеты Unicode box-drawing символов для рамок UI-панелей.
 * Сами символы декларируются в char-assets.rca под префиксом
 * {@code border.<style>.<part>} и подгружаются лениво через {@link AssetPool}.
 *
 * Семантика по умолчанию:
 *  - ROUNDED: пассивные UI-блоки и внутри-игровые помещения.
 *  - SHARP:   статические UI-панели (статус, лог, footer).
 *  - HEAVY:   активная/сфокусированная панель.
 *  - DOUBLE:  модальные диалоги (инвентарь, торговец).
 */
public enum BorderStyle {
    ROUNDED("rounded"),
    SHARP("sharp"),
    HEAVY("heavy"),
    DOUBLE("double");

    private final String key;
    private boolean loaded;
    private char tl, tr, bl, br, h, v;

    BorderStyle(String key) {
        this.key = key;
    }

    public char topLeft()      { ensureLoaded(); return tl; }
    public char topRight()     { ensureLoaded(); return tr; }
    public char bottomLeft()   { ensureLoaded(); return bl; }
    public char bottomRight()  { ensureLoaded(); return br; }
    public char horizontal()   { ensureLoaded(); return h;  }
    public char vertical()     { ensureLoaded(); return v;  }

    private void ensureLoaded() {
        if (loaded) return;
        AssetPool pool = AssetPool.get();
        tl = pool.getAsset("border." + key + ".tl");
        tr = pool.getAsset("border." + key + ".tr");
        bl = pool.getAsset("border." + key + ".bl");
        br = pool.getAsset("border." + key + ".br");
        h  = pool.getAsset("border." + key + ".h");
        v  = pool.getAsset("border." + key + ".v");
        loaded = true;
    }
}
