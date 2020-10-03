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

    public static R_Room GetRoom(R_Dungeon.Direction direction) {
        switch (direction) {
            case NEXT -> {
                return GetFromSet(ByCurrentRoom);
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

    static Predicate<R_Room> ByPlayerCurrentRoom = R_Room -> R_Room.NumberOfRoom == R_Player.CurrentRoom;

    static Predicate<R_Room> ByCurrentRoom = R_Room -> R_Dungeon.CurrentRoom == R_Dungeon.Rooms.get(R_Room);

    static Predicate<R_Room> ByFirstRoom = R_Room -> R_Room.NumberOfRoom == 1;

    static R_Room GetFromSet(Predicate ByPredicate){
        return (R_Room) R_Dungeon.Rooms.keySet()
                .stream()
                .filter(ByPredicate)
                .findAny()
                .orElse(null);
    }

    public static int GetCenterOfRoom(){
        return Math.floorDiv(R_Dungeon.CurrentRoom.length, 2);
    }

    public static boolean CheckExit(char c){
        if(CheckWall(c)){
            return c == Symbols.ARROW_DOWN;
        }
        return false;
    }

    public static boolean CheckBack(char c){
        if(CheckWall(c)){
            return c == Symbols.ARROW_UP;
        }
        return false;
    }

    public static boolean CheckWall(char c){
        if (c == Symbols.DOUBLE_LINE_HORIZONTAL)
            return false;
        if (c == Symbols.DOUBLE_LINE_VERTICAL)
            return false;
        if (c == Symbols.DOUBLE_LINE_TOP_LEFT_CORNER)
            return false;
        if (c == Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER)
            return false;
        if (c == Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER)
            return false;
        if (c == Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER)
            return false;
        return  true;
    }

    static void PlaceBorders(char[][] CurrentRoom){
        CurrentRoom[0][0] = Symbols.DOUBLE_LINE_TOP_LEFT_CORNER;
        CurrentRoom[CurrentRoom.length-1][CurrentRoom[0].length-1] = Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER;
        CurrentRoom[0][CurrentRoom[0].length-1] = Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER;
        CurrentRoom[CurrentRoom.length-1][0] = Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
        for (int i = 1; i < CurrentRoom[0].length-1; i++) {
            CurrentRoom[0][i] = Symbols.DOUBLE_LINE_VERTICAL;
        }
        for (int i = 1; i < CurrentRoom.length - 1; i++) {
            CurrentRoom[i][CurrentRoom[0].length - 1] = Symbols.DOUBLE_LINE_HORIZONTAL;
        }
        for (int i = 1; i < CurrentRoom[0].length - 1; i++) {
            CurrentRoom[CurrentRoom.length - 1][i] = Symbols.DOUBLE_LINE_VERTICAL;
        }
        for (int i = 1; i < CurrentRoom.length-1; i++) {
            CurrentRoom[i][0] = Symbols.DOUBLE_LINE_HORIZONTAL;
        }
    }

    static void PlaceDoors(R_Room room, char[][] CurrentRoom){
        if(!room.IsEndRoom)
            CurrentRoom[CurrentRoom.length/2][CurrentRoom[0].length-1] = Symbols.ARROW_DOWN;
        if(room.NumberOfRoom > 1)
            CurrentRoom[CurrentRoom.length/2][0] = Symbols.ARROW_UP;
    }

    static void FillSpaceWithEmpty(char[][] CurrentRoom){
        for (int i = 0; i < CurrentRoom.length; i++) {
            for (int j = 0; j < CurrentRoom[0].length; j++) {
                if (CheckWall(CurrentRoom[i][j])) {
                    CurrentRoom[i][j] = EmptyCell;
                }
            }
        }
    }

    static char[][] GenerateSubRoom(){
        int LengthY, LengthX,
                LengthXY,
                PointTopLeftX, PointTopY,
                PointBottomY, PointTopRightX;

        LengthY = (int) Math.pow(random.nextInt(1)+2,2);
        LengthX = LengthY;

        PointBottomY = LengthY;
        PointTopY = 0;

        PointTopRightX = LengthX;
        PointTopLeftX = 0;

        LengthXY = LengthY;

        char[][] BufferSubRoom = new char[LengthY][LengthX];
        for(int y = 0; y < BufferSubRoom.length; y++){
            BufferSubRoom[y][0] = Symbols.DOUBLE_LINE_VERTICAL;
            BufferSubRoom[y][LengthX-1] = Symbols.DOUBLE_LINE_VERTICAL;
            System.out.print(BufferSubRoom[y][0]);
        }
        System.out.print('\n');
        for(int x = 0; x < BufferSubRoom[0].length; x++){
            BufferSubRoom[0][x] = Symbols.DOUBLE_LINE_HORIZONTAL;
            BufferSubRoom[LengthY-1][x] = Symbols.DOUBLE_LINE_HORIZONTAL;
            System.out.print(BufferSubRoom[0][x]);
        }
        System.out.print('\n');
        FillSpaceWithEmpty(BufferSubRoom);
        BufferSubRoom[PointTopY][PointTopLeftX] = Symbols.DOUBLE_LINE_T_RIGHT;
        BufferSubRoom[PointBottomY-1][PointTopLeftX] = Symbols.DOUBLE_LINE_T_RIGHT;

        BufferSubRoom[PointTopY][PointTopRightX-1] = Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
        BufferSubRoom[PointBottomY-1][PointTopRightX-1] = Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER;

        return BufferSubRoom;

    }

    static void PlaceSubRoom(char[][] SubRoom, char[][] CurrentRoom){
        int SubRoomLenghtY = SubRoom.length;
        int SubRoomLenghtX = SubRoom[0].length;

        int RoomPointY = ((CurrentRoom.length/SubRoomLenghtY));
        int RoomPointX = 0;

        for(int y = 0; y < SubRoomLenghtY; y++){
            for(int x = 0; x < SubRoomLenghtX; x++){
                System.out.println("x:" + x + " " + "y:" + y);
                System.out.println("RoomPointX:" + RoomPointX + " " + "RoomPointX + x:" + (RoomPointX+x));
                System.out.println("RoomPointY:" + RoomPointY + " " + "RoomPointY + y:" + (RoomPointY+y));
                CurrentRoom[RoomPointX+x][RoomPointY+y] = SubRoom[y][x];
            }
        }
    }

    static void PlaceMobs(R_Room room, char[][] CurrentRoom){

        for(R_Mob mob : room.RoomCreatures){

            int y = CurrentRoom[0].length/2-random.nextInt(2)+1;

            int x = random.nextInt(CurrentRoom.length);

            System.out.println("MobPosition" + " " + "x:" + x + " " + "y:" + y);

            if(CheckWall(CurrentRoom[x][y])) {
                CurrentRoom[x][y] = mob.getCreatureSymbol();
                mob.setMobPosition(x,y);
                R_Dungeon.CurrentRoomCreatures.put((R_Mob) mob, mob.id);
            }
        }
    }

    public static void ConnectRooms(){

        int i = 2;

        ArrayList<R_Room> Keys = new ArrayList<>();

        for (Map.Entry<R_Room, char[][]> r_roomEntry : R_Dungeon.Rooms.entrySet()) {
            Keys.add(r_roomEntry.getKey());
        }

        Keys.sort(Comparator.comparingInt(value -> value.NumberOfRoom));

        for(R_Room room : Keys){
            if(!room.IsEndRoom) {
                int finalI = i;
                room.nextRoom = R_Dungeon.Rooms.keySet()
                        .stream()
                        .filter(r_room -> r_room.NumberOfRoom == finalI)
                        .findFirst()
                        .orElse(null);
            }
            i++;
        }
    }

    public static void GenerateDungeon(int Height, int Widght, int DungeonLenght){

        for(int i = 0; i < DungeonLenght; i++){
            Height = random.nextInt(15)+5;
            Widght = random.nextInt(20)+5;

            if(i == DungeonLenght-1){
                R_Dungeon.Rooms.put(new R_Room(i+1, true, Widght, Height), new char[Widght][Height]);
            }
            else
                R_Dungeon.Rooms.put(new R_Room(i+1, Widght, Height), new char[Widght][Height]);
        }
        ConnectRooms();
    }

    public static void GenerateRoom(R_Room room, char[][] CurrentRoom) {

        CurrentRoom = R_Dungeon.Rooms.get(room);
        R_Player.CurrentRoom = room.NumberOfRoom;
        System.out.println("CurrentRoom " + room.NumberOfRoom);
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
        System.out.println("Room dimensions = " + CurrentRoom.length + " " + CurrentRoom[0].length);

        int RoomLenghtY = CurrentRoom.length;
        int RoomLenghtX = CurrentRoom[0].length;

        PlaceBorders(CurrentRoom);

        FillSpaceWithEmpty(CurrentRoom);

        PlaceDoors(room, CurrentRoom);

        if(RoomLenghtX >= 12 && RoomLenghtY >= 12){
            PlaceSubRoom(GenerateSubRoom(), CurrentRoom);
        }
        if (RoomLenghtX >=3 && RoomLenghtY >=3){
            PlaceMobs(room, CurrentRoom);
        }

        R_Dungeon.CurrentRoom = CurrentRoom;

        System.out.println("Center of room:" + GetCenterOfRoom());
    }

    public static void PutPlayerInDungeon(int x, int y, char[][] CurrentRoom){
        CurrentRoom[x][y] = R_Player.Player;
        R_Player.Pos.x = x;
        R_Player.Pos.y = y;
    }

    public static void PutSomething(char c, char[][] CurrentRoom){
    }

    public static void PutSomething(String s, char[][] CurrentRoom){
    }
}
