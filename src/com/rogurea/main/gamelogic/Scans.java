package com.rogurea.main.gamelogic;

import com.googlecode.lanterna.Symbols;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.Log;

public class Scans {

    public static boolean CheckWall(char c){
        if (c == GameResources.HWall)
            return false;
        if (c == GameResources.VWall)
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
            return c == GameResources.NextRoom;
        }
        return false;
    }

    public static boolean CheckBack(char c){
        if(CheckWall(c)){
            return c == GameResources.BackRoom;
        }
        return false;
    }

    public static boolean CheckProps(char c){
        if(CheckWall(c)){
            return c == GameResources.chair
                    || c == GameResources.table_rect
                    || c == GameResources.table_hex;
        }
        return false;
    }

    public static boolean CheckItems(char c){
        for(char[] items : GameResources.WeaponAtlas)
            for(char item : items)
                if(c == item)
                    return true;
        return false;
    }

    public static void CheckSee(char c){
        if(GameResources.ModelNameMap.get(c) != null)
            Log.Action("see the " + GameResources.ModelNameMap.get(c));
    }
}
