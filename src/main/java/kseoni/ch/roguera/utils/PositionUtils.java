package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;

import java.util.Map;

public class PositionUtils {
    public static Position getNonOccupiedPositionCell(Room room, Position position) {
        Position currentPosition = position;
        int i = 0;
        while (i < Position.AROUND_POSITIONS.length && (room.getCell(currentPosition) == null || !room.getCell(currentPosition).isEmpty())) {
            currentPosition = currentPosition.getRelativePosition(Position.AROUND_POSITIONS[i]);
            i++;
        }
        return currentPosition;
    }

    public static boolean isEdge(Position p, Map<Position, Cell> cells) {
        Cell n = cells.get(p.getRelativePosition(Position.BACK));
        Cell e = cells.get(p.getRelativePosition(Position.RIGHT));
        Cell s = cells.get(p.getRelativePosition(Position.FRONT));
        Cell w = cells.get(p.getRelativePosition(Position.LEFT));

        // 1) Классическая edge — есть null среди кардинальных.
        if (n == null || e == null || s == null || w == null) return true;

        return false;
    }

    public static boolean isCorner(Position p, Map<Position, Cell> cells) {
        // 2) Вогнутый угол — диагональный null при двух смежных cells.
        Cell ne = cells.get(p.getRelativePosition(1, -1));
        Cell nw = cells.get(p.getRelativePosition(-1, -1));
        Cell se = cells.get(p.getRelativePosition(1, 1));
        Cell sw = cells.get(p.getRelativePosition(-1, 1));

        if (ne == null) return true;   // N и E точно cells (проверено выше)
        if (nw == null) return true;
        if (se == null) return true;
        if (sw == null) return true;

        return false;
    }
}
