package com.rogurea.screentesting;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.rogurea.main.resources.GameResources;

import java.io.IOException;
import java.util.Objects;

public class ScreenView {

    static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Screen terminal = null;

    public static TextGraphics textGraphics;

    public static void ScreenInit() throws IOException {
        Terminal tl = defaultTerminalFactory.createTerminal();

        terminal = new TerminalScreen(tl);

        terminal.startScreen();

        terminal.setCursorPosition(null);


        textGraphics = terminal.newTextGraphics();

        textGraphics.putString(3,3, TerminalTextUtils.fitString("⎔", 3));

        terminal.refresh();
    }

}
