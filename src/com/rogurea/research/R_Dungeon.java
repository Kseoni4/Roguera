package com.rogurea.research;

import com.googlecode.lanterna.Symbols;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class R_Dungeon {

    static int Height = 1;
    static int Widght = 1;

    static int DungeonLenght = 5;

    static Random random = new Random();

   // static ArrayList<R_Room> Rooms = new ArrayList<>();

    public static Hashtable<R_Room, char[][]> Rooms = new Hashtable<>();

    public static char EmptyCell = ' ';

    static char[][] CurrentRoom = new char[Height][Widght];

    public static boolean CheckExit(char c){
        if(CheckWall(c)){
            return c == Symbols.ARROW_DOWN;
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

    public static void ConnectRooms(){

        int i = 2;

        ArrayList<R_Room> Keys = new ArrayList<>();

        for (Map.Entry<R_Room, char[][]> r_roomEntry : Rooms.entrySet()) {
            Keys.add(r_roomEntry.getKey());
        }

        Keys.sort(Comparator.comparingInt(value -> value.NumberOfRoom));

        for(R_Room room : Keys){
            if(!room.IsEndRoom) {
                int finalI = i;
                room.nextRoom = Rooms.keySet()
                                .stream()
                                .filter(r_room -> r_room.NumberOfRoom == finalI)
                                .findFirst()
                                .orElse(null);
            }
            i++;
        }
    }

    public static void GenerateDungeon(){

        for(int i = 0; i < DungeonLenght; i++){
            Height = random.nextInt(30)+5;
            Widght = random.nextInt(15)+5;
            if(i == DungeonLenght-1){
                Rooms.put(new R_Room(i+1, true, Height, Widght), new char[Height][Widght]);
            }
            else
                Rooms.put(new R_Room(i+1, Height, Widght), new char[Height][Widght]);
        }
        ConnectRooms();

    }

    public static void GenerateRoom(R_Room room) {

        CurrentRoom = Rooms.get(room);
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
        for (int i = 0; i < CurrentRoom.length; i++) {
            for (int j = 0; j < CurrentRoom[0].length; j++) {
                if (CheckWall(CurrentRoom[i][j])) {
                    CurrentRoom[i][j] = EmptyCell;
                }
            }
        }
        if(!room.IsEndRoom)
            CurrentRoom[CurrentRoom.length/2][CurrentRoom[0].length-1] = Symbols.ARROW_DOWN;
    }

   public static void PutPlayerInDungeon(){
        CurrentRoom[1][1] = R_Player.Player;
        R_Player.Pos.x = 1;
        R_Player.Pos.y = 1;
    }
    public static String ShowDungeon(int i, int j){
        return Objects.toString(CurrentRoom[i][j]);
    }
}
