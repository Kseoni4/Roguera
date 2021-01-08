package com.rogurea.main.view.UI;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.ViewObjects;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;

public class ControlBlock implements IViewBlock {

    static TextGraphics Controls = null;

    static final StringBuilder stringControls = new StringBuilder()
                .append("i\u001b[48;5;57mInv\u001b[0m " +
                        "r\u001b[48;5;57mGenRoom\u001b[0m " +
                        "c\u001b[48;5;57mClrLog\u001b[0m " +
                        "x\u001b[48;5;57mDrnkPrtn\u001b[0m " +
                        "ESC\u001b[48;5;57mQuit");

    public ControlBlock(){
        ViewObjects.ViewBlocks.add(this);
    }

    public void Init(){
        try {
            Controls = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Draw(){
        try {
            TerminalView.DrawBlockInTerminal(Controls, stringControls.toString(), 0, TerminalView.terminal.getTerminalSize().getRows()-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Reset() {

    }
}
