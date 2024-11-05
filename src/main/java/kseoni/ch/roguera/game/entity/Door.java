package kseoni.ch.roguera.game.entity;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Room;
import lombok.Getter;


public class Door extends GameObject implements Scriptable<Player> {

    @Getter
    private int toRoom;

    public Door(int toRoom) {
        super("Door");
        this.toRoom = toRoom;
    }

    public Door(TextSprite doorSprite, int toRoom){
        super("Door", doorSprite);
        this.toRoom = toRoom;
    }

    @Override
    public void doAction(Player player) {
        player = Dungeon.get().currentFloor().currentRoom().getCell(player.getPosition()).removeObject();
        Room room = Dungeon.get().currentFloor().getRoom(toRoom);
        room.getCell(new Position(1,1)).placeObject(player);
        player.getPlayerController().setRoom(room);
        Dungeon.get().currentFloor().nextRoom(toRoom);
        System.out.println("Door passed");
    }
}
