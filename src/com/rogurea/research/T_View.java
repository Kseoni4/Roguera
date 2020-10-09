package com.rogurea.research;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.StyleSet;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class T_View extends Thread{

    static Random random = new Random();


    static int OffsetPositionName = 1;

    static int FightMenuSizeRows = 5;

    static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    static Terminal terminal = null;

    static TextGraphics MapDrawGraphics = null;

    static TextGraphics PlayerInfoGraphics = null;

    static TextGraphics FightInfoGraphics = null;

    static KeyStroke keyStroke = KeyStroke.fromString(" ");

    static TerminalPosition topPlayerInfoLeft;

    static TerminalPosition topFightInfoLeft;

    static TerminalSize PlayerInfoSize = new TerminalSize(Resources.getPlayerPositionInfo().length() + 2, 5);

    static TerminalSize FightMenuSize = new TerminalSize(OffsetPositionName, FightMenuSizeRows);

    static int r = 255, g = 200, b = 100;

    static TextColor.RGB textColor = new TextColor.RGB(r, g, b);

    static void SetGameScreen() throws IOException {

        MapDrawGraphics = terminal.newTextGraphics();

        PlayerInfoGraphics = terminal.newTextGraphics();

        FightInfoGraphics = terminal.newTextGraphics();

        terminal.resetColorAndSGR();
    }

    public static void InitTerminal(){
        try {
            terminal = defaultTerminalFactory.createTerminal();

            terminal.flush();

            terminal.enterPrivateMode();

            terminal.clearScreen();

            terminal.setCursorVisible(false);

            SetGameScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*  Хочу кратко рассказать, как устроен рендеринг в игре. Мы нажали на стрелочку - тут же активируется функция drawcall(), которая очищает экран, заново рисует блок информации и рисует карту.
    На примере показан процесс отрисовки (я специально немного замедлил для наглядности)

    На самом деле между нажатием на стрелочку и отрисовкой карты, в памяти игры позиция нашей собачки меняется в соответствующую сторону и мы выводим новое состояние игрового поля.
    Игровое поле - это эдакая таблица, где строки и столбцы - (y,x), а 0,0 начинается в левом верхнем углу */

    static void Drawcall() throws IOException {

        terminal.clearScreen();

        ResetTerminalPosition();

        InitGraphics(PlayerInfoGraphics, topPlayerInfoLeft, PlayerInfoSize);

        DrawPlayerInformation();

        DrawDungeon();

        terminal.flush();
    }

    static void ResetTerminalPosition(){

        topPlayerInfoLeft = new TerminalPosition(R_Dungeon.CurrentRoom[0].length + 1,1);

        topFightInfoLeft = new TerminalPosition(R_Dungeon.CurrentRoom[0].length + 1,
                topPlayerInfoLeft.getRow()+1);

        FightMenuSize = new TerminalSize(OffsetPositionName, FightMenuSizeRows);
    }

    static void InitGraphics(TextGraphics textGraphics, TerminalPosition terminalPosition, TerminalSize terminalSize){
        textGraphics.fillRectangle(terminalPosition, terminalSize, ' ');
    }

    static void DrawPlayerInformation(){

        PlayerInfoGraphics.putString(topPlayerInfoLeft.withRelative(2, 1),
                Resources.getPlayerPositionInfo());

        PlayerInfoGraphics.putString(topPlayerInfoLeft.withRelative(2,2),
                "Room size: " + R_Dungeon.CurrentRoom.length + "x" + R_Dungeon.CurrentRoom[0].length
                        + " (" + Objects.requireNonNull(R_Dungeon.Rooms.stream()
                        .filter(room -> room.NumberOfRoom == R_Player.CurrentRoom)
                        .findAny().orElse(null)).roomSize + ")");

        int TextOffset = 2;

        TextColor.ANSI ansi;

        for(String info : Resources.PlayerInfo){

            PlayerInfoGraphics.putString(topPlayerInfoLeft.withRelative(TextOffset, 3), " " + info);

            TextOffset += info.length();

            switch (info) {
                case "Player: " -> {
                    info = R_Player.nickName;
                    ansi = TextColor.ANSI.GREEN;
                }
                case "HP: " -> {
                    info = String.valueOf(R_Player.HP);
                    ansi = TextColor.ANSI.RED;
                }
                case "MP: " -> {
                    info = String.valueOf(R_Player.MP);
                    ansi = TextColor.ANSI.BLUE_BRIGHT;
                }
                case "Level: " -> {
                    info = String.valueOf(R_Player.Level);
                    ansi = TextColor.ANSI.CYAN;
                }
                case "Room: " -> {
                    info = String.valueOf(R_Player.CurrentRoom);
                    ansi = TextColor.ANSI.MAGENTA;
                }
                default -> ansi = TextColor.ANSI.WHITE;
            }
            PlayerInfoGraphics.setForegroundColor(ansi);

            PlayerInfoGraphics.putString(topPlayerInfoLeft.withRelative(TextOffset, 3), info + " ");

            PlayerInfoGraphics.setForegroundColor(TextColor.ANSI.WHITE);

            TextOffset += info.length();

        }
    }

    static void DrawDungeon() throws IOException {
        char cell = MapEditor.EmptyCell;
        for (int i = 0; i < R_Dungeon.CurrentRoom.length; i++) {
            for (int j = 0; j < R_Dungeon.CurrentRoom[0].length; j++) {
                cell = R_Dungeon.ShowDungeon(i, j).charAt(0);
                if (cell == R_Player.Player) {
                    DrawPlayer(MapDrawGraphics, i, j);
                } else if (CheckCreature(cell)) {
                    DrawMob(MapDrawGraphics, i, j);
                } else if (Scans.CheckWall(cell)){
                    //PutRandomCharacter(MapDrawGraphics, i, j);
                    DrawCell(MapDrawGraphics, i, j);
                }
                else{
                    //PutRandomCharacter(MapDrawGraphics, i, j);
                    MapDrawGraphics.putString(j,i, R_Dungeon.ShowDungeon(i,j));
                }
            }
        }
    }
    static void DrawPlayer(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        textGraphics.putString(j, i, R_Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static boolean CheckCreature(char cell){

        for(R_Mob mob : R_Dungeon.CurrentRoomCreatures.keySet()){
            if(cell == mob.getCreatureSymbol())
                return true;
        }
        return false;
    }

    static void DrawMob(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        textGraphics.putString(j, i, R_Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void DrawCell(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(textColor);
        textGraphics.putString(j, i, R_Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void PutRandomCharacter(TextGraphics textGraphics, int i, int j) throws InterruptedException, IOException {
        TextColor rgb = new TextColor.RGB(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        );
        textGraphics.setForegroundColor(rgb);
        textGraphics.putString(j,i, String.valueOf(Resources.rsymbls[random.nextInt(Resources.rsymbls.length-1)]),
                SGR.ITALIC);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        Thread.sleep(30);
        terminal.flush();
    }
/*

    static void FightTextGraphics(TextGraphics fightGraphics, TerminalPosition topFightLeft){
        fightGraphics.fillRectangle(topFightLeft, FightMenuSize, ' ');
        FightMenuSize = new TerminalSize(Offset, 1);
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

        ResetFightMenuSize();
        ClearFightMenu(2);

        for(R_Mob mob : CurrentRoomMobSet){
            NameOffset[Index] = PutMobNames(mob.Name)-mob.Name.length()-3;
            Index++;
        }
        SetCursor(NameOffset[MobIndex]);
    }

    static void MenuPrompt(Set<R_Mob> CurrentRoomMobSet) throws IOException {

        int[] NameOffset = new int[CurrentRoomMobSet.size()];

        R_Mob CurrentMob;

        KeyStroke KeyMenu;

        */
/*System.out.println("In fight: \n" + "\ttopLeft: " +
                topLeft.toString() + " topFightLeft: "
                + topFightLeft.toString() + " FightMenuSize: "
                + FightMenuSize.toString() + " topRight: "
                + topRight.toString());*//*


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

        fightGraphics.putString(topFightLeft.withRelative(1, 3),
                "Creatures:");
    }






*/
}
