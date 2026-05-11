package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.utils.PositionUtils;
import kseoni.ch.roguera.utils.RandomUtils;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class Room {

    private final Position roomLeftTopPosition;

    private final int roomId;

    private Position roomCenterPosition;

    private LinkedHashSet<Position> edges;

    @Setter
    private int width;

    @Setter
    private int height;

    @Setter
    private HashMap<Position, Cell> cells;

    private HashMap<Integer, Door> doors = new HashMap<>();

    public Room(){
        roomLeftTopPosition = Position.ZERO;
        roomId = 0;
    }

    public Set<Position> getRoomEdges(){
        if(edges == null) {
            this.edges = cells.values()
                    .stream()
                    .map(Cell::getPosition)
                    .filter(p -> PositionUtils.isEdge(p, cells))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return this.edges;
    }

    public Room(int roomId){
        this(roomId, Window.get().getWight(), Window.get().getHeight(), Position.ZERO);
    }

    public Room(int roomId, int width, int height, Position position){
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        this.roomLeftTopPosition = position;
        if(Boolean.parseBoolean(SettingsLoader.getSettingValue("debug.show.room-create"))) {
            System.out.println("Create room " + roomId + " [".concat(String.valueOf(width)).concat(";").concat(String.valueOf(height)).concat("]")
                    .concat(" on position ").concat(position.toString()));
            System.out.println("Doors " + doors);
        }
    }

    public Cell getCell(Position position){
        return cells.get(position);
    }

    public Position getRoomCenter(){
        if(roomCenterPosition == null){
            calculateRoomCenter();
        }
        return roomCenterPosition;
    }

    private void calculateRoomCenter(){
        Random rnd = RandomUtils.getRandom();
        roomCenterPosition = new Position(width/2, height/2);
        while (cells.get(roomCenterPosition) == null){
            roomCenterPosition = roomCenterPosition.getRelativePosition(Position.AROUND_POSITIONS[rnd.nextInt(0, Position.AROUND_POSITIONS.length)]);
        }
    }

    public void addDoor(Door door){
        System.out.println("Add door %s -> %s".formatted(this.roomId, door.getToRoom()));
        doors.put(door.getToRoom(), door);
    }

    @Override
    public String toString() {
;        return String.format(
                "Room[%d]=" +
                "[%dw;%dh], " +
                "lt:%s " +
                "cells: %s",
        roomId, width, height, roomLeftTopPosition, cells.size()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomId == room.roomId && Objects.equals(roomLeftTopPosition, room.roomLeftTopPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomLeftTopPosition, roomId);
    }
}
