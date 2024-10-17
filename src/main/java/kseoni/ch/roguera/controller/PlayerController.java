package kseoni.ch.roguera.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.creature.Player;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.DungeonMap;

public class PlayerController {
    private final Player player;

    public PlayerController(Player player){
        this.player = player;
    }

    public void movePlayer(KeyType key){
        Position oldPosition = player.getPosition();
        switch (key){
            case ArrowUp -> move(oldPosition, player.getPosition().getRelativePosition(0, -1));
            case ArrowDown -> move(oldPosition, player.getPosition().getRelativePosition(0, 1));
            case ArrowLeft -> move(oldPosition, player.getPosition().getRelativePosition(-1, 0));
            case ArrowRight -> move(oldPosition, player.getPosition().getRelativePosition(1, 0));
        }
    }

    private void move(Position oldPosition, Position newPosition) {
        System.out.println("Move from "+oldPosition+" to "+newPosition);
        if(!checkCell(newPosition)){
            return;
        }
        DungeonMap.currentRoom().getCells().get(oldPosition).removeObject();
        DungeonMap.currentRoom().getCells().get(newPosition).placeObject(player);
    }

    private boolean checkCell(Position newPosition){
        Cell cell = DungeonMap.currentRoom().getCells().get(newPosition);

        if(newPosition.getX() >= DungeonMap.currentRoom().getWidth() || newPosition.getY() >= DungeonMap.currentRoom().getHeight()){
            return false;
        }

        if(newPosition.isNegative()){
            return false;
        }

        if(!cell.isEmpty()){
            return false;
        }

        return true;
    }
}
