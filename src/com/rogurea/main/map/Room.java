package com.rogurea.main.map;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobFactory;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.ItemGenerate;
import com.rogurea.main.mapgenerate.BaseGenerate;

import java.util.ArrayList;

public class Room {
    public int NumberOfRoom;

    public int X = 1;
    public int Y = 1;

    public BaseGenerate.RoomSize roomSize;

    public boolean IsEndRoom = false;

    public ArrayList<Mob> RoomCreatures;

    public ArrayList<Item> RoomItems;

    public char[][] RoomStructure;

    public boolean IsRoomStructureGenerate = false;

    public Room nextRoom;

//    public DungeonShop dungeonShop;

    public Room(int RoomCounter, int x, int y){
        NumberOfRoom = RoomCounter;
        this.X = x;
        this.Y = y;
        if(RoomCounter > 1)
            RoomCreatures = MobFactory.getMobs();
        else
            RoomCreatures = new ArrayList<>(0);
        RoomStructure = new char[y][x];
        RoomItems = ItemGenerate.PutItemsIntoRoom();
    }

//    public R_Room(int RoomCounter, DungeonShop DS){
//        NumberOfRoom = RoomCounter;
//        this.dungeonShop = DS;
//    }

    public Room(int RoomCounter, boolean IsEndRoom, int x, int y){
        this.NumberOfRoom = RoomCounter;
        RoomCreatures = MobFactory.getMobs();
        this.X = x;
        this.Y = y;
        this.IsEndRoom = IsEndRoom;
        RoomStructure = new char[y][x];
    }
}
