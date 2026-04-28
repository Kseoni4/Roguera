package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.layout.Camera;
import kseoni.ch.roguera.map.Floor;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.Convert;

/**
 * Поверх стационарной карты перерисовывает только анимированные ячейки —
 * игрока и двери — с цветами, интерполированными по sin-волне.
 *
 * Игрок пульсирует GREEN ↔ светлее (период 1.5с).
 * Двери — MAUVE ↔ PINK (период 2.0с), фазы намеренно не совпадают.
 */
public class AnimationDrawer {

    private static final TextColor PLAYER_C1 = Palette.GREEN;
    private static final TextColor PLAYER_C2 = lighter(Palette.GREEN, 50);
    private static final TextColor DOOR_C1 = Palette.MAUVE;
    private static final TextColor DOOR_C2 = Palette.PINK;

    private static final long PLAYER_PERIOD_MS = 1500;
    private static final long DOOR_PERIOD_MS   = 2000;

    private final RenderLayer layer;
    private final Camera camera;
    private final Player player;

    public AnimationDrawer(Camera camera, Player player) {
        this.layer = Window.get().getRenderLayer(TGLayer.BACKGROUND);
        this.camera = camera;
        this.player = player;
    }

    public void draw(long now, Floor floor, Room currentRoom) {
        drawPlayer(now, currentRoom);
        drawDoors(now, floor);
    }

    private void drawPlayer(long now, Room currentRoom) {
        Position world = Convert.toGlobalPosition(currentRoom.getRoomLeftTopPosition(), player.getPosition());
        Position screen = camera.worldToScreen(world);
        if (!camera.isVisible(screen.getX(), screen.getY())) return;

        TextColor fg = pulse(PLAYER_C1, PLAYER_C2, now, PLAYER_PERIOD_MS);
        layer.setChar(screen.getX(),     screen.getY(), '@', fg, Palette.BASE);
        // Вторая колонка под scaleX=2 — пустая, чтобы не оставался артефакт.
        layer.setChar(screen.getX() + 1, screen.getY(), ' ', fg, Palette.BASE);
    }

    private void drawDoors(long now, Floor floor) {
        TextColor fg = pulse(DOOR_C1, DOOR_C2, now, DOOR_PERIOD_MS);
        for (Room room : floor.getRooms()) {
            for (Door door : room.getDoors().values()) {
                Position world = Convert.toGlobalPosition(room.getRoomLeftTopPosition(), door.getPosition());
                Position screen = camera.worldToScreen(world);
                if (!camera.isVisible(screen.getX(), screen.getY())) continue;

                char ch = door.getTextSprite().getSpriteChar();
                layer.setChar(screen.getX(),     screen.getY(), ch,  fg, Palette.BASE);
                layer.setChar(screen.getX() + 1, screen.getY(), ' ', fg, Palette.BASE);
            }
        }
    }

    /** sin-волна между c1 и c2 с заданным периодом. */
    private static TextColor pulse(TextColor c1, TextColor c2, long now, long periodMs) {
        double phase = (Math.sin(now * 2 * Math.PI / periodMs) + 1.0) / 2.0;
        return interpolate(c1, c2, phase);
    }

    private static TextColor interpolate(TextColor a, TextColor b, double t) {
        int r = (int) Math.round(a.getRed()   * (1 - t) + b.getRed()   * t);
        int g = (int) Math.round(a.getGreen() * (1 - t) + b.getGreen() * t);
        int bl = (int) Math.round(a.getBlue() * (1 - t) + b.getBlue()  * t);
        return new TextColor.RGB(clamp(r), clamp(g), clamp(bl));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static TextColor lighter(TextColor c, int by) {
        return new TextColor.RGB(clamp(c.getRed() + by), clamp(c.getGreen() + by), clamp(c.getBlue() + by));
    }
}
