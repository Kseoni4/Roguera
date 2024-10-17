package kseoni.ch.roguera.graphics.render;

import com.googlecode.lanterna.graphics.TextGraphics;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.TextSprite;

public class RenderLayer {

    private final TextGraphics textGraphics;

    public RenderLayer(TextGraphics textGraphics){
        this.textGraphics = textGraphics;
    }

    public void drawSpriteOn(TextSprite sprite, Position position){
        this.textGraphics.setCharacter(
                position.getX(),
                position.getY(),
                sprite.getSprite()
        );
    }

    public void drawSpriteLine(TextSprite sprite, Position from, Position to){
        this.textGraphics.drawLine(
                from.getX(),
                from.getY(),
                to.getX(),
                to.getY(),
                sprite.getSprite()
        );
    }
}
