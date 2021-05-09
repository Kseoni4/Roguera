package com.rogurea.main.mapgenerate;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.SavingSystem;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.player.Player;
import com.rogurea.main.map.Room;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.UI.PlayerInfoBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseGenerate implements Serializable {

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

    static final Predicate<Room> ByPlayerCurrentRoom = R_Room -> R_Room.NumberOfRoom == Player.CurrentRoom;

    static final Predicate<Room> ByFirstRoom = R_Room -> R_Room.NumberOfRoom == 1;

    public static void GenerateDungeon(int DungeonLenght){

        random = GetRandom.RNGenerator;

        AtomicInteger RoomNum = new AtomicInteger(1);

        if(Dungeon.Rooms.size() >= Dungeon.CurrentDungeonLenght){
            Dungeon.CurrentDungeonLenght += DungeonLenght;
        }

        Integer DecadeRoom = 0;

        if(Formula.RoomsForMobLevelUp != null){
            DecadeRoom = Formula.RoomsForMobLevelUp.get(Formula.RoomsForMobLevelUp.size()-1);
        }

        if(Dungeon.CurrentDungeonLenght >= DecadeRoom) {
            Formula.RoomsForMobLevelUp = (ArrayList<Integer>) Stream.generate(() -> RoomNum.addAndGet(3)).limit(Dungeon.CurrentDungeonLenght).collect(Collectors.toList());
            Formula.RoomsForMobLevelUp.forEach(s -> System.out.print(s + " "));
        }

        RoomSize[] roomSizes = RoomSize.values();

        for(byte i = (byte) Dungeon.Rooms.size(); i < Dungeon.CurrentDungeonLenght; i++){

            int RandomRoomSize = random.nextInt(3);

            byte Height = roomSizes[RandomRoomSize].GetHeightY()[random.nextInt(3)];
            byte Widght = roomSizes[RandomRoomSize].GetWidghtX()[random.nextInt(3)];
            if(i == Dungeon.CurrentDungeonLenght-1){
                Dungeon.Rooms.add(new Room((byte) (i+1), true, Widght, Height,roomSizes[RandomRoomSize]));
            }
            else
                Dungeon.Rooms.add(new Room((byte) (i+1), Widght, Height, roomSizes[RandomRoomSize]));

            MapEditor.FillSpaceWithEmpty(Dungeon.Rooms.get(i).RoomStructure);

            String message = "Create Room " + i + " Height: " + Height + " Width: " + Widght;
            Debug.log("GENERATE: Make Dungeon: " + message);
        }
        ConnectRooms();
    }

    public static void ConnectRooms(){

        int i = 2;

        Dungeon.Rooms.sort(Comparator.comparingInt(value -> value.NumberOfRoom));

        Debug.log("GENERATE: Connecting rooms");

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

    public static Room GetFromSet(Predicate<Room> predicate){
        return Dungeon.Rooms.stream().filter(predicate).findAny().orElse(null);
    }

    public static void GenerateRoom(Room room) {

        PGP pgp = new PGP();

        char[][] CurrentRoom = room.RoomStructure;

        MapEditor.FillAllSpaceWithEmpty(CurrentRoom);

        Player.CurrentRoom = room.NumberOfRoom;

        Debug.log("GENERATE: " + "Room " + room.NumberOfRoom + " is not generate, processing...");

        try {
            if(TerminalView.terminal != null) {
                TerminalView.terminal.clearScreen();
                TerminalView.terminal.flush();
            }
        }
        catch (IOException e){
            Debug.log(e.getMessage());
            e.printStackTrace();
        }

        room.IsRoomStructureGenerate = true;

/*        if(room.roomSize == RoomSize.BIG) {
        }*/

        CurrentRoom = pgp.GenerateRoom(CurrentRoom);

        ArrayList<Position> positions = getPositionsList(CurrentRoom, pgp.ExitPoint);

        MapEditor.PlaceDoors(room, CurrentRoom, pgp.ExitPoint);

        if(room.roomSize == RoomSize.BIG)
            BuildSubRooms(CurrentRoom, (byte) (pgp.ExitPoint.y/2), room.roomSize);

        MapEditor.PlaceMobs(room, CurrentRoom, positions);

        Dungeon.CurrentRoom = CurrentRoom;

        room.RoomStructure = CurrentRoom;

        if(room.dungeonShop != null)
            MapEditor.InsertShapeFlat(room.RoomStructure, room.dungeonShop.getShopStructure(), room.dungeonShop.ShopPosition);

        for(Mob mob : room.RoomCreatures){
            mob.GetAllInfo();
        }

        Debug.log("GENERATE: " + " Room " + room.NumberOfRoom + " generate has completed");

    }

    public static void PutPlayerInDungeon(int x, byte y, char[][] CurrentRoom){
        CurrentRoom[y][x] = Player.PlayerModel;
        Player.Pos.x = (byte) x;
        Player.Pos.y = y;
        PlayerInfoBlock.roomSize = Dungeon.GetCurrentRoom().roomSize;

        int HighestRoomNum = Player.playerStatistics.HighestRoomNumber;

        Player.playerStatistics.HighestRoomNumber = Math.max(HighestRoomNum, Dungeon.GetCurrentRoom().NumberOfRoom);

        Debug.log("GENERATE: Place player into position " + Player.GetPlayerPosition().toString());

        try {
            SavingSystem.saveGame();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Draw.clear();

        TerminalView.ReDrawAll(new IViewBlock[]{});
    }

    public static int GetCenterOfRoom(Room room){
        return Math.floorDiv(room.RoomStructure[0].length, 2);
    }

    private static ArrayList<Position> getPositionsList(char[][] CurrentRoom, Position ExitPoint) {

        ArrayList<Position> PositionsList = new ArrayList<>();

        if(CurrentRoom == null){
            System.out.println("You need to generate room first!");
            return null;
        }

        Position StartPosition = new Position(1,1);

        int y = StartPosition.y, x = StartPosition.x;

        for(int i = y; i < CurrentRoom.length;) {
            for (int j = x; j < CurrentRoom[0].length;) {
                if (Scans.CheckWall(CurrentRoom[i][j])) {
                    PositionsList.add(new Position(j, i));

                    j++;
                } else if(i == ExitPoint.y-1) {
                    break;

                } else if (!Scans.CheckWall(CurrentRoom[i + 1][StartPosition.x])) {

                    j = StartPosition.x;

                    while (!Scans.CheckWall(CurrentRoom[i + 1][j]))
                        j++;

                    StartPosition.y = i;

                    StartPosition.x = j;
                } else {
                    i++;

                    j = StartPosition.x;
                }
            }
            break;
        }
        return PositionsList;
    }

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
    }
}
