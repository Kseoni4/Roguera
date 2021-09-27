/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.base.Entity;
import com.rogurea.dev.base.GameObject;
import com.rogurea.main.creatures.Mob;
import com.rogurea.dev.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;

import java.util.Calendar;
import java.util.NoSuchElementException;

import static com.rogurea.dev.view.ViewObjects.logView;
import static com.rogurea.main.view.ViewObjects.logBlock;

public class Scan {

    /**
     * Проверяет клетку на наличие флага isWall(), который явно указывает,
     * является ли эта клетка стеной.
     * @param cell проверяемая клетка
     * @return true, если эта клетка - стена, false, если что-то нет.
     */
    public static boolean checkWall(Cell cell){
        return  cell.isWall();
    }

    /**
     * Проверяет игровой объект на клетке на принадлежность к части тумана (к классу @code FogPart)
     * @param gameObject игровой объект на клетке
     * @return true, если объект - часть тумана, false, если нет.
     */
    public static boolean checkFogPart(GameObject gameObject){
        return gameObject instanceof FogPart;
    }


    /**
     * Проверяет клетку на наличие модельки "угла", просматривая в названии модели окончание "ner".
     * Модели визуальных углов имеют название XYCorner, где XY - местоположение (например, RTCorner = Right Top Corner)
     * @param cell проверяемая клетка
     * @return true, если объект на клетке является моделькой "угла", false, если нет.
     */
    public static boolean checkCorner(Cell cell){
        return cell.getFromCell().model.getModelName().endsWith("ner");
    }


    /**
     * Проверяет клетку на наличие модельки "угла".
     * Модели визуальных углов имеют название XYCorner, где XY - местоположение (например, RTCorner = Right Top Corner)
     * @param cell проверяемая клетка
     * @param CornerAlign местоположение вида XY (например, RTCorner = Right Top Corner)
     * @return true, если объект на клетке является углом, false, если нет.
     */
    public static boolean checkCorner(Cell cell, String CornerAlign){
        return cell.getFromCell().model.getModelName().startsWith(CornerAlign);
    }

    public static void checkPlayerSee(Cell cell){
        if(cell.getFromCell() instanceof Item){
            logView.playerActionSee(((Item) cell.getFromCell()).getName());
            Debug.toLog("["+Calendar.getInstance().getTime()+"]"+"Player sees "+((Item) cell.getFromCell()).getName());
        } else if(cell.getFromCell() instanceof Entity){
            logView.playerActionSee(cell.getFromCell().model.getModelName());
        }
    }


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
