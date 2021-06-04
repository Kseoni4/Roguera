package com.rogurea.dev.player;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.base.Entity;
import com.rogurea.dev.gamemap.*;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.ViewObjects;

import static com.rogurea.dev.gamemap.Dungeon.player;

public class MoveController {

    private static Position newPos = new Position();

    public static void MovePlayer(KeyType key){
        switch (key) {
            case ArrowUp : Move(new Position(player.PlayerPosition.x, player.PlayerPosition.y + 1)); break;
            case ArrowLeft : Move(new Position(player.PlayerPosition.x - 1, player.PlayerPosition.y)); break;
            case ArrowDown : Move(new Position(player.PlayerPosition.x, player.PlayerPosition.y - 1)); break;
            case ArrowRight : Move(new Position(player.PlayerPosition.x + 1, player.PlayerPosition.y)); break;
        }
    }

    public static void Move(Position position){
        newPos = position;

        Cell nextCell = Dungeon.GetCurrentRoom().getCell(newPos);

        CheckObstaclesOrEntity(nextCell);

        if(nextCell.isEmpty()){
            MovePlayerIntoCell();
        } else if (nextCell.getFromCell() instanceof FogPart){
            Dungeon.GetCurrentRoom().getFogController().RemoveFogParts(player.lookAround(), 1);
            if(!CheckObstaclesOrEntity(nextCell))
                MovePlayerIntoCell();
        }


        ViewObjects.mapView.DrawAround();
    }
    private static void MovePlayerIntoCell(){
        Dungeon.GetCurrentRoom().getCell(player.PlayerPosition).removeFromCell();
        Dungeon.GetCurrentRoom().getCell(newPos).putIntoCell(player);
        player.PlayerPosition = newPos;
    }

    private static boolean CheckObstaclesOrEntity(Cell nextCell){
        if(Scan.CheckWall(nextCell)){
            return true;
        } else if(nextCell.getFromCell() instanceof Entity){
            ((Entity) nextCell.getFromCell()).action.run();
        }
        return false;
    }

}
