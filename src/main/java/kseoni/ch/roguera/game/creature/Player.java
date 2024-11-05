package kseoni.ch.roguera.game.creature;

import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Player extends Creature {

    private PlayerController playerController;

    public Player(String name, TextSprite textSprite) {
        super(name, textSprite);
    }
}
