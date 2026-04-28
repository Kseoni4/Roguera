package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.layout.Region;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.utils.Clock;
import kseoni.ch.roguera.utils.Convert;
import kseoni.ch.roguera.utils.SettingsLoader;

/**
 * Однострочный статус-бар: слева — название и версия, справа — FPS и
 * глобальные координаты игрока. Рисуется в {@link Region}, выданный
 * {@link kseoni.ch.roguera.graphics.ui.layout.Layout}, на UI-слое.
 */
public class HeaderDrawer {

    private static final TextColor BG     = Palette.SURFACE0;
    private static final TextColor FG     = Palette.TEXT;
    private static final TextColor MUTED  = Palette.OVERLAY;
    private static final TextColor ACCENT = Palette.MAUVE;
    private static final TextColor OK     = Palette.GREEN;
    private static final TextColor WARN   = Palette.YELLOW;
    private static final TextColor BAD    = Palette.RED;

    private final RenderLayer layer;
    private final Region region;
    private final Player player;
    private final String title;
    private final String version;

    public HeaderDrawer(Player player, Region region) {
        this.layer = Window.get().getRenderLayer(TGLayer.UI);
        this.region = region;
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
        int y = region.y();

        // 1. Заливка фона строки.
        layer.fill(region, ' ', FG, BG);

        // 2. Слева: title + version.
        int x = region.x() + 1;
        layer.putString(x, y, title, ACCENT, BG, SGR.BOLD);
        x += title.length();
        x += 2; // разделитель
        layer.putString(x, y, version, MUTED, BG);

        // 3. Справа: FPS + глобальная позиция игрока.
        double fps = Clock.getInstance().getCurrentFps();
        String fpsText = String.format("FPS %3.0f", fps);

        Position globalPos = Convert.toGlobalPosition(
                Dungeon.get().currentFloor().currentRoom().getRoomLeftTopPosition(),
                player.getPosition()
        );
        String posText = String.format("@ %d,%d", globalPos.getX(), globalPos.getY());

        String right = fpsText + "  " + posText + " ";
        int rightX = region.right() - right.length();

        layer.putString(rightX, y, fpsText, fpsColor(fps), BG);
        layer.putString(rightX + fpsText.length() + 2, y, posText, OK, BG);
    }

    private TextColor fpsColor(double fps) {
        if (fps >= 25) return OK;
        if (fps >= 15) return WARN;
        return BAD;
    }
}
