package com.rogurea.main.map;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobFactory;
import com.rogurea.main.creatures.NPC;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.items.Item;
import com.rogurea.main.mapgenerate.BaseGenerate;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    public byte NumberOfRoom;

    public byte X = 1;

    public byte Y = 1;

    public transient BaseGenerate.RoomSize roomSize;

    public boolean IsEndRoom = false;

    public ArrayList<Mob> RoomCreatures;

    public NPC RoomNPC;

    public ArrayList<Item> RoomItems;

    public char[][] RoomStructure;

    public boolean IsRoomStructureGenerate = false;

    public transient Room nextRoom;

    public Shop dungeonShop;

    public Room(byte RoomCounter, byte x, byte y, BaseGenerate.RoomSize roomSize){
        this.roomSize = roomSize;
        MakeRoom(RoomCounter, x, y);
    }

    public Room(byte RoomCounter, boolean IsEndRoom, byte x, byte y, BaseGenerate.RoomSize roomSize){
        this.IsEndRoom = IsEndRoom;
        this.roomSize = roomSize;
        MakeRoom(RoomCounter, x, y);
    }

    private void MakeRoom(byte RoomCounter, byte x, byte y){

        this.NumberOfRoom = RoomCounter;

        Debug.log("MAKE ROOM: #" + NumberOfRoom);

        Debug.log("MAKE ROOM: SIZE X = " + x + "; Y = " + y + ";");

        this.X = x;

        this.Y = y;

        if(RoomCounter > 1)
            RoomCreatures = MobFactory.getMobs(this);
        else {
            RoomCreatures = new ArrayList<>(0);
        }

        if(IsEndRoom){
            dungeonShop = new Shop(new Position(1,2));
            RoomNPC = dungeonShop.shopOwner;
        }

        RoomStructure = new char[y][x];

        RoomItems = new ArrayList<>();

        Debug.log("MAKE ROOM: #" + NumberOfRoom + " IS ENDED");
    }

    public Mob getMobFromRoom(Position pos){
        return RoomCreatures.stream().filter(
                 mob -> (mob.HisPosition.x == pos.x && mob.HisPosition.y == pos.y)
        ).findAny().orElse(null);
    }
}
