package com.rogurea.main.map;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.Player;

import java.io.IOException;
import java.util.*;

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

    public static ArrayList<Mob> CurrentRoomCreatures = new ArrayList<>();

    public static char[][] CurrentRoom = new char[Height][Widght];

    public static void Generate() throws IOException {

        BaseGenerate.GenerateDungeon(DungeonLenght);
        BaseGenerate.GenerateRoom(Objects.requireNonNull(BaseGenerate.GetRoom(Direction.FIRST)));
        BaseGenerate.PutPlayerInDungeon(CurrentRoom[0].length/2,1, Dungeon.CurrentRoom);
    }

    public static Room GetCurrentRoom(){
        return Rooms.stream().filter(
                room -> room.NumberOfRoom == Player.CurrentRoom
        ).findAny().orElse(null);
    }

    public static String ShowDungeon(int i, int j){
        return Objects.toString(Dungeon.CurrentRoom[i][j]);
    }

}
