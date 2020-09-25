package com.rogurea.research;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.Player;

import javax.swing.*;
import java.util.Objects;

public class R_Dungeon {

    static int Height = 20;
    static int Widght = 15;

    public static char EmptyCell = ' ';

    public static boolean CheckExit(char c){
        if(!CheckWall(c)){
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
        if (c == Symbols.ARROW_DOWN)
            return false;
        return  true;
    }

    public static char[][] Dungeon = new char[Height][Widght];

    public static void GenerateDungeon() {
        System.out.println("Dungeon length = " + Dungeon.length + " " + Dungeon[0].length);
        Dungeon[0][0] = Symbols.DOUBLE_LINE_TOP_LEFT_CORNER;
        Dungeon[Dungeon.length-1][Dungeon[0].length-1] = Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER;
        Dungeon[0][Dungeon[0].length-1] = Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER;
        Dungeon[Dungeon.length-1][0] = Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
        for (int i = 1; i < Dungeon[0].length-1; i++) {
            Dungeon[0][i] = Symbols.DOUBLE_LINE_VERTICAL;
        }
        for (int i = 1; i < Dungeon.length - 1; i++) {
            Dungeon[i][Dungeon[0].length - 1] = Symbols.DOUBLE_LINE_HORIZONTAL;
        }
        for (int i = 1; i < Dungeon[0].length - 1; i++) {
            Dungeon[Dungeon.length - 1][i] = Symbols.DOUBLE_LINE_VERTICAL;
        }
        for (int i = 1; i < Dungeon.length-1; i++) {
            Dungeon[i][0] = Symbols.DOUBLE_LINE_HORIZONTAL;
        }
        for (int i = 0; i < Dungeon.length; i++) {
            for (int j = 0; j < Dungeon[0].length; j++) {
                if (CheckWall(Dungeon[i][j])) {
                    Dungeon[i][j] = EmptyCell;
                }
            }
        }
        Dungeon[Dungeon.length/2][Dungeon[0].length-1] = Symbols.ARROW_DOWN;
    }

   public static void PutPlayerInDungeon(){
        Dungeon[1][1] = R_Player.Player;
    }
    public static String ShowDungeon(int i, int j){
        return Objects.toString(Dungeon[i][j]);
    }
}
