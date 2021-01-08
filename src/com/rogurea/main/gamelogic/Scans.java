package com.rogurea.main.gamelogic;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static com.rogurea.main.view.ViewObjects.logBlock;

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

    /**
     * Return true if current symbol on cell position is an item;
     * Return false if it is not;
     * @param c cell
     * @return boolean
     */
    public static boolean CheckItems(char c) {
        return CheckInResourceMap(c);
    }

    private static boolean CheckInResourceMap(char c){
        for (char item : GameResources.ResourcesMap.values()) {
            if(c == item){
                return true;
            } else{
                continue;
            }
        }
        return false;
    }

    public static void CheckSee(Position LookPosition){
        if(Scans.CheckWall(MapEditor.getFromCell(LookPosition)) && Scans.CheckCorner(MapEditor.getFromCell(LookPosition)))
            if(Scans.CheckItems(MapEditor.getFromCell(LookPosition))) {
                try {
                    Item ThisItem = Dungeon.GetCurrentRoom().RoomItems.stream().filter(item -> item.ItemPosition.equals(LookPosition)).findAny().get();
                    logBlock.Action("see the " + ThisItem.getMaterialColor() + ThisItem.name);
                }catch (NoSuchElementException e){
                    Debug.log("ITEM ERROR: no such item in the looking position " + LookPosition.toString());
                }
            }
        else if(Scans.CheckExit(MapEditor.getFromCell(LookPosition)))
            logBlock.Action("see the door");
    }

    public static boolean CheckCreature(char cell){

        for(Mob mob : Dungeon.GetCurrentRoom().RoomCreatures){
            if(cell == mob.getCreatureSymbol())
                 return true;
        }
        return false;
    }
}
