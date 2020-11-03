package com.rogurea.main.view;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;

import java.io.IOException;
import java.util.Random;

import static java.lang.Thread.sleep;

public class TerminalView implements Runnable {

    public static TerminalPosition CurrentPointerPosition;

    private static final Random random = new Random();

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Terminal terminal = null;

    public static KeyStroke keyStroke = KeyStroke.fromString(" ");

    private static void SetGameScreen() throws IOException {

        GameMapBlock.Init();

        PlayerInfoBlock.Init();

        LogBlock.Init();

        InventoryMenu.Init();

        ControlBlock.Init();

        terminal.resetColorAndSGR();
    }

    public static void InitTerminal() {
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
        while (keyStroke == null || keyStroke.getKeyType() != KeyType.Escape) {
            try {
                Drawcall();
                sleep(GameVariables.DCI);
            } catch (IOException | InterruptedException e) {
                break;
            }
        }
        System.out.println("Drawcall ended");
    }

    public static void ResetPositions(){
        PlayerInfoBlock.Reset();

        InventoryMenu.Reset();

        LogBlock.Reset();
    }

    private static void Drawcall() throws IOException {

        terminal.clearScreen();

        PlayerInfoBlock.GetInfo();

        GameMapBlock.DrawDungeon();

        LogBlock.RedrawLog();

        InventoryMenu.DrawInventory();

        ControlBlock.DrawControls();

        terminal.flush();
    }

    static void InitGraphics(TextGraphics textGraphics, TerminalPosition terminalPosition, TerminalSize terminalSize) {
        textGraphics.fillRectangle(terminalPosition, terminalSize, MapEditor.EmptyCell);
    }

    static void DrawBlockInTerminal(TextGraphics textgui, String data, TerminalPosition position) {
        textgui.putCSIStyledString(position, data);
    }

    static void DrawBlockInTerminal(TextGraphics textgui, String data, int x, int y) {
        textgui.putCSIStyledString(x, y, data);
    }

    static void PutCharInTerminal(TextGraphics textgui, TextCharacter data, int x, int y){
        textgui.setCharacter(x,y,data);
    }

    public static void SetPointerIntoPosition(TextGraphics textgui, char pointer, TerminalPosition position) {
        textgui.setCharacter(position, pointer);
    }

    static void PutRandomCharacter(TextGraphics textGraphics, int i, int j) throws InterruptedException, IOException {
        TextColor rgb = new TextColor.RGB(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        );
        textGraphics.setForegroundColor(rgb);
        textGraphics.putString(j, i, String.valueOf(GameResources.rsymbls[random.nextInt(GameResources.rsymbls.length - 1)]),
                SGR.ITALIC);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        sleep(30);
        terminal.flush();
    }
}
