package com.rogurea.research;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Random;

public class T_View {


    static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    static Terminal terminal = null;
    static TerminalPosition topLeft = new TerminalPosition(R_Dungeon.Dungeon.length + 1,1);
    static TerminalSize terminalSize = new TerminalSize(getPlayerPositionInfo().length() + 2, 5);
    static TerminalPosition topRight = topLeft.withRelativeColumn(terminalSize.getColumns()-1);

    static void InitNewTextGraphics(TextGraphics textGraphics1){
        textGraphics1.fillRectangle(topLeft, terminalSize, ' ');
    }

    static String getPlayerPositionInfo(){
        return  "Player position "
                + "x:" + R_Player.Pos.x
                + " "
                + "y:" + R_Player.Pos.y + ' ';
    }

    static void DrawPlayer(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        textGraphics.putString(i, j, R_Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void DrawDungeon(TextGraphics textGraphics, char cell){
        for (int i = 0; i < R_Dungeon.Dungeon.length; i++)
            for (int j = 0; j < R_Dungeon.Dungeon[0].length; j++) {
                // System.out.println("i:"+ i + " " + "j:" + j);
                //  System.out.println(R_Dungeon.Dungeon[i][j]);
                cell = R_Dungeon.ShowDungeon(i,j).charAt(0);
                if(cell == R_Player.Player) {
                    DrawPlayer(textGraphics, i, j);
                }
                else{
                    textGraphics.putString(i, j, R_Dungeon.ShowDungeon(i, j));
                }
                if (j == R_Dungeon.Dungeon[0].length - 1)
                    textGraphics.putString(i, j, "\n", SGR.BOLD);
            }
    }

    static Random random = new Random();

    static void NextRoomWindow(Terminal terminal) throws IOException {
        Screen screen = new TerminalScreen(terminal);
    }

    static void GameScreen(Terminal terminal) throws IOException {
        final TextGraphics textGraphics = terminal.newTextGraphics();
        TextGraphics textGraphics1 = terminal.newTextGraphics();

        terminal.resetColorAndSGR();

        KeyStroke keyStroke = KeyStroke.fromString("w");

        R_Dungeon.PutPlayerInDungeon();

        char cell = ' ';

        InitNewTextGraphics(textGraphics1);

        textGraphics1.putString(topLeft.withRelative(1,2),
                "Dungeon size: " + R_Dungeon.Dungeon.length + "x" + R_Dungeon.Dungeon[0].length);
        while (keyStroke.getKeyType() != KeyType.Escape) {

            textGraphics1.putString(topLeft.withRelative(1, 1), getPlayerPositionInfo());

            DrawDungeon(textGraphics, cell);

            terminal.flush();

            keyStroke = terminal.readInput();

            R_MoveController.MovePlayer(keyStroke);
        }
    }

    public static void setRandom(Screen screen, TerminalSize terminalSize){
        for(int column = 0; column < terminalSize.getColumns(); column++) {
            for(int row = 0; row < terminalSize.getRows(); row++) {
                screen.setCharacter(column, row, new TextCharacter(
                        ' ',
                        TextColor.ANSI.DEFAULT,
                        // This will pick a random background color
                        TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]));
            }
        }
    }

    public static void CheckResize(Screen screen, TerminalSize terminalSize){
        final int charactersToModifyPerLoop = 30;
        for(int i = 0; i < charactersToModifyPerLoop; i++) {
            TerminalPosition cellToModify = new TerminalPosition(
                    random.nextInt(terminalSize.getColumns()),
                    random.nextInt(terminalSize.getRows()));
            TextColor.ANSI color = TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)];
            TextCharacter characterInBackBuffer = screen.getBackCharacter(cellToModify);
            characterInBackBuffer = characterInBackBuffer.withBackgroundColor(color);
            characterInBackBuffer = characterInBackBuffer.withCharacter(' ');   // Because of the label box further down, if it shrinks
            screen.setCharacter(cellToModify, characterInBackBuffer);
        }
        String sizeLabel = "Terminal Size: " + terminalSize;
        TerminalPosition labelBoxTopLeft = new TerminalPosition(1, 1);
        TerminalSize labelBoxSize = new TerminalSize(sizeLabel.length() + 2, 3);
        TerminalPosition labelBoxTopRightCorner = labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 1);
        TextGraphics textGraphics = screen.newTextGraphics();
        //This isn't really needed as we are overwriting everything below anyway, but just for demonstrative purpose
        textGraphics.fillRectangle(labelBoxTopLeft, labelBoxSize, ' ');
        textGraphics.drawLine(
                labelBoxTopLeft.withRelativeColumn(1),
                labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 2),
                Symbols.DOUBLE_LINE_HORIZONTAL);
        textGraphics.drawLine(
                labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(1),
                labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(labelBoxSize.getColumns() - 2),
                Symbols.DOUBLE_LINE_HORIZONTAL);
        textGraphics.setCharacter(labelBoxTopLeft, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
        textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        textGraphics.setCharacter(labelBoxTopRightCorner, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
        textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
        textGraphics.putString(labelBoxTopLeft.withRelative(1, 1), sizeLabel);
    }

    public static void InitTerminal(){
        try{
            terminal = defaultTerminalFactory.createTerminal();
            terminal.flush();
            terminal.enterPrivateMode();
            terminal.clearScreen();
            terminal.setCursorVisible(false);
            GameScreen(terminal);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (terminal != null) {
                try {
                    terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
