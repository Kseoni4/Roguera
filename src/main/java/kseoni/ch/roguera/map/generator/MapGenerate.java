package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.ObjectPool;
import kseoni.ch.roguera.utils.RandomUtils;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.Getter;

import java.util.*;

public class MapGenerate {
    private final Random rnd;

    private HashMap<Integer, Room> tempRoomMap;

    private int worldXBound;

    private int worldYBound;

    @Getter
    private final boolean debugShowDungeonGenerate;

    public MapGenerate(int worldXBound, int worldYBound) {
        rnd = RandomUtils.getRandom();
        debugShowDungeonGenerate = Boolean.parseBoolean(SettingsLoader.getSettingValue("debug.show.dungeon-generate"));
        this.worldXBound = worldXBound;
        this.worldYBound = worldYBound;

    }

    public void setRndSeed(long seed) {
        RandomUtils.setSeed(seed);
    }


    public Map<Integer, Room> generateFloor(int roomCount) {
        generateDebugLog("==========GENERATE FLOOR============");

        generateDebugLog("Clearing object pool");
        ObjectPool.get().clearPool();

        // Phase one
        generateDebugLog("====Phase One====");
        tempRoomMap = new PhaseOneCreateRooms(this, rnd).createRooms(roomCount, worldXBound, worldYBound);

        //KeyInput.getWait();

        // Phase two
        if (roomCount > 1) {
            generateDebugLog("====Phase Two====");
            new PhaseTwoMergeRooms(this, tempRoomMap).mergeRooms();
        }

        //KeyInput.getWait();

        // Phase three
        if(tempRoomMap.size() > 1) {
            generateDebugLog("====Phase Three====");
            new PhaseThreeConnectClusters(this, tempRoomMap).connectRooms();
        }

        //KeyInput.getWait();

        if(!new MapValidator(new HashSet<>(tempRoomMap.values())).validateRooms()){
            generateDebugLog("!!! Floor is not valid !!!");
            return null;
        }

        // Phase four
        generateDebugLog("====Phase Four====");
        new PhaseFourCreateRoomStructure(this, tempRoomMap).createRoomStructure();
        generateDebugLog("==========GENERATION COMPLETE============");
        EventLoop.get().send(Event.raise("Generation complete"));
        return tempRoomMap;
    }

    public void generateDebugLog(String message){
        if(debugShowDungeonGenerate) {
            System.out.println(message);
        }
    }
}