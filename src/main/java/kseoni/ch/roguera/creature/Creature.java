package kseoni.ch.roguera.creature;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.graphics.sprites.TextSprite;

public class Creature extends GameObject {

    private int health;

    private int level;

    public Creature(String name, TextSprite textSprite) {
        super(name);
        this.setTextSprite(textSprite);
        this.health = 1;
        this.level = 1;
    }
}
