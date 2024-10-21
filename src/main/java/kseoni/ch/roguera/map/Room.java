package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.Window;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
public class Room {

    private final Position roomLeftTopPosition;

    private final int roomId;

    @Setter
    private int width;

    @Setter
    private int height;

    @Setter
    private HashMap<Position, Cell> cells;

    public Room(int roomId){
        this(roomId, Window.get().getWight(), Window.get().getHeight(), Position.ZERO);
    }

    public Room(int roomId, int width, int height, Position position){
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        this.roomLeftTopPosition = position;
        System.out.println("Create room [".concat(String.valueOf(width)).concat(";").concat(String.valueOf(height)).concat("]")
                .concat(" on position ").concat(position.toString()));
    }

    public Cell getCell(Position position){
        return cells.get(position);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                '}'+roomLeftTopPosition;
    }
}
