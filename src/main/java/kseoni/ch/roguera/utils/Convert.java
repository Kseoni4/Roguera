package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.map.Room;

import java.util.Set;
import java.util.stream.Collectors;

public class Convert {

    public static Set<Position> toGlobalPositions(Room room){
        return room.getCells().keySet()
                .stream()
                .map(position -> {
                    /*System.out.println(
                            "local position "+ position + " -> " + "global position "+ position.getRelativePosition(room.getRoomLeftTopPosition()
                            )
                    );*/
                    return position.getRelativePosition(room.getRoomLeftTopPosition());
                })
                .collect(Collectors.toSet());
    }

}
