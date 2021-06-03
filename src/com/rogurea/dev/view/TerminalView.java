package com.rogurea.dev.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.dev.view.IViewBlock;

import java.io.IOException;
import java.util.Arrays;

import static com.rogurea.dev.view.ViewObjects.ViewBlocks;

public class TerminalView {

    public static TerminalPosition CurrentPointerPosition;

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Terminal terminal = null;

    public static KeyStroke keyStroke = KeyStroke.fromString(" ");

    public static void SetGameScreen() throws IOException {

        Debug.log("RENDERING: Setting view blocks");

        ViewBlocks.forEach(Draw::init);

        //Effects.Init();

        terminal.resetColorAndSGR();
    }

    public static void InitTerminal() {
        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("Roguera build: " + GameResources.version);

            //SwingTerminalFontConfiguration fontConfiguration = SwingTerminalFontConfiguration.newInstance(GameResources.TerminalFont);

            //defaultTerminalFactory.setTerminalEmulatorFontConfiguration(fontConfiguration);

            terminal = defaultTerminalFactory.createTerminal();

            terminal.flush();

            //terminal.enterPrivateMode();

            //terminal.clearScreen();

            terminal.setCursorVisible(false);

            terminal.newTextGraphics().putString(new TerminalPosition(50, 50), "LOADING...");

            terminal.flush();

            Debug.log("RENDERING: Terminal init");

        } catch (IOException e) {
            Debug.log(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void ReDrawAll(IViewBlock[] except){

        Debug.log("RENDERING: Redraw blocks");

        /*try {
            terminal.clearScreen();
            Debug.log("RENDERING: Screen has been cleared");
        } catch (IOException e) {
            Debug.log("ERROR: Clear screen was failed");
            Debug.log(e.getMessage());
            e.printStackTrace();
        }*/

        ResetPositions(except);

        ViewBlocks.forEach(
                viewBlock -> {
                    if (Arrays.stream(except).noneMatch(viewBlock::equals)){
                        Debug.log("RENDERING: draw call block " + viewBlock.getClass().getSimpleName());
                        viewBlock.Draw();
                    }
                });
        Draw.flush();
    }

    private static void ResetPositions(IViewBlock[] except){

        Debug.log("RENDERING: reset view blocks position");

        ViewBlocks.forEach(viewBlock -> {
                if(Arrays.stream(except).noneMatch(viewBlock::equals))
                    Debug.log("RENDERING: reset block " + viewBlock.getClass().getSimpleName());
                    viewBlock.Reset();
            });
    }


    public static void InitGraphics(TextGraphics textGraphics, TerminalPosition terminalPosition, TerminalSize terminalSize) {
        textGraphics.fillRectangle(terminalPosition, terminalSize, MapEditor.EmptyCell);
    }

    public static void DrawBlockInTerminal(TextGraphics textgui, String data, TerminalPosition position) {
        textgui.putCSIStyledString(position, data);
    }

    public static void DrawBlockInTerminal(TextGraphics textgui, String data, int x, int y) {
        textgui.putCSIStyledString(x, y, data);
    }

    public static void PutCharInTerminal(TextGraphics textgui, TextCharacter data, int x, int y){
        textgui.setCharacter(x,y,data);
    }

    public static void PutCharInTerminal(TextGraphics textgui, TextCharacter data, Position position){
        textgui.setCharacter(position.x,position.y,data);
    }

    public static void SetPointerIntoPosition(TextGraphics textgui, char pointer, TerminalPosition position) {
        textgui.setCharacter(position, pointer);
    }
}
