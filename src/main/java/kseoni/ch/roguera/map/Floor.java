package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.map.generator.MapGenerate;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

public class Floor {

    private Map<Integer, Room> rooms;

    @Getter
    private int floorNumber;

    private int worldXBound;
    private int worldYBound;

    @Getter
    @Setter
    private int roomIdPointer = 0;

    private int roomCount;

    public Floor(int roomCount, int floorNumber, int worldXBound, int worldYBound){
        EventLoop.get().send(Event.raise("Create new floor"));
        this.floorNumber = floorNumber;
        int failedGenerations = -1;
        while (this.rooms == null) {
            failedGenerations++;
            this.rooms = new MapGenerate(worldXBound, worldYBound).generateFloor(roomCount);
        }
        System.out.println("Failed generations before successful map "+failedGenerations);
        this.roomCount = roomCount;
        this.roomIdPointer = rooms.values().iterator().next().getRoomId();
        this.worldXBound = worldXBound;
        this.worldYBound = worldYBound;
    }

    public void nextRoom(int roomId){
        roomIdPointer = roomId;
    }

    public void regenerate(){
        System.out.println("Regenerating floor " + floorNumber);
        EventLoop.get().send(Event.raise("Regenerating floor"));
        this.rooms = null;
        int failedGenerations = -1;
        while (this.rooms == null) {
            failedGenerations++;
            this.rooms = new MapGenerate(worldXBound, worldYBound).generateFloor(roomCount);
        }
        System.out.println("Failed generations before successful map "+failedGenerations);
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
