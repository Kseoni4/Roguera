/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.input;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.view.TerminalView;

import java.io.IOException;
import java.util.Optional;


public class Input {

    private static KeyStroke GetKey(){
        try {
            return TerminalView.terminal.readInput();
        } catch (IOException e) {
            return null;
        }
    };

    public static Optional<KeyStroke> waitForInput(){
        return Optional.ofNullable(GetKey());
    }

    public static boolean keyNotNull(KeyStroke key){
        return key != null;
    }

    public static boolean keyIsEscape(KeyStroke key){
        return key.getKeyType().equals(KeyType.Escape);
    }
}
