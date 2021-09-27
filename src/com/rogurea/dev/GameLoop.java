/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.gamelogic.Events;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.gamemap.Scan;
import com.rogurea.dev.player.KeyController;
import com.rogurea.dev.player.MoveController;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.view.Message;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.dev.view.ViewObjects;

import java.io.IOException;
import java.util.Random;

public class GameLoop {
    public GameLoop(){

    }

    public void start() {

        /* Message win = new Message(Colors.RED_BRIGHT,"teststtssstst\nfsddsds");

        win.show(); */

        Events.putTestItemIntoPos.action(new Position(1,2));

        Events.putChestIntoPos.action(new Position(4,3));

        ViewObjects.logView.putLog("log init_2");

        ViewObjects.logView.putLog("Very very long super string for testing");

        Random random = new Random();
        try {
            while (isNotEscapePressed() && isNotClosed()) {

                if (TerminalView.keyStroke != null) {
                    TerminalView.keyStroke = TerminalView.terminal.readInput();

                    if (TerminalView.keyStroke.getKeyType() == KeyType.Character) {
                        KeyController.getKey(TerminalView.keyStroke.getCharacter());
                    }

                    MoveController.movePlayer(TerminalView.keyStroke.getKeyType());
                    Scan.checkPlayerSee(Dungeon.player.getFrontCell());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            TerminalView.dispose();
        }
    }

    private boolean isNotEscapePressed(){
        KeyType keyType = TerminalView.keyStroke.getKeyType();
        return keyType != KeyType.Escape;
    }

    private boolean isNotClosed(){
       return TerminalView.terminal != null;
    }
}
