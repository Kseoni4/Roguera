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
import kseoni.ch.roguera.utils.RandomUtils;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MapGenerate {
    private final SecureRandom rnd;

    private HashMap<Integer, Room> tempRoomMap;

    public MapGenerate() {
        rnd = RandomUtils.getRandom();
    }

    public void setRndSeed(byte[] seed) {
        RandomUtils.setSeed(seed);
    }


    public Map<Integer, Room> generateFloor(int roomCount) {

        System.out.println("==========GENERATE FLOOR============");

        // Phase one
        System.out.println("====Phase One====");
        tempRoomMap = new PhaseOneCreateRooms(rnd).createRooms(roomCount);

        // Phase two
        if (roomCount > 1) {
            System.out.println("====Phase Two====");
            new PhaseTwoMergeRooms(tempRoomMap).mergeRooms();
        }

        // Phase three
        if(tempRoomMap.size() > 1) {
            System.out.println("====Phase Three====");
            new PhaseThreeConnectClusters(tempRoomMap).connectRooms();
        }

        // Phase four
        System.out.println("====Phase Four====");
        new PhaseFourCreateRoomStructure(tempRoomMap).createRoomStructure();

        System.out.println("==========GENERATION COMPLETE============");
        return tempRoomMap;
    }
}