package kseoni.ch.roguera.map;

import kseoni.ch.roguera.map.generator.MapGenerate;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

public class Floor {

    private Map<Integer, Room> rooms;

    @Getter
    private int floorNumber;

    @Getter
    @Setter
    private int roomIdPointer = 0;

    private int roomCount;

    public Floor(int roomCount, int floorNumber){
        this.floorNumber = floorNumber;
        this.rooms = new MapGenerate().generateFloor(roomCount);
        this.roomCount = roomCount;
        this.roomIdPointer = rooms.values().iterator().next().getRoomId();
    }

    public void nextRoom(int roomId){
        roomIdPointer = roomId;
    }

    public void regenerate(){
        System.out.println("Regerating floor " + floorNumber);
        this.rooms = new MapGenerate().generateFloor(roomCount);
        this.roomIdPointer = rooms.values().iterator().next().getRoomId();
        System.out.println("New room id:"+roomIdPointer);
    }

    public Room currentRoom(){
        return rooms.get(roomIdPointer);
    }

    public Room getRoom(int roomId){
        return rooms.get(roomId);
    }

    public Collection<Room> getRooms(){
        return rooms.values();
    }

}
