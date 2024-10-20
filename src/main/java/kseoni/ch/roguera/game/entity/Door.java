package kseoni.ch.roguera.game.entity;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import lombok.Getter;

public class Door extends GameObject implements Scriptable{

    @Getter
    private int roomId;

    public Door(TextSprite doorSprite, int roomId){
        super("Door", doorSprite);
        this.roomId = roomId;
    }

    @Override
    public void doAction() {
        System.out.println("Door passed");
    }
}
