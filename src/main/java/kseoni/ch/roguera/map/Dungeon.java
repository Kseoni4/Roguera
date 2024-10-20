package kseoni.ch.roguera.map;

import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.Getter;

import java.util.HashMap;
import java.util.Properties;

public class Dungeon {

    @Getter
    private final int worldXBound;

    @Getter
    private final int worldYBound;

    private static final int ROOM_COUNT_BASE = 2;

    private static int FLOOR_NUMBER_COUNTER = 0;

    private int currentFloorPointer = 1;

    private final HashMap<Integer, Floor> floors;

    private static Dungeon INSTANCE;

    private Dungeon(){
        worldXBound = Integer.parseInt(SettingsLoader.getSettingValue("dungeon.world.size.xbound"));
        worldYBound = Integer.parseInt(SettingsLoader.getSettingValue("dungeon.world.size.ybound"));
        floors = new HashMap<>();
        initDungeon();
    }

    public static Dungeon get(){
        if (INSTANCE == null) {
            INSTANCE = new Dungeon();
        }
        return INSTANCE;
    }

    private void initDungeon(){
        int floorNumber = ++FLOOR_NUMBER_COUNTER;
        floors.put(floorNumber, new Floor(ROOM_COUNT_BASE, floorNumber));
    }

    public Floor currentFloor(){
        return floors.get(currentFloorPointer);
    }
}
