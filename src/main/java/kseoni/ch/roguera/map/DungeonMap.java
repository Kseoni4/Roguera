package kseoni.ch.roguera.map;

import java.util.HashMap;

public class DungeonMap {

    private static final int ROOM_COUNT_BASE = 1;

    private HashMap<Integer, Room> rooms;

    private int roomIdPointer = 0;

    private static DungeonMap INSTANCE;

    private DungeonMap(){
        rooms = new HashMap<>();
        initDungeon(ROOM_COUNT_BASE);
    }

    public static DungeonMap get(){
        if (INSTANCE == null) {
            INSTANCE = new DungeonMap();
        }
        return INSTANCE;
    }

    private void initDungeon(int roomCount){
        for(int i = 0; i < roomCount; i++) {
            rooms.put(i, new Room(i, 20, 15));
        }
    }

    public Room currentRoom(){
        return rooms.get(roomIdPointer);
    }
}
