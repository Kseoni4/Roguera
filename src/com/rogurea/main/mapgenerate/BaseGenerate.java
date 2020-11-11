package com.rogurea.main.mapgenerate;

import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.map.Room;
import com.rogurea.main.view.viewblocks.PlayerInfoBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;

public class BaseGenerate {

    public static final Random random = new Random();

    public enum RoomSize {
        SMALL{
            @Override
            public byte[] GetWidghtX() {
                return new byte[]{
                        3, 5, 10
                };
            }

            @Override
            public byte[] GetHeightY() {
                return new byte[] {
                        3, 5, 10
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

    static final Predicate<Room> ByPlayerCurrentRoom = R_Room -> R_Room.NumberOfRoom == Player.CurrentRoom;

    static final Predicate<Room> ByFirstRoom = R_Room -> R_Room.NumberOfRoom == 1;

    public static void GenerateDungeon(int DungeonLenght){

        RoomSize[] roomSizes = RoomSize.values();

        for(byte i = 0; i < DungeonLenght; i++){

            byte Height = roomSizes[2].GetHeightY()[random.nextInt(3)];
            byte Widght = roomSizes[2].GetWidghtX()[random.nextInt(3)];

            if(i == DungeonLenght-1){
                Dungeon.Rooms.add(new Room((byte) (i+1), true, Widght, Height));
            }
            else
                Dungeon.Rooms.add(new Room((byte) (i+1), Widght, Height));

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
            case NEXT, BACK -> {
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

    public static void GenerateRoom(Room room) {

        char[][] CurrentRoom = room.RoomStructure;

        Dungeon.CurrentRoomCreatures = new ArrayList<>();

        MapEditor.FillAllSpaceWithEmpty(CurrentRoom);

        Player.CurrentRoom = room.NumberOfRoom;

        try {
            if(TerminalView.terminal != null) {
                TerminalView.terminal.clearScreen();
                TerminalView.terminal.flush();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        room.IsRoomStructureGenerate = true;

        if(room.roomSize == RoomSize.BIG) {
            PointGenerateProcedure.GenerationShapeByPoints(CurrentRoom);
        }

        MapEditor.PlaceDoors(room, CurrentRoom);

        int r = 3;

        for(Item i : room.RoomItems)
            CurrentRoom[2][(r++)] = i._model;

        MapEditor.PlaceMobs(room, CurrentRoom);

        Dungeon.CurrentRoom = CurrentRoom;

        room.RoomStructure = CurrentRoom;
    }

    public static void PutPlayerInDungeon(int x, byte y, char[][] CurrentRoom){
        CurrentRoom[y][x] = Player.PlayerModel;
        Player.Pos.x = (byte) x;
        Player.Pos.y = y;
        PlayerInfoBlock.roomSize = Dungeon.GetCurrentRoom().roomSize;

        TerminalView.ReDrawAll();
    }

    public static int GetCenterOfRoom(Room room){
        return Math.floorDiv(room.RoomStructure[0].length, 2);
    }
}
