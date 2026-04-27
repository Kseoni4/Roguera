package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
@Builder
@AllArgsConstructor
public class Room {

    private final Position roomLeftTopPosition;

    private final int roomId;

    private Position roomCenterPosition;

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
        Random rnd = new Random();
        roomCenterPosition = new Position(width/2, height/2);
        while (cells.get(roomCenterPosition) == null){
            roomCenterPosition = roomCenterPosition.getRelativePosition(Position.AroundPositions[rnd.nextInt(0, Position.AroundPositions.length)]);
        }
    }

    public void addDoor(Door door){
        doors.put(door.getToRoom(), door);
    }

    @Override
    public String toString() {
;        return String.format(
                "Room[%d]=" +
                "[%dw;%dh], " +
                "lt:%s",
        roomId, width, height, roomLeftTopPosition, cells.size()
        );
    }
}
