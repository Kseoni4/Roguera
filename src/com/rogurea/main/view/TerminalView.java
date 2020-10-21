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
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

import java.io.IOException;
import java.util.Random;

public class TerminalView implements Runnable{

    private static final Random random = new Random();

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Terminal terminal = null;

    static TextGraphics Controls = null;

    public static KeyStroke keyStroke = KeyStroke.fromString(" ");

    static int r = 255, g = 200, b = 100;

    static void SetGameScreen() throws IOException {

        GameMapBlock.Init();

        PlayerInfoBlock.Init();

        LogBlock.Init();

        InventoryMenu.Init();

        Controls = terminal.newTextGraphics();

        terminal.resetColorAndSGR();

        ResetTerminalPosition();
    }

    public static void InitTerminal(){
        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("Roguera build: " + GameResources.version);

            SwingTerminalFontConfiguration fontConfiguration = SwingTerminalFontConfiguration.newInstance(GameResources.TerminalFont);

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

  /*
  Хочу кратко рассказать, как устроен рендеринг в игре. Мы нажали на стрелочку - тут же активируется функция drawcall(), которая очищает экран, заново рисует блок информации и рисует карту.
    На примере показан процесс отрисовки (я специально немного замедлил для наглядности)

    На самом деле между нажатием на стрелочку и отрисовкой карты, в памяти игры позиция нашей собачки меняется в соответствующую сторону и мы выводим новое состояние игрового поля.
    Игровое поле - это эдакая таблица, где строки и столбцы - (y,x), а 0,0 начинается в левом верхнем углу

*/
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

        PlayerInfoBlock.GetInfo();

        GameMapBlock.DrawDungeon();

        LogBlock.RedrawLog();

        InventoryMenu.DrawInventory();

        Controls.putCSIStyledString(new TerminalPosition(0, terminal.getTerminalSize().getRows()-1),
                "i\u001b[48;5;57mInv\u001b[0m r\u001b[48;5;57mGenRoom\u001b[0m c\u001b[48;5;57mClrLog\u001b[0m ESC\u001b[48;5;57mQuit");
        terminal.flush();
    }

    static void ResetTerminalPosition(){

    }

    static void InitGraphics(TextGraphics textGraphics, TerminalPosition terminalPosition, TerminalSize terminalSize){
        textGraphics.fillRectangle(terminalPosition, terminalSize, MapEditor.EmptyCell);
    }

    static void DrawBlockInTerminal(TextGraphics textgui, String data, TerminalPosition position){
        textgui.putCSIStyledString(position, data);
    }

    static void DrawBlockInTerminal(TextGraphics textgui, String data, int x, int y){
        textgui.putCSIStyledString(x, y, data);
    }

    static void SetPointerIntoPosition(TextGraphics textgui, char pointer, TerminalPosition position){
        textgui.setCharacter(position, pointer);
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

        fightGraphics.putString(topFightLeft.withRelative(1, 3),
                "Creatures:");
    }*/








}
