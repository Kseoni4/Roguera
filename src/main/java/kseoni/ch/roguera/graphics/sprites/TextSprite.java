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

    public TextColor getSpriteColor(int colorLayer){
        return switch (colorLayer){
            case 0 -> sprite.getBackgroundColor();
            case 1 -> sprite.getForegroundColor();
            default -> throw new IllegalStateException("Unexpected value: " + colorLayer);
        };
    }

    public void modifierSprite(SGR modifier){
        this.sprite = this.sprite.withModifier(modifier);
    }

    private TextSprite(){
        this.sprite = new TextCharacter('.');
    }

    public TextSprite(char model){
        this(new TextCharacter(model));
    }

    public TextSprite(TextCharacter sprite){
        this.sprite = sprite;
    }

    public TextSprite(char spriteChar, TextColor foregroundColor, TextColor backgroundColor){
        this.sprite = new TextCharacter(spriteChar, foregroundColor, backgroundColor);
    }
}
