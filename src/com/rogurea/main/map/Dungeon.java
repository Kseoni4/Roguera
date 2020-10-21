package com.rogurea.main.map;

import com.googlecode.lanterna.TerminalTextUtils;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.mapgenerate.BaseGenerate;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class Dungeon {

    public enum Direction {
        NEXT,
        BACK,
        FIRST
    };

    static int Height = 1;

    static int Widght = 1;

    static int DungeonLenght = 10;

    public static ArrayList<Room> Rooms = new ArrayList<>();

    public static Hashtable<Mob, Integer> CurrentRoomCreatures = new Hashtable<Mob, Integer>();

    public static char[][] CurrentRoom = new char[Height][Widght];

    public static void Generate() throws IOException {

        BaseGenerate.GenerateDungeon(DungeonLenght);
        BaseGenerate.GenerateRoom(Objects.requireNonNull(BaseGenerate.GetRoom(Direction.FIRST)));
        BaseGenerate.PutPlayerInDungeon(CurrentRoom[0].length/2,1, Dungeon.CurrentRoom);
    }

    public static Room GetCurrentRoom(Predicate<Room> predicate){
        return Rooms.stream().filter(predicate).findAny().orElse(null);
    }

    public static String ShowDungeon(int i, int j){
        return Objects.toString(Dungeon.CurrentRoom[i][j]);
    }

}
