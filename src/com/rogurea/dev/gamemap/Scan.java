package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.GameObject;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;

import java.util.NoSuchElementException;

import static com.rogurea.main.view.ViewObjects.logBlock;

public class Scan {

    public static boolean CheckWall(Cell cell){
        return  cell.isWall();
    }

    public static boolean CheckFogPart(GameObject gameObject){
        return gameObject instanceof FogPart;
    }

    public static boolean CheckCorner(Cell cell){
        return cell.getFromCell().model.getModelName().endsWith("ner");
    }

    public static boolean CheckCorner(Cell cell, String CornerAlign){
        return cell.getFromCell().model.getModelName().startsWith(CornerAlign);
    }

    /*public static boolean CheckWall(char c){
        for(char cell : GameResources.RoomStructureAtlas){
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
    }*/

   /* public static boolean CheckRoomExit(char c){
            return c == GameResources.GetModel("NextDoor");
    }

    public static boolean CheckDungeonExit(char c){
        return c == GameResources.GetModel("DungeonExit");
    }

    public static boolean CheckBack(char c){
            return c == GameResources.GetModel("BackDoor");
    }
*/
    /**
     * Return true if current symbol on cell position is an item;
     * Return false if it is not;
     * @param c cell
     * @return boolean
     */
    /*public static boolean CheckItems(char c) {
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
        if(Scan.CheckWall(MapEditor.getFromCell(LookPosition)) && Scan.CheckCorner(MapEditor.getFromCell(LookPosition)))
            if(Scan.CheckItems(MapEditor.getFromCell(LookPosition))) {
                try {
                    Item ThisItem = Dungeon.GetCurrentRoom().RoomItems.stream().filter(item -> item.ItemPosition.equals(LookPosition)).findAny().get();
                    logBlock.Action("see the " + ThisItem.getMaterialColor() + ThisItem.name);
                }catch (NoSuchElementException e){
                    Debug.log("ITEM ERROR: no such item in the looking position " + LookPosition.toString());
                }
            }
        else if(Scan.CheckRoomExit(MapEditor.getFromCell(LookPosition)))
            logBlock.Action("see the door");
    }

    public static boolean CheckCreature(char cell){

        for(Mob mob : Dungeon.GetCurrentRoom().RoomCreatures){
            if(cell == mob.getCreatureSymbol())
                 return true;
        }
        return false;
    }

    public static boolean CheckNPC(Position LookPosition){
        if(Dungeon.GetCurrentRoom().RoomNPC != null)
            return Dungeon.GetCurrentRoom().RoomNPC.getNPCPosition().equals(LookPosition);
        else
            return false;
    }*/
}
