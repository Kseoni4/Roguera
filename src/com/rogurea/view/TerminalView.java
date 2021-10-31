/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.rogurea.base.Debug;
import com.rogurea.mapgenerate.MapEditor;
import com.rogurea.resources.GameResources;
import com.rogurea.view.ui.InventoryView;
import com.rogurea.view.ui.LogView;
import com.rogurea.view.ui.PlayerInfoView;
import com.rogurea.gamemap.Position;

import java.io.IOException;
import java.util.Arrays;

import static com.rogurea.view.ViewObjects.*;

public class TerminalView {

    public static TerminalPosition currentPointerPosition;

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Terminal terminal = null;

    public static KeyStroke keyStroke = KeyStroke.fromString(" ");

    public static TerminalSize windowSize;

    public static int windowHeight;

    public static int windowWight;

    public static void setGameScreen() throws IOException {

        //Debug.log("RENDERING: Setting view blocks");

        ViewBlocks.forEach(Draw::init);

        playerInfoView = new PlayerInfoView();

        playerInfoView.infoPosition = infoGrid.placeNewBlock(playerInfoView, 1);

        playerInfoView.Init();

        logView = new LogView();

        logView.logPosition = infoGrid.placeNewBlock(logView, 2);

        logView.setLogHeightsize(infoGrid.getHorizontalBlockHeight());

        logView.setLogWightSize(infoGrid.getVerticalBlockWight());

        logView.Init();

        inventoryView = new InventoryView();

        inventoryView.inventoryPosition = infoGrid.placeNewBlock(inventoryView, 3);

        inventoryView.Init();

        terminal.addResizeListener((terminal, terminalSize) -> {
            try {
                terminal.clearScreen();
                updateWindowSize(terminalSize);
                Debug.toLog("Window size: ".concat(windowSize.toString()));

                ViewBlocks.forEach(Draw::reset);

                playerInfoView.infoPosition = infoGrid.get_zeroPointBlock1();

                logView.logPosition = infoGrid.get_zeroPointBlock2();

                logView.setLogHeightsize(infoGrid.getHorizontalBlockHeight());

                logView.setLogWightSize(infoGrid.getVerticalBlockWight());

                inventoryView.inventoryPosition = infoGrid.get_zeroPointBlock3();

                ViewBlocks.forEach(Draw::init);

                Debug.toLog("[RESIZE] Redraw all after resize");

                ViewBlocks.forEach(Draw::call);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        terminal.resetColorAndSGR();
    }


    public static void initTerminal() {
        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("Roguera build: " + GameResources.VERSION);

            defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(90
                    ,25));

            if(GameResources.TerminalFont != null){
                defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(
                        GameResources.TerminalFont
                ));
            }

            keyStroke = KeyStroke.fromString(" ");

            terminal = defaultTerminalFactory.createTerminal();

            updateWindowSize(terminal.getTerminalSize());

            Debug.toLog("Window size: ".concat(windowSize.toString()));

            terminal.flush();

            //terminal.enterPrivateMode();

            //terminal.clearScreen();

            terminal.setCursorVisible(false);

            terminal.newTextGraphics().putString(new TerminalPosition(50, 50), "LOADING...");

            terminal.flush();

            //Debug.log("RENDERING: Terminal init");

        } catch (IOException e) {
            //Debug.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateWindowSize(TerminalSize terminalSize) throws IOException {
        windowSize = terminalSize;

        windowHeight = windowSize.getRows();

        windowWight = windowSize.getColumns();
    }

    public static void dispose() {
        if(terminal != null){
            try {
                TerminalView.keyStroke = null;
                terminal.clearScreen();
                terminal.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void reDrawAll(IViewBlock[] except){

        //Debug.log("RENDERING: Redraw blocks");

        /*try {
            terminal.clearScreen();
            Debug.log("RENDERING: Screen has been cleared");
        } catch (IOException e) {
            Debug.log("ERROR: Clear screen was failed");
            Debug.log(e.getMessage());
            e.printStackTrace();
        }*/

        resetPositions(except);

        ViewBlocks.forEach(
                viewBlock -> {
                    if (Arrays.stream(except).noneMatch(viewBlock::equals)){
                        //Debug.log("RENDERING: draw call block " + viewBlock.getClass().getSimpleName());
                        viewBlock.Draw();
                    }
                });
        Draw.flush();
    }

    private static void resetPositions(IViewBlock[] except){

        //Debug.log("RENDERING: reset view blocks position");

        ViewBlocks.forEach(viewBlock -> {
                if(Arrays.stream(except).noneMatch(viewBlock::equals))
                    //Debug.log("RENDERING: reset block " + viewBlock.getClass().getSimpleName());
                    viewBlock.Reset();
            });
    }


    public static void initGraphics(TextGraphics textGraphics, TerminalPosition terminalPosition, TerminalSize terminalSize) {
        textGraphics.fillRectangle(terminalPosition, terminalSize, MapEditor.EmptyCell);
    }

    public static void drawBlockInTerminal(TextGraphics textgui, String data, TerminalPosition position) {
        textgui.putCSIStyledString(position, data);
    }

    public static void drawBlockInTerminal(TextGraphics textgui, String data, Position position){
        textgui.putCSIStyledString(new TerminalPosition(position.x, position.y), data);
    }

    public static void drawBlockInTerminal(TextGraphics textgui, String data, int x, int y) {
        textgui.putCSIStyledString(x, y, data);
    }

    public static void putCharInTerminal(TextGraphics textgui, TextCharacter data, int x, int y){
        textgui.setCharacter(x,y,data);
    }

    public static void putCharInTerminal(TextGraphics textgui, TextCharacter data, Position position){
        textgui.setCharacter(position.x,position.y,data);
    }

    public static void setPointerIntoPosition(TextGraphics textgui, char pointer, TerminalPosition position) {
        textgui.setCharacter(position, pointer);
    }
    public static void setPointerIntoPosition(TextGraphics textgui, char pointer, Position position) {
        textgui.setCharacter(new TerminalPosition(position.x, position.y), pointer);
    }
}