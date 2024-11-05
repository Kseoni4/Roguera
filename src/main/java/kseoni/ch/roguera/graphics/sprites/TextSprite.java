package kseoni.ch.roguera.graphics.sprites;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import lombok.Getter;

@Getter
public class TextSprite {

    public static final TextSprite DEFAULT_SPRITE = new TextSprite();

    private TextCharacter sprite;

    public char getSpriteChar(){
        return sprite.getCharacter();
    }

    public TextColor getSpriteColor(ColorLayer colorLayer){
        return switch (colorLayer){
            case BACKGROUND -> sprite.getBackgroundColor();
            case FOREGROUND -> sprite.getForegroundColor();
        };
    }

    public void setSpriteColor(ColorLayer layer, TextColor color){
        switch (layer){
            case FOREGROUND -> sprite = new TextCharacter(sprite.getCharacter(), color, null);
            case BACKGROUND -> sprite = new TextCharacter(sprite.getCharacter(), null, color);
        }
    }

    public void modifierSprite(SGR modifier){
        this.sprite = this.sprite.withModifier(modifier);
    }

    private TextSprite(){
        this.sprite = new TextCharacter(' ');
    }

    public TextSprite(TextCharacter sprite){
        this.sprite = sprite;
    }

    public TextSprite(char model){
        this(model, null, null);
    }

    public TextSprite(char spriteChar, TextColor foregroundColor){
        this(spriteChar, foregroundColor, null);
    }

    public TextSprite(char spriteChar, TextColor foregroundColor, TextColor backgroundColor){
        this.sprite = new TextCharacter(spriteChar, foregroundColor, backgroundColor);
    }

    public enum ColorLayer {
        BACKGROUND(0),
        FOREGROUND(1);

        final int layer;
        ColorLayer(int layer){
            this.layer = layer;
        }
    }
}
