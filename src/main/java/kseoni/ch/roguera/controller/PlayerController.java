package kseoni.ch.roguera.controller;

import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Room;

import java.util.Objects;

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
        System.out.println("Move from l"+oldPosition+"g"+oldPosition.getRelativePosition(room.getRoomLeftTopPosition())+" to l"+newPosition+"g"+newPosition.getRelativePosition(room.getRoomLeftTopPosition()));
        if(!checkCell(newPosition)){
            return;
        }
        room.getCell(oldPosition).removeObject();
        room.getCell(newPosition).placeObject(player);
    }

    private boolean checkCell(Position newPosition){
        Cell cell = room.getCell(newPosition);

        Position newPositionGlobal = newPosition.getRelativePosition(room.getRoomLeftTopPosition());

        if(Objects.isNull(cell)){
            return false;
        }

        if(newPosition.getX() >= room.getWidth() || newPosition.getY() >= room.getHeight()){
            return false;
        }

        if(newPositionGlobal.getX() < room.getRoomLeftTopPosition().getX() || newPositionGlobal.getY() < room.getRoomLeftTopPosition().getY()){
            return false;
        }

        if(newPositionGlobal.isNegative()){
            return false;
        }

        if(!cell.isEmpty()){
            return false;
        }

        return true;
    }
}
