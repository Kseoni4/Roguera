package com.rogurea.main.map;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobFactory;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.ItemGenerate;
import com.rogurea.main.mapgenerate.BaseGenerate;

import java.util.ArrayList;

public class Room {
    public byte NumberOfRoom;

    public byte X = 1;

    public byte Y = 1;

    public BaseGenerate.RoomSize roomSize;

    public boolean IsEndRoom = false;

    public ArrayList<Mob> RoomCreatures;

    public ArrayList<Item> RoomItems;

    public char[][] RoomStructure;

    public boolean IsRoomStructureGenerate = false;

    public Room nextRoom;

//    public DungeonShop dungeonShop;

    public Room(byte RoomCounter, byte x, byte y){
        MakeRoom(RoomCounter, x, y);
    }

//    public R_Room(int RoomCounter, DungeonShop DS){
//        NumberOfRoom = RoomCounter;
//        this.dungeonShop = DS;
//    }

    public Room(byte RoomCounter, boolean IsEndRoom, byte x, byte y){
        this.IsEndRoom = IsEndRoom;
        MakeRoom(RoomCounter, x, y);
    }

    private void MakeRoom(byte RoomCounter, byte x, byte y){
        this.NumberOfRoom = RoomCounter;

        this.X = x;

        this.Y = y;

        if(RoomCounter > 1)
            RoomCreatures = MobFactory.getMobs(NumberOfRoom);
        else
            RoomCreatures = new ArrayList<>(0);

        RoomStructure = new char[y][x];

        RoomItems = ItemGenerate.PutItemsIntoRoom(NumberOfRoom);
    }

    public Mob getMobFromRoom(Position pos){
        return RoomCreatures.stream().filter(
                 mob -> (mob.HisPosition.x == pos.x && mob.HisPosition.y == pos.y)
        ).findAny().orElse(null);
    }
}
