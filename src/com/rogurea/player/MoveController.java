/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.player;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.base.Entity;
import com.rogurea.base.GameObject;
import com.rogurea.creatures.Mob;
import com.rogurea.creatures.NPC;
import com.rogurea.gamelogic.Events;
import com.rogurea.gamemap.*;
import com.rogurea.items.Item;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;

import java.util.Optional;

import static com.rogurea.gamemap.Dungeon.player;
import static com.rogurea.view.ViewObjects.infoGrid;
import static com.rogurea.view.ViewObjects.inventoryView;

public class MoveController {

    private static Position newPos = new Position();

    public static void movePlayer(Optional<KeyStroke> key){

        Optional<KeyType> keyType = Optional.ofNullable(key.get().getKeyType());

        keyType.ifPresent(pointer -> {
            switch (pointer) {
                case ArrowUp:
                    move(new Position(player.playerPosition.x, player.playerPosition.y - 1));
                    break;
                case ArrowLeft:
                    move(new Position(player.playerPosition.x - 1, player.playerPosition.y));
                    break;
                case ArrowDown:
                    move(new Position(player.playerPosition.x, player.playerPosition.y + 1));
                    break;
                case ArrowRight:
                    move(new Position(player.playerPosition.x + 1, player.playerPosition.y));
                    break;
                case Character:
                    moveByWASD(key.get().getCharacter());
                    break;
            }
        });
    }

    private static void moveByWASD(char key){
        switch (key) {
            case 'w':
            case 'ц':
                move(new Position(player.playerPosition.x, player.playerPosition.y - 1));
                break;
            case 'a':
            case 'ф':
                move(new Position(player.playerPosition.x - 1, player.playerPosition.y));
                break;
            case 's':
            case 'ы':
                move(new Position(player.playerPosition.x, player.playerPosition.y + 1));
                break;
            case 'd':
            case 'в':
                move(new Position(player.playerPosition.x + 1, player.playerPosition.y));
                break;
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
        }
        ViewObjects.mapView.drawAround();
    }
    private static void movePlayerIntoCell(){
        Dungeon.getCurrentRoom().getCell(player.playerPosition).removeFromCell();
        Dungeon.getCurrentRoom().getCell(newPos).putIntoCell(player);
        player.playerPosition = newPos;
        player.cellPosition = newPos;
    }

    private static boolean checkNextCell(Cell nextCell){

        GameObject object = nextCell.getFromCell();

        if (object instanceof FogPart) {
            Dungeon.getCurrentRoom().getFogController().removeFogParts(player.lookAround(), 3);
        }

        if(Scan.checkWall(nextCell)){
            return true;
        } else if(object instanceof Entity){
            ((Entity) object).action.go();
            return true;
        }

        if(object.tag.startsWith("item")){
            Item _item = (Item) nextCell.getFromCell();
            if(object instanceof FogPart) {
                nextCell.clear();
            }
            if(player.putUpItem(_item)) {
                nextCell.removeFromCell();
                Draw.call(inventoryView);
                Draw.call(infoGrid);
            }
        }

        if(object.tag.startsWith("creature.mob")){
            assert object instanceof Mob;
            Mob mob = (Mob) object;
            if(mob.getHP() > 0)
                Events.encounter(player, mob);
            else
                movePlayerIntoCell();
        } else if (object.tag.startsWith("creature.npc")) {
            assert object instanceof NPC;
            ((NPC) object).executeLogic();
        }

        Scan.checkPlayerSee(nextCell);
        return false;
    }

}
