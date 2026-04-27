package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.TerminalScreen;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.utils.Clock;
import kseoni.ch.roguera.utils.SettingsLoader;
import kseoni.ch.roguera.utils.Convert;

public class HeaderDrawer {

    // Палитра в стиле Catppuccin Mocha — цельный subtle-look
    private static final TextColor BG       = new TextColor.RGB(49, 50, 68);   // surface0
    private static final TextColor FG       = new TextColor.RGB(205, 214, 244); // text
    private static final TextColor MUTED    = new TextColor.RGB(127, 132, 156); // overlay1
    private static final TextColor ACCENT   = new TextColor.RGB(203, 166, 247); // mauve
    private static final TextColor OK       = new TextColor.RGB(166, 227, 161); // green
    private static final TextColor WARN     = new TextColor.RGB(249, 226, 175); // yellow
    private static final TextColor BAD      = new TextColor.RGB(243, 139, 168); // red

    private final TextGraphics gfx;
    private final int width;
    private final String title;
    private final String version;
    private final Player player;

    public HeaderDrawer(Player player) {
        this.gfx = Window.get().getRawScreen().newTextGraphics();
        this.width = Window.get().getWight();
        this.player = player;

        String full = SettingsLoader.getSettingValue("game.version");
        if (full.contains(" build ")) {
            int idx = full.indexOf(" build ");
            this.title = full.substring(0, idx);
            this.version = full.substring(idx + " build ".length());
        } else {
            this.title = "Roguera";
            this.version = full;
        }
    }

    public void draw() {
        // 1. Заливка фона
        gfx.setBackgroundColor(BG);
        gfx.fillRectangle(
                new com.googlecode.lanterna.TerminalPosition(0, 0),
                new com.googlecode.lanterna.TerminalSize(width, 1),
                new TextCharacter(' ').withBackgroundColor(BG)
        );

        // 2. Слева — название и версия
        int x = 1;
        x = put(" ", x, FG);
        x = put(title, x, ACCENT, SGR.BOLD);
        x = put("  ", x, FG);
        x = put(version, x, MUTED);

        // 3. Справа — FPS и координаты
        String fpsText = String.format("FPS %3.0f", Clock.getInstance().getCurrentFps());
        Position playerPos = player.getPosition();
        Position globalPos = Convert.toGlobalPosition(
                Dungeon.get().currentFloor().currentRoom().getRoomLeftTopPosition(),
                playerPos
        );
        String posText = String.format("@ %d,%d", globalPos.getX(), globalPos.getY());

        String right = fpsText + "  " + posText + " ";
        int rightX = width - right.length();

        put(fpsText, rightX, fpsColor(Clock.getInstance().getCurrentFps()));
        put("  ", rightX + fpsText.length(), FG);
        put(posText, rightX + fpsText.length() + 2, OK);
    }

    private int put(String text, int x, TextColor fg, SGR... modifiers) {
        gfx.setForegroundColor(fg);
        gfx.setBackgroundColor(BG);
        for (SGR m : modifiers) gfx.enableModifiers(m);
        gfx.putString(x, 0, text);
        for (SGR m : modifiers) gfx.disableModifiers(m);
        return x + text.length();
    }

    private TextColor fpsColor(double fps) {
        if (fps >= 25) return OK;
        if (fps >= 15) return WARN;
        return BAD;
    }
}