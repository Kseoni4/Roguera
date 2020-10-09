package com.rogurea.research;

import com.googlecode.lanterna.Symbols;

public class Resources {

    static final char EmptyCell = ' ';

    static final char[] rsymbls = {
            '$', '/', '\\',
            '#', '!', '&',
            '^', '(', '*', ')',
            Symbols.BLOCK_SPARSE,
            Symbols.DOUBLE_LINE_VERTICAL,
            Symbols.DOUBLE_LINE_HORIZONTAL,
            Symbols.BULLET,
            Symbols.BLOCK_DENSE,
            Symbols.BLOCK_MIDDLE,
            Symbols.BLOCK_SOLID,
            Symbols.OUTLINED_SQUARE
    };

    static String getPlayerPositionInfo(){
        return  "Player position "
                + "x:" + R_Player.Pos.x
                + " "
                + "y:" + R_Player.Pos.y + ' ';
    }

    static final String[] PlayerInfo = {"Player: ", "HP: ", "MP: ", "Level: ", "Room: "};

}
