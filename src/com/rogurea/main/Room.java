package com.rogurea.main;

import java.util.ArrayList;
import java.util.List;

public class Room {

    public int NumberOfRoom;

    public boolean IsEndRoom = false;

    public List<Creature> RoomCreatures;

    public Room nextRoom;

    public DungeonShop dungeonShop;

    public Room(int RoomCounter){
        NumberOfRoom = RoomCounter;
        if(RoomCounter > 1)
            RoomCreatures = Dungeon.InsertCreatures();
        else
            RoomCreatures = new ArrayList<>(0);
    }

    public Room(int RoomCounter, DungeonShop DS){
        NumberOfRoom = RoomCounter;
        this.dungeonShop = DS;
    }

    public Room(int RoomCounter, boolean IsEndRoom){
        this.NumberOfRoom = RoomCounter;
        RoomCreatures = Dungeon.InsertCreatures();
        this.IsEndRoom = IsEndRoom;
    }
}
