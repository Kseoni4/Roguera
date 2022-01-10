/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.mapgenerate;


import com.rogurea.Main;
import com.rogurea.base.Debug;
import com.rogurea.creatures.NPC;
import com.rogurea.gamelogic.MobFactory;
import com.rogurea.gamelogic.NPCLogicFactory;
import com.rogurea.gamemap.*;
import com.rogurea.items.Chest;
import com.rogurea.items.Gold;
import com.rogurea.resources.GetRandom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RoomGenerate implements Serializable {

    public static Random random = GetRandom.RNGenerator;

    public enum RoomSize {
        SMALL {
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        15, 16, 17
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[]{
                        11, 12, 14
                };
            }

        },
        MIDDLE {
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        17, 18, 19
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[]{
                        12, 15, 16
                };
            }
        },
        BIG {
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        18, 20, 23,
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[]{
                        19, 22, 23
                };
            }
        };

        public byte[] GetWidghtX() {
            return new byte[0];
        }

        public byte[] GetHeightY() {
            return new byte[0];
        }
    }

    public static void generateRoomSequence(int floorNumber) {
        random = GetRandom.RNGenerator;

        Debug.toLog("[ROOM_GENERATE] Generate room sequence on floor " + Dungeon.currentFloorNumber);

        Dungeon.rooms = new ArrayList<>();

        Dungeon.floors.add(new Floor());

        if(floorNumber > Dungeon.floors.size()){
            while(floorNumber >= Dungeon.floors.size()){
                Dungeon.floors.add(new Floor());
            }
        }

        RoomSize[] roomSizes = RoomSize.values();

        if (!Main.isNewGame()) {
            floorNumber = Dungeon.floors.size() - 1;
            Debug.toLog("[DUNGEON] new floor number by size: " + floorNumber);
        }

        for (byte i = (byte) Dungeon.rooms.size(); i < Dungeon.DungeonRoomsCount; i++) {

            int RandomRoomSize = random.nextInt(3);

            byte Height = roomSizes[RandomRoomSize].GetHeightY()[random.nextInt(3)];

            byte Widght = roomSizes[RandomRoomSize].GetWidghtX()[random.nextInt(3)];

            if((i + 1) == 10){
                Height = roomSizes[2].GetHeightY()[2];

                Widght = roomSizes[2].GetWidghtX()[2];
            }

            if (i >= Dungeon.DungeonRoomsCount - 1) {
                Dungeon.rooms.add(new Room((i + 1), Widght, Height, roomSizes[RandomRoomSize], true));
            } else {
                Dungeon.rooms.add(new Room((i + 1), Widght, Height, roomSizes[RandomRoomSize]));
            }
            Dungeon.floors.get(floorNumber).putRoomInFloor(Dungeon.rooms.get(i));
        }
        ConnectRooms();
    }

    public static void ConnectRooms() {

        Dungeon.rooms.sort(Comparator.comparingInt(value -> value.roomNumber));

        Dungeon.rooms.forEach(room -> {
            if (!room.isEndRoom)
                room.LinkRooms(Dungeon.rooms.get(room.roomNumber));
            else
                room.LinkRooms(Dungeon.rooms.get(0));
        });
    }

    public static void generateRoomStructure(Room room) {

        Debug.toLog("[GENERATE_ROOM] Generate room: " + room.roomNumber + "(floor " + room.floorNumber + ")");

        room.ClearCells();

        room.getObjectsSet().clear();

        PGP pgp = new PGP();

        Dungeon.player.setCurrentRoom((byte) room.roomNumber);

        MapEditor.SetRoomForEdit(room);

        room.setCells(pgp.generateRoom(room.getCells()));

        room.makePerimeter();

        room.setPositionsInsidePerimeter();

        MapEditor.PlaceDoors(room, room.getCells(), pgp.ExitPoint);

        int randomChestCount = ThreadLocalRandom.current().nextInt(0, 2);

        for (int i = 0; i < randomChestCount; i++) {
            Position randomPos = room.positionsInsidePerimeter.get(random.nextInt(room.positionsInsidePerimeter.size()));

            while (isWallsOnSides(room.getCell(randomPos).getCellsAround())) {
                randomPos = room.positionsInsidePerimeter.get(random.nextInt(room.positionsInsidePerimeter.size()));
            }
            MapEditor.setIntoCell(new Chest(), randomPos);
        }

        int randomGoldCount = ThreadLocalRandom.current().nextInt(0,6);

        for(int i = 0; i < randomGoldCount;i++) {

            MapEditor.setIntoCell(new Gold(),
                    room.positionsInsidePerimeter.get(
                            random.nextInt(room.positionsInsidePerimeter.size()
                            )
                    )
            );
        }

        if (room.width >= 12 && room.height > 12) {
           placeMobs(room);
        }

        placeTrader(room);

        room.initFogController();
    }

    private static boolean isWallsOnSides(Cell[] cells){
        return Arrays.stream(cells).filter(Cell::isWall).count() > 1;
    }

    private static void placeTrader(Room room){
        Position randomPos = room.positionsInsidePerimeter.get(random.nextInt(room.positionsInsidePerimeter.size()));

        while(isWallsOnSides(room.getCell(randomPos).getCellsAround())) {
            randomPos = room.positionsInsidePerimeter.get(random.nextInt(room.positionsInsidePerimeter.size()));
        }

        room.getCell(randomPos).putIntoCell(new NPC("Trader", NPCLogicFactory.getTraderLogic()));
}

    private static void placeMobs(Room room){
        if(!(Dungeon.getCurrentRoom().isEndRoom)) {
            int randomMobCount = Math.min(ThreadLocalRandom.current().nextInt(0, Dungeon.getCurrentFloor().get().getFloorNumber()+1),5);

            for (int i = 0; i < randomMobCount; i++) {
                MapEditor.setIntoCell(MobFactory.newMob(), room.positionsInsidePerimeter.get(
                        random.nextInt(room.positionsInsidePerimeter.size()
                        )
                ));
            }
        } else {
            placeBoss(room);
        }
    }

    private static void placeBoss(Room room){
        MapEditor.setIntoCell(MobFactory.newBoss(), room.positionsInsidePerimeter.get(
                random.nextInt(room.positionsInsidePerimeter.size())
        ));
    }
    /*

    private static void BuildSubRooms(char[][] CurrentRoom, byte y, RoomSize roomSize){
        byte x = FindLeftBorder(CurrentRoom, y);

        int depth = 1;

        if(roomSize == RoomSize.BIG)
            depth = 2;

        SubdivideZone(CurrentRoom, new Position(x,y), depth);
    }

    private static byte FindLeftBorder(char[][] CurrentRoom, byte y){
        byte x = (byte) (CurrentRoom[0].length/2);
        while(Scans.CheckWall(CurrentRoom[y][x])){
            x--;
        }
        return x;
    }
*/

    /*
    private static void SubdivideZone(char[][] CurrentRoom, Position startposition, int depth){

        MapEditor.DrawDirection drawDirection = depth % 2 == 0 ? MapEditor.DrawDirection.RIGHT : MapEditor.DrawDirection.UP;

        int xshift = drawDirection == MapEditor.DrawDirection.RIGHT ? 1 : 0;

        int yshift = drawDirection == MapEditor.DrawDirection.UP ? 1 : 0;

        int lenght = FindLenght(CurrentRoom, drawDirection, new Position(startposition.x+xshift, startposition.y-yshift));

        MapEditor.InsertShapeLine(CurrentRoom, drawDirection, lenght, startposition);

        depth--;
        if(depth > 0)
            SubdivideZone(CurrentRoom, new Position(lenght, startposition.y), depth);

        if(drawDirection == MapEditor.DrawDirection.RIGHT){
            xshift = lenght;
            MapEditor.setIntoCell(CurrentRoom, MapEditor.EmptyCell, new Position(lenght-3,startposition.y));
        }
        else{
            yshift = lenght;
            MapEditor.setIntoCell(CurrentRoom, MapEditor.EmptyCell, new Position(startposition.x,lenght-3));
        }

        PlaceCorners(CurrentRoom, drawDirection, startposition, new Position(startposition.x+xshift, startposition.y-yshift));

    }

    private static int FindLenght(char[][] CurrentRoom, MapEditor.DrawDirection direction, Position startpos){
        int l = 0;
        try {
            while(Scans.CheckWall(CurrentRoom[startpos.y][startpos.x])){
                if(direction == MapEditor.DrawDirection.RIGHT)
                    startpos.x++;
                else if (startpos.y > 0){
                    startpos.y--;
                }
                else{
                    break;
                }
                l++;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            Debug.log("ERROR: Subdivide generation failed, index (y:" + startpos.y + ";x:" + startpos.x+") is out of bounds");
        }

        return l+1;
    }

    private static void PlaceCorners(char[][] CurrentRoom, MapEditor.DrawDirection drawDirection, Position startposition, Position endposition){
        if (drawDirection == MapEditor.DrawDirection.RIGHT) {
            MapEditor.setIntoCell(CurrentRoom, GameResources.GetModel("LRCenter"), startposition);
            MapEditor.setIntoCell(CurrentRoom, GameResources.GetModel("RLCenter"), endposition);
        }
        else{
            MapEditor.setIntoCell(CurrentRoom, GameResources.GetModel("BCenter"), startposition);
            MapEditor.setIntoCell(CurrentRoom, GameResources.GetModel("TCenter"), endposition);
        }
    }*/
}
