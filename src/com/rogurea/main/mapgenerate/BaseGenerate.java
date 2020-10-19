package com.rogurea.main.mapgenerate;

import com.rogurea.main.items.Item;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.map.Room;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;

public class BaseGenerate {

    public static Random random = new Random();

    public static char EmptyCell = ' ';

    public enum RoomSize {
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
                        12, 15, 16
                };
            }

            @Override
            public int[] GetHeightY() {
                return new int[] {
                        10, 12, 15
                };
            }
        },
        BIG{
            @Override
            public int[] GetWidghtX() {
                return new int[]{
                        18, 20, 23,
                };
            }

            @Override
            public int[] GetHeightY() {
                return new int[] {
                        19, 22, 23
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

    static Predicate<Room> ByPlayerCurrentRoom = R_Room -> R_Room.NumberOfRoom == Player.CurrentRoom;

    static Predicate<Room> ByFirstRoom = R_Room -> R_Room.NumberOfRoom == 1;

    public static void GenerateDungeon(int DungeonLenght){

        int r = 0;

        RoomSize[] roomSizes = RoomSize.values();

        for(int i = 0; i < DungeonLenght; i++){

            r = random.nextInt(roomSizes.length);

            int Height = roomSizes[2].GetHeightY()[random.nextInt(3)];
            int Widght = roomSizes[2].GetWidghtX()[random.nextInt(3)];

            if(i == DungeonLenght-1){
                Dungeon.Rooms.add(new Room(i+1, true, Widght, Height));
            }
            else
                Dungeon.Rooms.add(new Room(i+1, Widght, Height));

            Dungeon.Rooms.get(i).roomSize = roomSizes[2];

            MapEditor.FillSpaceWithEmpty(Dungeon.Rooms.get(i).RoomStructure);
        }
        ConnectRooms();
    }

    public static void ConnectRooms(){

        int i = 2;

        Dungeon.Rooms.sort(Comparator.comparingInt(value -> value.NumberOfRoom));

        for(Room room : Dungeon.Rooms){
            if(!room.IsEndRoom) {
                int finalI = i;
                room.nextRoom = Dungeon.Rooms
                        .stream()
                        .filter(r_room -> r_room.NumberOfRoom == finalI)
                        .findFirst()
                        .orElse(null);
            }
            i++;
        }
    }

    public static Room GetRoom(Dungeon.Direction direction) {
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

    static Room GetFromSet(Predicate<Room> predicate){
        return Dungeon.Rooms.stream().filter(predicate).findAny().orElse(null);
    }

    public static void GenerateRoom(Room room) throws IOException {

        char[][] CurrentRoom = room.RoomStructure;

        MapEditor.FillAllSpaceWithEmpty(CurrentRoom);

        Player.CurrentRoom = room.NumberOfRoom;

        int RoomLenghtY = CurrentRoom.length;

        int RoomLenghtX = CurrentRoom[0].length;

        System.out.println("CurrentRoom " + room.NumberOfRoom);

        System.out.println("Room dimensions = " + CurrentRoom.length + " " + CurrentRoom[0].length);

        try {
            if(TerminalView.terminal != null) {
                TerminalView.terminal.clearScreen();
                TerminalView.terminal.flush();
                System.out.println("Screen cleaned");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Border placed");

        System.out.println("Subshapes placed");

        Dungeon.CurrentRoom = CurrentRoom;

        room.RoomStructure = CurrentRoom;

        room.IsRoomStructureGenerate = true;
        if(room.roomSize == RoomSize.BIG) {
            GenerateRules.GenerationShapeByPoints(CurrentRoom);
        }

        MapEditor.PlaceDoors(room, CurrentRoom);

        int r = 3;

        for(Item i : room.RoomItems)
            CurrentRoom[2][(r++)] = i._model;

        System.out.println("Door placed");
    }

    public static void PutPlayerInDungeon(int x, int y, char[][] CurrentRoom){
        CurrentRoom[y][x] = Player.PlayerModel;
        Player.Pos.x = x;
        Player.Pos.y = y;
    }

    public static int GetCenterOfRoom(Room room){
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
