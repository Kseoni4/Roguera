package kseoni.ch.roguera.graphics.ui.parts;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UIElement {

    private String name;

    private String contentOfElement;

    private Position elementPosition;
}
