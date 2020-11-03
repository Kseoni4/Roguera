package com.rogurea.main.view;

import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

public class ControlBlock {

    static TextGraphics Controls = null;

    static final String stringControls = "i\u001b[48;5;57mInv\u001b[0m r\u001b[48;5;57mGenRoom\u001b[0m c\u001b[48;5;57mClrLog\u001b[0m ESC\u001b[48;5;57mQuit";

    public static void Init(){
        try {
            Controls = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DrawControls(){
        try {
            TerminalView.DrawBlockInTerminal(Controls, stringControls, 0, TerminalView.terminal.getTerminalSize().getRows()-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
