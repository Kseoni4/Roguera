package com.rogurea.prototype_old;

import java.util.*;

public class Dungeon {

    static String[] GroundCreatureNames = {
            "Rat",
            "Goblin",
            "Orc",
            "Skelleton",
            "Zombie"
    };

    static String[] LootNames ={
            "Small sword",
            "Broke bow",
            "Copper knife",
            "Axe",
            "Paper"
    };

    static int RoomCounter = 1;

    static int RandomRoomCount = 10;

    static ArrayList<String> ItemNames = new ArrayList<>();

    static ArrayList<Item> Items = new ArrayList<>();

    public static ArrayList<Room> Rooms = new ArrayList<>();

    public static boolean Generate(){

        while(RoomCounter <= RandomRoomCount){
            CreateRoom();
        }

        ConnectRooms();

        return Rooms != null;
    }

    public static void CreateRoom() {

        if(RoomCounter < RandomRoomCount) {
            Rooms.add(new Room(RoomCounter++));
        }
        else if(RoomCounter == RandomRoomCount){
            Rooms.add(new Room(RoomCounter++, true));
        }
    }

    public static void ConnectRooms(){
        for(Room r : Rooms){
            if(!r.IsEndRoom)
                r.nextRoom = Rooms.get(Rooms.indexOf(r)+1);
        }
    }

    public static DungeonShop PlaceDungeonShop()
    {
        return new DungeonShop();
    }

    public static List<Creature> InsertCreatures(){
        List<Creature> Creatures = new ArrayList<>();

        Random r = new Random();

        while(Creatures.size() <  r.nextInt(5))
        {
            Creatures.add(new Mob(
                            GroundCreatureNames[r.nextInt(5)],
                            Mob.MobType.GROUND
                    )
            );
        }
        return Creatures;
    }

    public static ArrayList<Item> InsertLoot(){
        ArrayList<Item> Loot = new ArrayList<>();

        return Loot;
    }
}
