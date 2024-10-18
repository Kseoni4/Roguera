package kseoni.ch.roguera.graphics.sprites;

import kseoni.ch.roguera.base.Position;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RectangleShape {

    private Position topLeftPosition;

    private int width;

    private int height;

    private TextSprite horizontalSprite;
    private TextSprite verticalSprite;
    private TextSprite topLeftCorner;
    private TextSprite topRightCorner;
    private TextSprite bottomLeftCorner;
    private TextSprite bottomRightCorner;
}