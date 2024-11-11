package kseoni.ch.roguera.controller;

import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.game.entity.Scriptable;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Room;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerController {

    private final Map<KeyType, Position> directionMap = new LinkedHashMap<>(
            Map.of(KeyType.ArrowUp, Position.BACK,
                    KeyType.ArrowDown, Position.FRONT,
                    KeyType.ArrowLeft, Position.LEFT,
                    KeyType.ArrowRight, Position.RIGHT));

    private final Player player;

    @Setter
    private Room room;

    public PlayerController(Player player){
        this.player = player;
        this.room = Dungeon.get().currentFloor().currentRoom();
    }

    public void movePlayer(KeyType key){
        if(!directionMap.containsKey(key)){
            return;
        }
        Position oldPosition = player.getPosition();
        Position direction = directionMap.get(key);
        move(oldPosition, player.getPosition().getRelativePosition(direction));
    }

    private void move(Position oldPosition, Position newPosition) {
        String moveInfo = String.format("ROOM[%d] mv from l%s to l%s", room.getRoomId(), oldPosition, newPosition);
        System.out.println(moveInfo);
        //System.out.println("Move from l"+oldPosition+"g"+oldPosition.getRelativePosition(room.getRoomLeftTopPosition())+" to l"+newPosition+"g"+newPosition.getRelativePosition(room.getRoomLeftTopPosition()));
        if(!checkCell(newPosition)){
            return;
        }

        if(room.getCell(newPosition).getObject() instanceof Scriptable){
            Scriptable<Player> scriptable = room.getCell(newPosition).getObject();
            scriptable.doAction(player);
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

        if(newPosition.getX() > room.getWidth() || newPosition.getY() > room.getHeight()){
            return false;
        }

        if(newPositionGlobal.getX() < room.getRoomLeftTopPosition().getX() || newPositionGlobal.getY() < room.getRoomLeftTopPosition().getY()){
            return false;
        }

        if(newPositionGlobal.isNegative()){
            return false;
        }

        if(cell.isWall()){
            return false;
        }

        return true;
    }

    public void reset(){
        this.room = Dungeon.get().currentFloor().currentRoom();
    }
}
