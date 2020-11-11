package com.rogurea.main.view;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;
import java.io.IOException;

import static com.rogurea.main.resources.ViewObject.*;

public class TerminalView {

    public static TerminalPosition CurrentPointerPosition;

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    public static Terminal terminal = null;

    public static KeyStroke keyStroke = KeyStroke.fromString(" ");

    private static void SetGameScreen() throws IOException {

        Draw.init(gameMapBlock);
        Draw.init(playerInfoBlock);
        Draw.init(logBlock);
        Draw.init(inventoryMenu);
        Draw.init(controlBlock);

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
    public static void ReDrawAll(){

        try {
            terminal.clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResetPositions();

        Draw.call(playerInfoBlock);
        Draw.call(gameMapBlock);
        Draw.call(controlBlock);
        Draw.call(inventoryMenu);
        Draw.call(logBlock);
    }

    private static void ResetPositions(){
        Draw.reset(playerInfoBlock);
        Draw.reset(inventoryMenu);
        Draw.reset(logBlock);
        Draw.reset(controlBlock);
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

    public static void SetPointerIntoPosition(TextGraphics textgui, char pointer, TerminalPosition position) {
        textgui.setCharacter(position, pointer);
    }

    /*static void PutRandomCharacter(TextGraphics textGraphics, int i, int j) throws InterruptedException, IOException {
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
    }*/
}
