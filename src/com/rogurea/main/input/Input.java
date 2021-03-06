package com.rogurea.main.input;

import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;


public class Input {

    public static KeyStroke GetKey(){
        try {
            return TerminalView.terminal.readInput();
        } catch (IOException e) {
            Debug.log(e.getMessage());
        }
        return KeyStroke.fromString("");
    }

}
