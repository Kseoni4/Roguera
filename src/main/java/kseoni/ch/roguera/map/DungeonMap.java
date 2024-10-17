package kseoni.ch.roguera.map;

import java.util.HashMap;

public class DungeonMap {

    private static HashMap<Integer, Room> rooms;

    private static int roomIdPointer = 0;

    static {
        rooms = new HashMap<>();
        initDungeon(1);
    }

    private static void initDungeon(int roomCount){
        rooms.put(0, new Room(0, 10, 15));
    }

    public static Room currentRoom(){
        return rooms.get(roomIdPointer);
    }
}
