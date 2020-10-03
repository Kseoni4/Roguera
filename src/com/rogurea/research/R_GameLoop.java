package com.rogurea.research;

import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.Arrays;

public class R_GameLoop {

    public static void Start(){
        try{

            T_View.InitTerminal();
            InLoop();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            if (T_View.terminal != null) {
                try {
                    T_View.terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static void InLoop() throws IOException {
        char cell = ' ';

        while (T_View.keyStroke.getKeyType() != KeyType.Escape) {

            T_View.Drawcall();

            T_View.keyStroke = T_View.terminal.readInput();

/*            if(R_MobController.MobsScan()) {
                T_View.DrawFightMenu();
                T_View.MenuPrompt(R_Dungeon.CurrentRoomCreatures.keySet());
            }*/

            R_MoveController.MovePlayer(T_View.keyStroke.getKeyType());
        }
    }

}
