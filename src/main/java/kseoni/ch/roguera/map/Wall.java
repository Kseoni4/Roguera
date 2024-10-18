package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import lombok.AllArgsConstructor;
import lombok.Builder;

public class Wall extends GameObject {

    public Wall(){
        super("Wall");
        this.setTextSprite(TextSprite.DEFAULT_SPRITE);
    }

    public Wall(TextSprite sprite){
        super("Wall");
        this.setTextSprite(sprite);
    }
}
