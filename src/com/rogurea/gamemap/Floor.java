package com.rogurea.gamemap;

import com.rogurea.base.Debug;

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
        Debug.toLog("[FLOOR "+floorNumber+"] New floor is created");
    }

    public int getFloorNumber(){
        return this.floorNumber;
    }

    public void putRoomInFloor(Room room){
        Debug.toLog("[FLOOR "+floorNumber+"] putting room "+ room.roomNumber + " into the floor");
        room.floorNumber = this.floorNumber;
        this.roomsOnFloor.add(room);
    }

    public static void resetCounter(){
        floorNumberCounter = 0;
    }

    public static void setCounterFromLoad(int currentFloorNumber){
        floorNumberCounter = currentFloorNumber;
    }
}
