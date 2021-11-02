package com.rogurea.base;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import com.rogurea.GameLoop;
import com.rogurea.view.TerminalView;

import javax.swing.*;
import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DisposeListener implements Runnable{

    private final MultiWindowTextGUI terminalLookingOn;

    public DisposeListener(MultiWindowTextGUI terminal){
        this.terminalLookingOn = terminal;
    }

    @Override
    public void run() {
        Debug.toLog("[DISPOSE_LISTENER] Start listen on");
        boolean isActive = true;



        Debug.toLog("[DISPOSE_LISTENER] Terminal  has been closed");
        //GameLoop.endGameSequence();
    }
}
