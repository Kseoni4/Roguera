package com.rogurea.research;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Set;

public class T_View extends Thread{

    static int Offset = 1;

    static int MaxOffset = Offset;

    static int FightMenuSizeRows = 5;

    static int MobIndex = 0;

    static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    static Terminal terminal = null;
    static TerminalSize terminalSize = new TerminalSize(getPlayerPositionInfo().length() + 2, 5);
    static TerminalSize FightMenuSize = new TerminalSize(Offset, FightMenuSizeRows);
    static TerminalPosition topLeft;
    static TerminalPosition topRight;
    static TerminalPosition topFightLeft;

    static KeyStroke keyStroke = KeyStroke.fromString(" ");

    static TextGraphics textGraphics = null;

    static TextGraphics textGraphics1 = null;

    static TextGraphics fightGraphics = null;

    static void InitNewTextGraphics(TextGraphics textGraphics1, TerminalPosition topLeft){
        textGraphics1.fillRectangle(topLeft, terminalSize, ' ');
    }

    static void FightTextGraphics(TextGraphics fightGraphics, TerminalPosition topFightLeft){
        fightGraphics.fillRectangle(topFightLeft, FightMenuSize, ' ');
        FightMenuSize = new TerminalSize(Offset, 1);
    }

    static String getPlayerPositionInfo(){
        return  "Player position "
                + "x:" + R_Player.Pos.x
                + " "
                + "y:" + R_Player.Pos.y + ' ';
    }

    static boolean CheckCreature(char cell){

        for(R_Mob mob : R_Dungeon.CurrentRoomCreatures.keySet()){
            if(cell == mob.getCreatureSymbol())
                return true;
        }
        return false;
    }

    static void DrawPlayer(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        textGraphics.putString(i, j, R_Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void DrawMob(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        textGraphics.putString(i, j, R_Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void DrawDungeon(TextGraphics textGraphics, char cell){
        for (int i = 0; i < R_Dungeon.CurrentRoom.length; i++)
            for (int j = 0; j < R_Dungeon.CurrentRoom[0].length; j++) {
                // System.out.println("i:"+ i + " " + "j:" + j);
                //  System.out.println(R_Dungeon.Dungeon[i][j]);
                cell = R_Dungeon.ShowDungeon(i,j).charAt(0);
                if(cell == R_Player.Player) {
                    DrawPlayer(textGraphics, i, j);
                }
                else if(CheckCreature(cell)){
                    DrawMob(textGraphics, i, j);
                }
                else{
                    textGraphics.putString(i, j, R_Dungeon.ShowDungeon(i, j));
                }
                if (j == R_Dungeon.CurrentRoom[0].length - 1)
                    textGraphics.putString(i, j, "\n", SGR.BOLD);
            }
    }

    static void SlideCursor(int NameOffsetA, int NameOffsetB){

        fightGraphics.setCharacter(
                topFightLeft.withRelative(NameOffsetA, 4), ' '
        );
        SetCursor(NameOffsetB);
    }

    static void SetCursor(int NameOffset){
        fightGraphics.setCharacter(
                topFightLeft.withRelative(NameOffset,4),
                Symbols.TRIANGLE_RIGHT_POINTING_BLACK);
    }

    static R_Mob SetMob(int MobIndex, Set<R_Mob> CurrentRoomMobSet){
        return (R_Mob) CurrentRoomMobSet.toArray()[MobIndex];
    }

    static int PutMobNames(String mobname){
        fightGraphics.putString(topFightLeft.withRelative(Offset,4), mobname);
        return Offset += mobname.length()+2;
    }

    static void ResetFightMenuSize(){
        FightMenuSize = new TerminalSize(R_Dungeon.CurrentRoom.length+MaxOffset, FightMenuSizeRows);
    }

    static void ClearFightMenu(int offset){
        fightGraphics.fillRectangle(new TerminalPosition(
                R_Dungeon.CurrentRoom.length+offset, topLeft.getRow()+5), FightMenuSize, ' ');
    }

    static void ResetMobNames(int[] NameOffset, Set<R_Mob> CurrentRoomMobSet){
        Offset = 1;
        int Index = 0;
        System.out.println("B4R:\n" + "\ttopLeft: " +
                topLeft.toString() + " topFightLeft: "
                + topFightLeft.toString() + " FightMenuSize: "
                + FightMenuSize.toString() + " topRight: "
                + topRight.toString());
        ResetFightMenuSize();
        ClearFightMenu(2);
        System.out.println("AFR:\n" + "\ttopLeft: " +
                topLeft.toString() + " topFightLeft: "
                + topFightLeft.toString() + " FightMenuSize: "
                + FightMenuSize.toString() + " topRight: "
                + topRight.toString());
        for(R_Mob mob : CurrentRoomMobSet){
            NameOffset[Index] = PutMobNames(mob.Name)-mob.Name.length()-3;
            Index++;
        }
        SetCursor(NameOffset[MobIndex]);
    }

    static void MenuPrompt(Set<R_Mob> CurrentRoomMobSet) throws IOException {

       // FightTextGraphics(fightGraphics, topFightLeft);

        int[] NameOffset = new int[CurrentRoomMobSet.size()];

        R_Mob CurrentMob;

        KeyStroke KeyMenu = KeyStroke.fromString(" ");

        System.out.println("In fight: \n" + "\ttopLeft: " +
                topLeft.toString() + " topFightLeft: "
                + topFightLeft.toString() + " FightMenuSize: "
                + FightMenuSize.toString() + " topRight: "
                + topRight.toString());

        ResetMobNames(NameOffset, CurrentRoomMobSet);

        MaxOffset = Offset;

        SetCursor(NameOffset[0]);

        terminal.flush();

        while(!CurrentRoomMobSet.isEmpty()){

            DrawFightMenu();

            ResetMobNames(NameOffset, CurrentRoomMobSet);

            terminal.flush();

            KeyMenu = terminal.readInput();

            try {
                switch (KeyMenu.getKeyType()) {
                    case ArrowRight -> {
                        MobIndex++;
                        SlideCursor(NameOffset[MobIndex - 1], NameOffset[MobIndex]);
                        //CurrentMob = SetMob(MobIndex, CurrentRoomMobSet);
                    }
                    case ArrowLeft -> {
                        MobIndex--;
                        SlideCursor(NameOffset[MobIndex + 1], NameOffset[MobIndex]);

                        //CurrentMob = SetMob(MobIndex, CurrentRoomMobSet);
                    }
                    case Enter -> {
                        CurrentMob = SetMob(MobIndex, CurrentRoomMobSet);
                        R_MobController.RemoveMob(CurrentMob);
                        CurrentRoomMobSet.remove(CurrentMob);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException e){
                SlideCursor(NameOffset[Math.abs(MobIndex)-MobIndex], 0);
                MobIndex = 0;
            }
        }
        ClearFightMenu(1);
        terminal.flush();
    }

    static void DrawFightMenu() throws IOException {

        Offset = 1;

        //System.out.println("Found Player!");

        fightGraphics.putString(topFightLeft.withRelative(1, 3),
                "Creatures:");
    }

    static void ResetTerminalPosition(){

        topLeft = new TerminalPosition(R_Dungeon.CurrentRoom.length + 1,1);

        topFightLeft = new TerminalPosition(R_Dungeon.CurrentRoom.length + 1, topLeft.getRow()+1);

        FightMenuSize = new TerminalSize(Offset, FightMenuSizeRows);

        topRight = topLeft.withRelativeColumn(terminalSize.getColumns()-1);

        System.out.println("topLeft: " +
                topLeft.toString() + " topFightLeft: "
                + topFightLeft.toString() + " FightMenuSize: "
                + FightMenuSize.toString() + " topRight: "
                + topRight.toString());
    }

    static void DrawInformation(TextGraphics textGraphics1, TerminalPosition topLeft){

        textGraphics1.putString(topLeft.withRelative(1,2),
                "Dungeon size: " + R_Dungeon.CurrentRoom.length + "x" + R_Dungeon.CurrentRoom[0].length);

        textGraphics1.putString(topLeft.withRelative(1,3), "" +
                "Player: " + R_Player.nickName + " "
                + "HP:" + R_Player.HP + " "
                + "MP:" + R_Player.MP + " "
                + "Level:" + R_Player.Level + " "
                + "Room:" + R_Player.CurrentRoom);

        textGraphics1.putString(topLeft.withRelative(1, 1), getPlayerPositionInfo());
    }

    static void GameScreen(Terminal terminal) throws IOException, InterruptedException {

        textGraphics = terminal.newTextGraphics();

        textGraphics1 = terminal.newTextGraphics();

        fightGraphics = terminal.newTextGraphics();

        terminal.resetColorAndSGR();
        while(keyStroke.getKeyType() != KeyType.Escape){
            R_GameLoop.InLoop();
        }
    }

    static void Drawcall(char cell) throws IOException {
        ResetTerminalPosition();

        InitNewTextGraphics(textGraphics1, topLeft);

        DrawInformation(textGraphics1, topLeft);

        DrawDungeon(textGraphics, cell);

        terminal.flush();
    }
//    public static void setRandom(Screen screen, TerminalSize terminalSize){
//        for(int column = 0; column < terminalSize.getColumns(); column++) {
//            for(int row = 0; row < terminalSize.getRows(); row++) {
//                screen.setCharacter(column, row, new TextCharacter(
//                        ' ',
//                        TextColor.ANSI.DEFAULT,
//                        // This will pick a random background color
//                        TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)]));
//            }
//        }
//    }

//    public static void CheckResize(Screen screen, TerminalSize terminalSize){
//        final int charactersToModifyPerLoop = 30;
//        for(int i = 0; i < charactersToModifyPerLoop; i++) {
//            TerminalPosition cellToModify = new TerminalPosition(
//                    random.nextInt(terminalSize.getColumns()),
//                    random.nextInt(terminalSize.getRows()));
//            TextColor.ANSI color = TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)];
//            TextCharacter characterInBackBuffer = screen.getBackCharacter(cellToModify);
//            characterInBackBuffer = characterInBackBuffer.withBackgroundColor(color);
//            characterInBackBuffer = characterInBackBuffer.withCharacter(' ');   // Because of the label box further down, if it shrinks
//            screen.setCharacter(cellToModify, characterInBackBuffer);
//        }
//        String sizeLabel = "Terminal Size: " + terminalSize;
//        TerminalPosition labelBoxTopLeft = new TerminalPosition(1, 1);
//        TerminalSize labelBoxSize = new TerminalSize(sizeLabel.length() + 2, 3);
//        TerminalPosition labelBoxTopRightCorner = labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 1);
//        TextGraphics textGraphics = screen.newTextGraphics();
//        //This isn't really needed as we are overwriting everything below anyway, but just for demonstrative purpose
//        textGraphics.fillRectangle(labelBoxTopLeft, labelBoxSize, ' ');
//        textGraphics.drawLine(
//                labelBoxTopLeft.withRelativeColumn(1),
//                labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 2),
//                Symbols.DOUBLE_LINE_HORIZONTAL);
//        textGraphics.drawLine(
//                labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(1),
//                labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(labelBoxSize.getColumns() - 2),
//                Symbols.DOUBLE_LINE_HORIZONTAL);
//        textGraphics.setCharacter(labelBoxTopLeft, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
//        textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
//        textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
//        textGraphics.setCharacter(labelBoxTopRightCorner, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
//        textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
//        textGraphics.setCharacter(labelBoxTopRightCorner.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
//        textGraphics.putString(labelBoxTopLeft.withRelative(1, 1), sizeLabel);
//    }

    public static void InitTerminal(){
        try{
            terminal = defaultTerminalFactory.createTerminal();
            terminal.clearScreen();
            terminal.flush();
            terminal.enterPrivateMode();
            terminal.clearScreen();
            terminal.setCursorVisible(false);
            GameScreen(terminal);

        }
        catch (IOException | InterruptedException e){
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
