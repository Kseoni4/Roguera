package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.SGR;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.ui.layout.BorderStyle;
import kseoni.ch.roguera.graphics.ui.layout.Region;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Floor;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.Convert;
import kseoni.ch.roguera.utils.RandomUtils;

/**
 * Левый sidebar — статус игрока. Показывает имя, текущий этаж/комнату,
 * локальные и world-координаты, seed генерации.
 */
public class PlayerSidebar extends Panel {

    private final Player player;

    public PlayerSidebar(Region region, Player player) {
        super(region, BorderStyle.SHARP, "Player");
        this.player = player;
    }

    @Override
    protected void renderContent(RenderLayer layer, Region content) {
        Floor floor = Dungeon.get().currentFloor();
        Room room = floor.currentRoom();
        Position localPos = player.getPosition();
        Position worldPos = Convert.toGlobalPosition(room.getRoomLeftTopPosition(), localPos);

        int x = content.x() + 1;
        int y = content.y();

        putLabeled(layer, x, y++, "Name", player.getName(), Palette.TEXT);
        y++;
        putLabeled(layer, x, y++, "Floor", String.valueOf(floor.getFloorNumber()), Palette.GREEN);
        putLabeled(layer, x, y++, "Room",  String.valueOf(room.getRoomId()), Palette.GREEN);
        y++;
        putLabeled(layer, x, y++, "Local", String.format("%d,%d", localPos.getX(), localPos.getY()), Palette.SUBTEXT);
        putLabeled(layer, x, y++, "World", String.format("%d,%d", worldPos.getX(), worldPos.getY()), Palette.SUBTEXT);
        y++;
        putLabeled(layer, x, y, "Seed",  String.valueOf(RandomUtils.getSeed()), Palette.OVERLAY);
    }

    private void putLabeled(RenderLayer layer, int x, int y, String label, String value,
                            com.googlecode.lanterna.TextColor valueFg) {
        layer.putString(x, y, label, Palette.OVERLAY, Palette.BASE);
        layer.putString(x + label.length(), y, ": ", Palette.OVERLAY, Palette.BASE);
        layer.putString(x + label.length() + 2, y, value, valueFg, Palette.BASE, SGR.BOLD);
    }
}
