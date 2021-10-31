package com.rogurea.gamemap;

import java.io.Serializable;
import java.util.ArrayList;

public class Floor implements Serializable {

    private static int floorNumberCounter = 0;

    private int floorNumber;

    private ArrayList<Room> roomsOnFloor;

    public ArrayList<Room> getRooms(){
        return this.roomsOnFloor;
    }

    public Floor(){
        this.floorNumber = ++floorNumberCounter;
        this.roomsOnFloor = new ArrayList<>();
    }

    public int getFloorNumber(){
        return this.floorNumber;
    }

    public void putRoomInFloor(Room room){
        room.floorNumber = this.floorNumber;
        this.roomsOnFloor.add(room);
    }

    public static void resetCounter(){
        floorNumberCounter = 0;
    }
}
