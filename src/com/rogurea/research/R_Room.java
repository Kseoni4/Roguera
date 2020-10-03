package com.rogurea.research;

import java.util.ArrayList;
import java.util.List;

public class R_Room {
    public int NumberOfRoom;

    public int X = 1;
    public int Y = 1;

    public boolean IsEndRoom = false;

    public ArrayList<R_Mob> RoomCreatures;

    public char[][] RoomStructure;

    public R_Room nextRoom;

//    public DungeonShop dungeonShop;

    public R_Room(int RoomCounter, int x, int y){
        NumberOfRoom = RoomCounter;
        this.X = x;
        this.Y = y;
        if(RoomCounter > 1)
            RoomCreatures = R_MobFactory.getMobs();
        else
            RoomCreatures = new ArrayList<>(0);
        RoomStructure = new char[x][y];
    }

//    public R_Room(int RoomCounter, DungeonShop DS){
//        NumberOfRoom = RoomCounter;
//        this.dungeonShop = DS;
//    }

    public R_Room(int RoomCounter, boolean IsEndRoom, int x, int y){
        this.NumberOfRoom = RoomCounter;
        RoomCreatures = R_MobFactory.getMobs();
        this.X = x;
        this.Y = y;
        this.IsEndRoom = IsEndRoom;
        RoomStructure = new char[x][y];
    }
}
