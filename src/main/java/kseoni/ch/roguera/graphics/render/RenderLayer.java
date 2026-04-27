package kseoni.ch.roguera.graphics.render;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.graphics.ui.layout.Region;

public class RenderLayer {

    private final TextGraphics textGraphics;

    public RenderLayer(TextGraphics textGraphics) {
        this.textGraphics = textGraphics;
    }

    public void drawSpriteOn(TextSprite sprite, Position position) {
        this.textGraphics.setCharacter(
                position.getX(),
                position.getY(),
                sprite.getSprite()
        );
    }

    public void drawSpriteLine(TextSprite sprite, Position from, Position to) {
        this.textGraphics.drawLine(
                from.getX(),
                from.getY(),
                to.getX(),
                to.getY(),
                sprite.getSprite()
        );
    }

    /** Записать одиночный символ с явным fg/bg. */
    public void setChar(int x, int y, char ch, TextColor fg, TextColor bg) {
        textGraphics.setCharacter(x, y, new TextCharacter(ch, fg, bg));
    }

    /** Залить регион символом ch с указанными fg/bg. */
    public void fill(Region region, char ch, TextColor fg, TextColor bg) {
        TextCharacter cell = new TextCharacter(ch, fg, bg);
        for (int row = region.y(); row < region.bottom(); row++) {
            for (int col = region.x(); col < region.right(); col++) {
                textGraphics.setCharacter(col, row, cell);
            }
        }
    }

    /** Вывести строку с заданными цветами и SGR-модификаторами. */
    public void putString(int x, int y, String text, TextColor fg, TextColor bg, SGR... modifiers) {
        for (int i = 0; i < text.length(); i++) {
            TextCharacter cell = new TextCharacter(text.charAt(i), fg, bg);
            for (SGR m : modifiers) cell = cell.withModifier(m);
            textGraphics.setCharacter(x + i, y, cell);
        }
    }

    public void putString(int x, int y, String text, TextColor fg, TextColor bg) {
        putString(x, y, text, fg, bg, new SGR[0]);
    }
}
