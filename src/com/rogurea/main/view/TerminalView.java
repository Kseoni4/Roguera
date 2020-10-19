package com.rogurea.main.view;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class TerminalView implements Runnable{

    static Random random = new Random();

    static boolean HideInv = true;

    static int OffsetPositionName = 1;

    static int FightMenuSizeRows = 5;

    static DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Terminal terminal = null;

    static TextGraphics MapDrawGraphics = null;

    static TextGraphics PlayerInfoGraphics = null;

    static TextGraphics FightInfoGraphics = null;

    static TextGraphics LoggerGraphics = null;

    static TextGraphics InventoryGraphics = null;

    static TextGraphics Controls = null;

    static int LogHistorySize = 11;

    static String[] LogHistory = new String[LogHistorySize];

    static int LogHistoryIndex = 0;

    public static KeyStroke keyStroke = KeyStroke.fromString(" ");

    static TerminalPosition topPlayerInfoLeft;

    static TerminalPosition topFightInfoLeft;

    static TerminalSize PlayerInfoSize = new TerminalSize(GameResources.getPlayerPositionInfo().length() + 2, 5);

    static TerminalSize FightMenuSize = new TerminalSize(OffsetPositionName, FightMenuSizeRows);

    static TerminalSize LogSize = new TerminalSize(256, LogHistory.length);

    static TerminalSize InventorySize = new TerminalSize(10,10);

    static TerminalPosition topLoggerLeft;

    static TerminalPosition topInventoryLeft;

    static int r = 255, g = 200, b = 100;

    static TextColor.RGB textColor = new TextColor.RGB(r, g, b);

    static void SetGameScreen() throws IOException {

        MapDrawGraphics = terminal.newTextGraphics();

        PlayerInfoGraphics = terminal.newTextGraphics();

        FightInfoGraphics = terminal.newTextGraphics();

        LoggerGraphics = terminal.newTextGraphics();

        Controls = terminal.newTextGraphics();

        InventoryGraphics = terminal.newTextGraphics();

        terminal.resetColorAndSGR();

        ResetTerminalPosition();
    }

    public static void InitTerminal(){
        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("Roguera build: " + GameResources.version);

            Font font = new Font("Px437 IBM VGA 9x16", Font.PLAIN, 24);

            SwingTerminalFontConfiguration fontConfiguration = SwingTerminalFontConfiguration.newInstance(font);

            defaultTerminalFactory.setTerminalEmulatorFontConfiguration(fontConfiguration);

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

    public void run() {
        while(keyStroke.getKeyType() != KeyType.Escape) {
            try {
                Drawcall();
                Thread.sleep(10);
            } catch (IOException | InterruptedException e) {

            }
        }
        System.out.println("Drawcall ended");
    }

    public static void Drawcall() throws IOException {

        terminal.clearScreen();

        ResetTerminalPosition();

        InitGraphics(PlayerInfoGraphics, topPlayerInfoLeft, PlayerInfoSize);

        DrawPlayerInformation();

        RedrawLog();

        DrawInventory(HideInv);

        DrawDungeon();

        Controls.putCSIStyledString(new TerminalPosition(0, terminal.getTerminalSize().getRows()-1),
                "i\u001b[48;5;57mInv\u001b[0m r\u001b[48;5;57mGenRoom\u001b[0m c\u001b[48;5;57mClrLog\u001b[0m ESC\u001b[48;5;57mQuit");
        terminal.flush();
    }

    static void ResetTerminalPosition(){

        topPlayerInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,1);

        topFightInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,
                topPlayerInfoLeft.getRow()+1);

        FightMenuSize = new TerminalSize(OffsetPositionName, FightMenuSizeRows);

        topLoggerLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 4,12);

        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+35, 12);
    }

    static void InitGraphics(TextGraphics textGraphics, TerminalPosition terminalPosition, TerminalSize terminalSize){
        textGraphics.fillRectangle(terminalPosition, terminalSize, MapEditor.EmptyCell);
    }

    static void DrawPlayerInformation(){

        PlayerInfoGraphics.putString(topPlayerInfoLeft.withRelative(2, 1),
                GameResources.getPlayerPositionInfo());

        PlayerInfoGraphics.putString(topPlayerInfoLeft.withRelative(2,2),
                "Room size: " + Dungeon.CurrentRoom.length + "x" + Dungeon.CurrentRoom[0].length
                        + " (" + Objects.requireNonNull(Dungeon.Rooms.stream()
                        .filter(room -> room.NumberOfRoom == Player.CurrentRoom)
                        .findAny().orElse(null)).roomSize + ")");

        PlayerInfoGraphics.putCSIStyledString(topPlayerInfoLeft.withRelative(2, 3),
                GameResources.UpdatePlayerInfo());

        PlayerInfoGraphics.putCSIStyledString(topPlayerInfoLeft.withRelative(2,4),
                "Equped: " + (Player.Equip.get("FirstWeapon") != null ? Colors.ORANGE + Player.Equip.get("FirstWeapon")._model : "none")
        );
    }

    static void RedrawLog(){
        InitGraphics(LoggerGraphics, topLoggerLeft, LogSize);
        DrawLogBorders();
        if(LogHistory[0] != null) {
            int i = 0;
            for (String s : LogHistory) {
                if (s != null) {
                    s = s.replaceFirst("(\\[38;5;nm)", "[38;5;" + (245+(LogHistory.length-(LogHistoryIndex*2))+i) + "m");
                    LoggerGraphics.putCSIStyledString(topLoggerLeft.withRelative(0,i),s);
                    i++;
                }
                else
                    break;
            }
        }
    }

    static void DrawLogBorders(){
        LoggerGraphics.putString(topLoggerLeft.withRow(10), "Log");
        LoggerGraphics.drawLine(topLoggerLeft.withRow(11), new TerminalPosition(topLoggerLeft.getColumn()+20,topLoggerLeft.getRow()-1), Symbols.SINGLE_LINE_HORIZONTAL);
    }

    static void DrawDungeon() throws IOException {
        char cell = MapEditor.EmptyCell;
        for (int i = 0; i < Dungeon.CurrentRoom.length; i++) {
            for (int j = 0; j < Dungeon.CurrentRoom[0].length; j++) {
                cell = Dungeon.ShowDungeon(i, j).charAt(0);
                if (cell == Player.PlayerModel) {
                    DrawPlayer(MapDrawGraphics, i, j);
                } else if (CheckCreature(cell)) {
                    DrawMob(MapDrawGraphics, i, j);
                } else if (Scans.CheckWall(cell)){
                    DrawCell(MapDrawGraphics, i, j);
/*                    if(cell == GameResources.Bot)
                    {
                        MapDrawGraphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
                        MapDrawGraphics.putString(j, i, Dungeon.ShowDungeon(i, j));
                        MapDrawGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                    }*/
                }
                else{
                    MapDrawGraphics.putString(j,i, Dungeon.ShowDungeon(i,j));
                }
            }
        }
    }

    static void DrawPlayer(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        textGraphics.putString(j, i, Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    public static boolean CheckCreature(char cell){

        for(Mob mob : Dungeon.CurrentRoomCreatures.keySet()){
            if(cell == mob.getCreatureSymbol())
                return true;
        }
        return false;
    }

    static void DrawMob(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        textGraphics.putString(j, i, Dungeon.ShowDungeon(i, j), SGR.CIRCLED);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void DrawCell(TextGraphics textGraphics, int i, int j){
        textGraphics.setForegroundColor(textColor);
        textGraphics.putString(j, i,Dungeon.ShowDungeon(i,j));
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    static void PutRandomCharacter(TextGraphics textGraphics, int i, int j) throws InterruptedException, IOException {
        TextColor rgb = new TextColor.RGB(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        );
        textGraphics.setForegroundColor(rgb);
        textGraphics.putString(j,i, String.valueOf(GameResources.rsymbls[random.nextInt(GameResources.rsymbls.length-1)]),
                SGR.ITALIC);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        Thread.sleep(30);
        terminal.flush();
    }

    static void WriteLog(StringBuilder sb){
        if(LogHistoryIndex < LogHistory.length) {
            sb.insert(0,"\u001b[38;5;n" + "m");
            LogHistory[LogHistoryIndex] = sb.toString();
//            LoggerGraphics.putString(topLoggerLeft.withRelative(0, LogHistoryIndex), sb.toString());
            LogHistoryIndex++;
        }
        else {
            ClearLog();
            sb.insert(0,"\u001b[38;5;255m");
            LogHistory[LogHistoryIndex] = sb.toString();
        }
    }

    public static void ClearLog(){
        LogHistory = new String[LogHistorySize];
        LogHistoryIndex = 0;
    }

    public static void DrawInventory(boolean hide){
        if(!hide){
            Item thisItem = InventoryMenu.GetItem();
            DrawInventoryBorders();
            int offset = 1;
            for(Item item : Player.Inventory){
                InventoryGraphics.putCSIStyledString(topInventoryLeft.withRelative(offset,1),
                        "\u001b[38;5;202m" + item._model);
                offset++;
            }
            PutCursonOnPos(InventoryMenu.CursorPos);
            ShowItemInfo(thisItem);
        }
    }

    public static void DrawInventoryBorders(){
        InventoryGraphics.putCSIStyledString(topInventoryLeft.getColumn(), topInventoryLeft.getRow()-1,
                "Inventory");
        InventoryGraphics.drawLine(
                topInventoryLeft,
                new TerminalPosition(topInventoryLeft.getColumn()+10, topInventoryLeft.getRow()),
                GameResources.InvHWallDown
        );
        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+2),
                new TerminalPosition(topInventoryLeft.getColumn()+10, topInventoryLeft.getRow()+2),
                GameResources.InvHWallUp
        );

        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+1),
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+1),
                GameResources.InvVWallRight
        );
        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn()+11, topInventoryLeft.getRow()+1),
                new TerminalPosition(topInventoryLeft.getColumn()+11, topInventoryLeft.getRow()+1),
                GameResources.InvVWallLeft
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn(),
                topInventoryLeft.getRow(),
                GameResources.InvLPCorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn()+11,
                topInventoryLeft.getRow(),
                GameResources.InvRPorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn(),
                topInventoryLeft.getRow()+2,
                GameResources.InvLDorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn()+11,
                topInventoryLeft.getRow()+2,
                GameResources.InvRDorner
        );

    }

    public static void PutCursonOnPos(TerminalPosition position){
        InventoryGraphics.setCharacter(
                position,
                GameResources.PointerUp
        );
    }

    public static void ShowItemInfo(Item item){
        if(item != null)
            InventoryGraphics.putCSIStyledString(
                    topInventoryLeft.getColumn(),
                    topInventoryLeft.getRow()+4,
                    item._model + " " + item.name + " " +
                    + item.SellPrice
            );
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
