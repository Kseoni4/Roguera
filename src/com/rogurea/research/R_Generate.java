package com.rogurea.research;

import com.googlecode.lanterna.Symbols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

public class R_Generate {

    public static Random random = new Random();

    public static char EmptyCell = ' ';

    enum RoomSize {
        SMALL{
            @Override
            public int[] GetWidghtX() {
                return new int[]{
                        3, 5, 10
                };
            }

            @Override
            public int[] GetHeightY() {
                return new int[] {
                        3, 5, 10
                };
            }

            },
        MIDDLE{

            @Override
            public int[] GetWidghtX() {
                return new int[]{
                        12, 15, 22
                };
            }

            @Override
            public int[] GetHeightY() {
                return new int[] {
                        10, 15, 20
                };
            }
        };

        public int[] GetWidghtX() {
            return new int[0];
        }

        public int[] GetHeightY() {
            return new int[0];
        }
    }

    static Predicate<R_Room> ByPlayerCurrentRoom = R_Room -> R_Room.NumberOfRoom == R_Player.CurrentRoom;

    static Predicate<R_Room> ByFirstRoom = R_Room -> R_Room.NumberOfRoom == 1;

    public static void GenerateDungeon(int Height, int Widght, int DungeonLenght){

        int r = 0;

        RoomSize[] roomSizes = RoomSize.values();

        for(int i = 0; i < DungeonLenght; i++){

            r = random.nextInt(roomSizes.length);

            Height = roomSizes[r].GetHeightY()[random.nextInt(3)];
            Widght = roomSizes[r].GetWidghtX()[random.nextInt(3)];


            if(i == DungeonLenght-1){
                R_Dungeon.Rooms.add(new R_Room(i+1, true, Widght, Height));
            }
            else
                R_Dungeon.Rooms.add(new R_Room(i+1, Widght, Height));

            R_Dungeon.Rooms.get(i).roomSize = roomSizes[r];

            MapEditor.PlaceCorners(R_Dungeon.Rooms.get(i).RoomStructure);
            MapEditor.FillSpaceWithEmpty(R_Dungeon.Rooms.get(i).RoomStructure);
        }
        ConnectRooms();
    }

    public static void ConnectRooms(){

        int i = 2;

        R_Dungeon.Rooms.sort(Comparator.comparingInt(value -> value.NumberOfRoom));

        for(R_Room room : R_Dungeon.Rooms){
            if(!room.IsEndRoom) {
                int finalI = i;
                room.nextRoom = R_Dungeon.Rooms
                        .stream()
                        .filter(r_room -> r_room.NumberOfRoom == finalI)
                        .findFirst()
                        .orElse(null);
            }
            i++;
        }
    }

    public static R_Room GetRoom(R_Dungeon.Direction direction) {
        switch (direction) {
            case NEXT -> {
                return GetFromSet(ByPlayerCurrentRoom);
            }
            case BACK -> {
                return GetFromSet(ByPlayerCurrentRoom);
            }
            case FIRST -> {
                return GetFromSet(ByFirstRoom);
            }
        }
        return null;
    }

    static R_Room GetFromSet(Predicate<R_Room> predicate){
        return R_Dungeon.Rooms.stream().filter(predicate).findAny().orElse(null);
    }

    public static void GenerateRoom(R_Room room){

        char[][] CurrentRoom = room.RoomStructure;

        R_Player.CurrentRoom = room.NumberOfRoom;

        int RoomLenghtY = CurrentRoom.length;

        int RoomLenghtX = CurrentRoom[0].length;

        System.out.println("CurrentRoom " + room.NumberOfRoom);

        System.out.println("Room dimensions = " + CurrentRoom.length + " " + CurrentRoom[0].length);

        try {
            if(T_View.terminal != null) {
                T_View.terminal.clearScreen();
                T_View.terminal.flush();
                System.out.println("Screen cleaned");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        MapEditor.InsertShapeFlat(CurrentRoom, MapEditor.DrawRectangle(RoomLenghtY,
                RoomLenghtX), 0, 0);

        System.out.println("Border placed");

       MapEditor.PlaceSubShapes(room.roomSize, CurrentRoom);

       System.out.println("Subshapes placed");

        MapEditor.PlaceDoors(room, CurrentRoom);

        System.out.println("Door placed");

        R_Dungeon.CurrentRoom = CurrentRoom;

        room.RoomStructure = CurrentRoom;

        room.IsRoomStructureGenerate = true;
    }

    public static void PutPlayerInDungeon(int x, int y, char[][] CurrentRoom){
        CurrentRoom[y][x] = R_Player.Player;
        R_Player.Pos.x = x;
        R_Player.Pos.y = y;
    }

    public static int GetCenterOfRoom(R_Room room){
        return Math.floorDiv(room.RoomStructure[0].length, 2);
    }


    /*

    static void PlaceMobs(R_Room room, char[][] CurrentRoom){

        for(R_Mob mob : room.RoomCreatures){

            int y = CurrentRoom[0].length/2-random.nextInt(2)+1;

            int x = random.nextInt(CurrentRoom.length);

            System.out.println("MobPosition" + " " + "x:" + x + " " + "y:" + y);

            if(Scans.CheckWall(CurrentRoom[x][y])) {
                CurrentRoom[x][y] = mob.getCreatureSymbol();
                mob.setMobPosition(x,y);
                R_Dungeon.CurrentRoomCreatures.put((R_Mob) mob, mob.id);
            }
        }
    }
*/

}
