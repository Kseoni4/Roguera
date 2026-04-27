package kseoni.ch.roguera.map.generator;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.map.Wall;
import kseoni.ch.roguera.utils.Convert;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class PhaseOneCreateRooms {

    private Random rnd;

    private MapGenerate mapGen;

    public PhaseOneCreateRooms(MapGenerate mapGen, Random random){
        rnd = random;
        this.mapGen = mapGen;
    }

    public HashMap<Integer, Room> createRooms(int roomCount){

        HashMap<Integer, Room> roomMap = new HashMap<>();

        for (int i = 0; i < roomCount; i++) {

            int x = rnd.nextInt(84);
            int y = rnd.nextInt(25);

            Position topLeftPosition = new Position(x, y);

            Room room = createRoom(i, topLeftPosition);
            roomMap.put(room.getRoomId(), room);
        }

        return roomMap;
    }

    private Room createRoom(int roomId, Position topLeftPosition) {
        int width = rnd.nextInt(5, 15) + 1;
        int height = rnd.nextInt(5, 15) + 1;

        Room room = Room.builder()
                .roomId(roomId)
                .width(width)
                .height(height)
                .roomLeftTopPosition(topLeftPosition)
                .doors(new HashMap<>())
                .build();

        room.setCells(createCellsForRoom(room));

/*
        int red = rnd.nextInt(0, 255);
        int green = rnd.nextInt(0, 255);
        int blue = rnd.nextInt(0, 255);

        room.getCells().values().forEach(
                cell -> cell.placeObject(new Wall(new TextSprite('.', new TextColor.RGB(red, green, blue))))
        );
*/

        mapGen.generateDebugLog(
                "Create room size "
                        .concat("[")
                        .concat(String.valueOf(width))
                        .concat(";")
                        .concat(String.valueOf(height))
                        .concat("]")
                        .concat(" from top left position ").concat(topLeftPosition.toString())
                        .concat(" with center in ").concat(room.getRoomCenter().toString())
                        .concat(" global ").concat(Convert.toGlobalPosition(room.getRoomLeftTopPosition(), room.getRoomCenter()).toString())
                        .concat(" with roomId ").concat(String.valueOf(roomId))
        );

        mapGen.generateDebugLog("Cells count: " + room.getCells().size());

        room.getCell(Position.ZERO.getRelativePosition(1, 1)).placeObject(new Wall(new TextSprite(Character.forDigit(roomId, Character.MAX_RADIX))));
        room.getCell(room.getRoomCenter()).placeObject(new Wall(new TextSprite('*', TextColor.ANSI.MAGENTA_BRIGHT)));

        return room;
    }

    private HashMap<Position, Cell> createCellsForRoom(Room room) {
        HashMap<Position, Cell> cells = new HashMap<>();

        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                Position pos = new Position(x, y);
                cells.put(pos, new Cell(pos));
            }
        }
        return cells;
    }
}
