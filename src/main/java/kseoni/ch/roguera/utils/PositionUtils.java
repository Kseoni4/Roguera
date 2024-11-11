package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.map.Room;

public class PositionUtils {
    public static Position getNonOccupiedPositionCell(Room room, Position position) {
        Position currentPosition = position;
        int i = 0;
        while (i < Position.AroundPositions.length && (room.getCell(currentPosition) == null || !room.getCell(currentPosition).isEmpty())){
            currentPosition = currentPosition.getRelativePosition(Position.AroundPositions[i]);
            i++;
        }
        return currentPosition;
    }
}
