/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.mapgenerate;


import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Room;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.main.player.Player;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

public class RoomGenerate implements Serializable {

    public static Random random = null;

    public enum RoomSize {
        SMALL{
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        10, 11, 12
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[] {
                        9, 10, 11
                };
            }

            },
        MIDDLE{
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        12, 15, 16
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[] {
                        10, 12, 15
                };
            }
        },
        BIG{
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        18, 20, 23,
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[] {
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

    public static void GenerateRoomSequence(int RoomCount){
        random = GetRandom.RNGenerator;
        if(Dungeon.rooms.size() >= Dungeon.DungeonRoomsCount){
            Dungeon.DungeonRoomsCount += RoomCount;
        }

        RoomSize[] roomSizes = RoomSize.values();

        for(byte i = (byte) Dungeon.rooms.size(); i < Dungeon.DungeonRoomsCount; i++){

            int RandomRoomSize = random.nextInt(3);

            byte Height = roomSizes[RandomRoomSize].GetHeightY()[random.nextInt(3)];
            byte Widght = roomSizes[RandomRoomSize].GetWidghtX()[random.nextInt(3)];
            if(i == Dungeon.DungeonRoomsCount-1){
                Dungeon.rooms.add(new Room((i+1), Widght, Height, roomSizes[RandomRoomSize], true));
            }
            else
                Dungeon.rooms.add(new Room((i+1), Widght, Height, roomSizes[RandomRoomSize]));
        }
        ConnectRooms();
    }

    public static void ConnectRooms(){

        Dungeon.rooms.sort(Comparator.comparingInt(value -> value.RoomNumber));

        Dungeon.rooms.forEach(room -> {
            if(!room.isEndRoom)
                room.LinkRooms(Dungeon.rooms.get(room.RoomNumber));
        });

    }

    public static void GenerateRoomStructure(Room room){

        room.ClearCells();

        PGP pgp = new PGP();

        Player.CurrentRoom = (byte) room.RoomNumber;

        MapEditor.SetRoomForEdit(room);

        /*try {
            if(TerminalView.terminal != null) {
                TerminalView.terminal.clearScreen();
                TerminalView.terminal.flush();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }*/
       room.setCells(pgp.GenerateRoom(room.getCells()));

       room.makePerimeter();

       MapEditor.PlaceDoors(room, room.getCells(), pgp.ExitPoint);

       room.initFogController();
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
