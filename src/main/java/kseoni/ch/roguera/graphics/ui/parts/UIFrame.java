package kseoni.ch.roguera.graphics.ui.parts;

import kseoni.ch.roguera.base.Position;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UIFrame {

    private String name;

    private int wight;

    private int height;

    private UIPart topLeftCorner;
    private UIPart topRightCorner;
    private UIPart bottomLeftCorner;
    private UIPart bottomRightCorner;

    private Position topLeftFramePosition;

    private List<Position> localPositions;

    private HashMap<String, UIElement> elements;
}
