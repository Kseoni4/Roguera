/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.base.Entity;
import com.rogurea.dev.gamemap.*;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.ViewObjects;

import static com.rogurea.dev.gamemap.Dungeon.player;
import static com.rogurea.dev.view.ViewObjects.inventoryView;
import static com.rogurea.dev.view.ViewObjects.logView;

public class MoveController {

    private static Position newPos = new Position();

    public static void movePlayer(KeyType key){
        switch (key) {
            case ArrowUp : move(new Position(player.playerPosition.x, player.playerPosition.y + 1)); break;
            case ArrowLeft : move(new Position(player.playerPosition.x - 1, player.playerPosition.y)); break;
            case ArrowDown : move(new Position(player.playerPosition.x, player.playerPosition.y - 1)); break;
            case ArrowRight : move(new Position(player.playerPosition.x + 1, player.playerPosition.y)); break;
        }
    }

    public static void move(Position position){
        newPos = position;

        Cell nextCell = Dungeon.getCurrentRoom().getCell(newPos);

        if(checkNextCell(nextCell)){
            return;
        }

        if(nextCell.isEmpty()){
            movePlayerIntoCell();
        } /*else if (nextCell.getFromCell() instanceof FogPart){
            Dungeon.GetCurrentRoom().getFogController().RemoveFogParts(player.lookAround(), 1);
            if(!CheckObstaclesOrEntity(nextCell))
                MovePlayerIntoCell();
        }*/


        ViewObjects.mapView.drawAround();
    }
    private static void movePlayerIntoCell(){
        Dungeon.getCurrentRoom().getCell(player.playerPosition).removeFromCell();
        Dungeon.getCurrentRoom().getCell(newPos).putIntoCell(player);
        player.playerPosition = newPos;
    }

    private static boolean checkNextCell(Cell nextCell){
        if (nextCell.getFromCell() instanceof FogPart) {
            Dungeon.getCurrentRoom().getFogController().removeFogParts(player.lookAround(), 1);
        }

        if(Scan.checkWall(nextCell)){
            return true;
        } else if(nextCell.getFromCell() instanceof Entity){
            ((Entity) nextCell.getFromCell()).action.run();
            return true;
        }

        if(nextCell.getFromCell().tag.startsWith("item")){
            Item _item = (Item) nextCell.getAndRemoveFromCell();
            if(nextCell.getFromCell() instanceof FogPart) {
                nextCell.clear();
            }
            player.putUpItem(_item);
            Draw.call(inventoryView);
        }

        Scan.checkPlayerSee(nextCell);
        return false;
    }

}
