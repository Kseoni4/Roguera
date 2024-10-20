package kseoni.ch.roguera.map;

import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;

public class Floor {

    private final HashMap<Integer, Room> rooms;

    @Getter
    private int floorNumber;

    @Getter
    private int roomIdPointer = 0;

    public Floor(int roomCount, int floorNumber){
        this.floorNumber = floorNumber;
        this.rooms = new MapGenerate().initFloor(roomCount);
    }

    public Room currentRoom(){
        return rooms.get(roomIdPointer);
    }

    public Collection<Room> getRooms(){
        return rooms.values();
    }

}
