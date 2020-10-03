package com.rogurea.research;

import com.googlecode.lanterna.Symbols;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class R_Dungeon {

    public static enum Direction {
        NEXT,
        BACK,
        FIRST
    };

    static int Height = 1;

    static int Widght = 1;

    static int DungeonLenght = 5;

    public static Hashtable<R_Room, char[][]> Rooms = new Hashtable<>();

    public static Hashtable<R_Mob, Integer> CurrentRoomCreatures = new Hashtable<R_Mob, Integer>();

    static char[][] CurrentRoom = new char[Height][Widght];

    public static void Generate(){
        R_Generate.GenerateDungeon(Height, Widght, DungeonLenght);
        R_Generate.GenerateRoom(
                R_Generate.GetRoom(Direction.FIRST),
                CurrentRoom
        );
        R_Generate.PutPlayerInDungeon(1,1, R_Dungeon.CurrentRoom);
    }

    public static String ShowDungeon(int i, int j){
        return Objects.toString(CurrentRoom[i][j]);
    }

}
