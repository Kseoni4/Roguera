package kseoni.ch.roguera.graphics.ui.parts;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UIPart {

    private UIPartType uiPartType;

    private TextSprite partSprite;

    /**
     * Local position of part in {@link UIElement}
     */
    private Position partPosition;
}
