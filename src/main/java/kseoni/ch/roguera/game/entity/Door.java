package kseoni.ch.roguera.game.entity;

import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Floor;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.PositionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

public class Door extends GameObject implements Scriptable<Player> {

    @Getter
    private int toRoom = -1;

    @Setter
    @Getter
    private Door nextDoor;

    @Getter
    private Room curretRoom;

    public Door(int toRoom) {
        this.toRoom = toRoom;
    }

    public Door(TextSprite doorSprite, int toRoom, Room curretRoom) {
        super("Door", doorSprite);
        this.toRoom = toRoom;
        init(curretRoom);
    }

    private void init(Room curretRoom){
        this.curretRoom = curretRoom;
        curretRoom.addDoor(this);
    }

    @Override
    public void doAction(Player player) {
        if(Objects.isNull(nextDoor)){
            System.out.println("No next door to room: "+toRoom);
            return;
        }

        Floor curretFloor = Dungeon.get().currentFloor();
        int thisRoomId = curretRoom.getRoomId();

        player = curretRoom.getCell(player.getPosition()).removeObject();

        Room nextRoom = curretFloor.getRoom(toRoom);
        nextRoom.getCell(PositionUtils.getNonOccupiedPositionCell(nextRoom, nextDoor.getPosition())).placeObject(player);

        player.getPlayerController().setRoom(nextRoom);
        curretFloor.nextRoom(toRoom);

        EventLoop.get().send(Event.raise("Player has passed the door from room "+thisRoomId+" to room "+toRoom));
        System.out.println("Door passed");
    }
}
