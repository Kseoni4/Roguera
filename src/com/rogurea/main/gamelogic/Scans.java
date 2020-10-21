package com.rogurea.main.gamelogic;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.LogBlock;

public class Scans {

    public static boolean CheckWall(char c){
        for(char cell : GameResources.MapStructureAtlas){
            if(cell == c)
                return false;
        }
        return true;
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
            for(char prop : GameResources.FurnitureAtlas){
                if(prop == c){
                    return true;
                }
            }
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
            LogBlock.Action("see the " + GameResources.ModelNameMap.get(c));
    }

    public static boolean CheckCreature(char cell){

        for(Mob mob : Dungeon.CurrentRoomCreatures.keySet()){
            if(cell == mob.getCreatureSymbol())
                return true;
        }
        return false;
    }
}
