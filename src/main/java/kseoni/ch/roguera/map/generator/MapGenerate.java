package kseoni.ch.roguera.map.generator;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.map.Wall;
import kseoni.ch.roguera.utils.Convert;
import kseoni.ch.roguera.utils.ObjectPool;
import kseoni.ch.roguera.utils.RandomUtils;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.Getter;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MapGenerate {
    private final Random rnd;

    private HashMap<Integer, Room> tempRoomMap;

    @Getter
    private final boolean debugShowDungeonGenerate;

    public MapGenerate() {
        rnd = RandomUtils.getRandom();
        debugShowDungeonGenerate = Boolean.parseBoolean(SettingsLoader.getSettingValue("debug.show.dungeon-generate"));
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
        tempRoomMap = new PhaseOneCreateRooms(this, rnd).createRooms(roomCount);

        // Phase two
        if (roomCount > 1) {
            generateDebugLog("====Phase Two====");
            new PhaseTwoMergeRooms(this, tempRoomMap).mergeRooms();
        }

        // Phase three
        if(tempRoomMap.size() > 1) {
            generateDebugLog("====Phase Three====");
            new PhaseThreeConnectClusters(this, tempRoomMap).connectRooms();
        }

        if(!new MapValidator(new HashSet<>(tempRoomMap.values())).validateRooms()){
            generateDebugLog("!!! Floor is not valid !!!");
            return null;
        }

        // Phase four
        generateDebugLog("====Phase Four====");
        new PhaseFourCreateRoomStructure(this, tempRoomMap).createRoomStructure();
        generateDebugLog("==========GENERATION COMPLETE============");
        return tempRoomMap;
    }

    public void generateDebugLog(String message){
        if(debugShowDungeonGenerate)
            System.out.println(message);
    }
}