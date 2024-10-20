package kseoni.ch.roguera.controller;

import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Room;

public class PlayerController {
    private final Player player;

    private final Room room;

    public PlayerController(Player player){
        this.player = player;
        this.room = Dungeon.get().currentFloor().currentRoom();
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
        room.getCells().get(oldPosition).removeObject();
        room.getCells().get(newPosition).placeObject(player);
    }

    private boolean checkCell(Position newPosition){
        Cell cell = room.getCells().get(newPosition);

        if(newPosition.getX() >= room.getWidth() || newPosition.getY() >= room.getHeight()){
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
