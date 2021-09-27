/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.resources.Colors;

import java.io.IOException;

public class Message extends Window {

    private final String _message;

    public Message(String message) {
        super(new TerminalPosition(5,5),  new TerminalSize(message.length()+4, 3));
        this._message = message;
    }

    public Message(String color, String _message){
        super(new TerminalPosition(5,5), new TerminalSize(_message.length()+4, 3));
        this._message = color + _message;
    }

    @Override
    protected void content() {
        putStringIntoWindow(_message, new Position(1,0));
        putStringIntoWindow("OK", new Position((_windowSize.getColumns()/2)-2,1));
    }

    @Override
    protected void input() {
        try {
            TerminalView.terminal.readInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
