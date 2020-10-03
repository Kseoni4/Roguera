package com.rogurea.research;

import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.Arrays;

public class R_GameLoop {

    public static void Start() throws IOException {

        T_View.InitTerminal();

    }

    public static void InLoop() throws IOException {
        char cell = ' ';

        while (T_View.keyStroke.getKeyType() != KeyType.Escape) {

            T_View.Drawcall(cell);

            T_View.keyStroke = T_View.terminal.readInput();

            if(R_MobController.MobsScan()) {
                T_View.DrawFightMenu();
                T_View.MenuPrompt(R_Dungeon.CurrentRoomCreatures.keySet());
            }

            R_MoveController.MovePlayer(T_View.keyStroke.getKeyType());
        }
    }

}
