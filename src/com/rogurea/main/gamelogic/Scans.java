package com.rogurea.main.gamelogic;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.resources.GameResources;
import static com.rogurea.main.resources.ViewObject.logBlock;

public class Scans {

    public static boolean CheckWall(char c){
        for(char cell : GameResources.MapStructureAtlas){
            if(cell == c)
                return false;
        }
        return true;
    }

    public static boolean CheckCorner(char c){
        for(char cell : GameResources.CornersAtlas){
            if(cell == c)
                return false;
        }
        return true;
    }

    public static boolean CheckExit(char c){
            return c == GameResources.GetModel("NextDoor");
    }

    public static boolean CheckBack(char c){
            return c == GameResources.GetModel("BackDoor");
    }

    /*public static boolean CheckProps(char c){
            for(char prop : GameResources.FurnitureAtlas){
                if(prop == c){
                    return true;
                }
            }
        return false;
    }*/

    public static boolean CheckItems(char c){
        for(char[] items : GameResources.WearableAtlas)
            for(char item : items)
                if(c == item)
                    return true;
                else if(c == GameResources.EmptyCell)
                    return false;
        return false;
    }

    public static void CheckSee(char c){
        if(GameResources.ModelNameMap.get(c) != null)
            logBlock.Action("see the " + GameResources.ModelNameMap.get(c));
    }

    public static boolean CheckCreature(char cell){

        for(Mob mob : Dungeon.CurrentRoomCreatures){
            if(cell == mob.getCreatureSymbol())
                 return true;
        }
        return false;
    }
}
