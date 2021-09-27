/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.input;

import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.dev.base.Debug;
import com.rogurea.dev.view.TerminalView;

import java.io.IOException;


public class Input {

    public static KeyStroke GetKey(){
        try {
            return TerminalView.terminal.readInput();
        } catch (IOException e) {
            Debug.toLog(e.getMessage());
        }
        return KeyStroke.fromString("");
    }

}
