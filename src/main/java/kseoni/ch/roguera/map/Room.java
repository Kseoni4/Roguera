package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.Position;
import lombok.Getter;

import java.util.HashMap;

public class Room {

    @Getter
    private final int roomId;

    @Getter
    private final int width;

    @Getter
    private final int height;

    @Getter
    private HashMap<Position, Cell> cells;

    public Room(int roomId, int width, int height){
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        prepareCells();

        // System.out.println(cells);
    }

    public Cell getCell(Position position){
        return cells.get(position);
    }

    private void prepareCells(){
        this.cells = new HashMap<>();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Position pos = new Position(x,y);
                cells.put(pos, new Cell(pos));
            }
        }
    }
}
