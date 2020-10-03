package com.rogurea.research;

import com.googlecode.lanterna.Symbols;

public class Scans {

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
}
