package com.rogurea.dev.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.EditorEntity;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.resources.Colors;

import java.io.IOException;

/**
 * Базовый абстрактный класс окна, работает непосредственно с методами
 * отрисовки графики фреймворка Lanterna.
 * Окно любого класса должно быть наследовано от {@code Window} и переопределять методы
 * содержимого {@code Content()} и способа ввода {@code Input()}.
 */
public abstract class Window implements IViewBlock {

    private final int _windowID;

    private static int _idCounter = 0;

    protected final TerminalSize _windowSize;

    private final TerminalPosition _localZeroPoint;

    private final TerminalPosition _positionOnScreen;

    private TextGraphics _windowGraphics;

    private TextCharacter borderStyle = new TextCharacter('█');

    private TextColor _bgColor = Colors.GetTextColor(Colors.BLACK);

    public void show(){
        Draw.init(this);
        Draw.call(this);
        input();
        Draw.reset(this);
        TerminalView.reDrawAll(IViewBlock.empty);
    }

    protected void content(){

    }

    protected void input(){
        try {
            TerminalView.terminal.readInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void putStringIntoWindow(String s, Position position){
        TerminalView.drawBlockInTerminal(_windowGraphics, s, _localZeroPoint.withRelative(position.x, position.y));
    }

    protected void putElementIntoWindow(Element element){
        _windowGraphics.putCSIStyledString(element.getTerminalPosition(), element.ElementTitle);
    }

    protected void setBackgroundColor(String color){
        _bgColor = Colors.GetTextColor(color);
    }

    protected void setBorderStyle(char borderModel){
        setBorderStyle(borderModel, Colors.GREY_241);
    }

    protected void setBorderStyle(String color){
        setBorderStyle(borderStyle.getCharacter(), color);
    }

    protected void setBorderStyle(char borderModel, String color){
        borderStyle = borderStyle.withCharacter(borderModel).withForegroundColor(Colors.GetTextColor(color));
    }

    private void drawBorder(){
        _windowGraphics.setBackgroundColor(_bgColor)
                .fillRectangle(_positionOnScreen, _windowSize, EditorEntity.EMPTY_CELL.getModel())
                .drawRectangle(_positionOnScreen, _windowSize, borderStyle)
                .putCSIStyledString(_positionOnScreen.withRelative(3,0), Colors.WHITE_BRIGHT + _windowID);
    }

    private void clearWindow(){
        this._windowGraphics.fillRectangle(_positionOnScreen, _windowSize, EditorEntity.EMPTY_CELL.getModel());
    }

    protected Window(TerminalPosition _positionOnScreen, TerminalSize _windowSize) {
        _windowID = ++_idCounter;
        this._positionOnScreen = _positionOnScreen;
        this._windowSize = _windowSize;
        _localZeroPoint = _positionOnScreen.withRelative(1,1);
    }

    @Override
    public void Init() {
        try {
            _windowGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Draw() {
        drawBorder();

        content();
    }

    @Override
    public void Reset() {
        clearWindow();
    }
}
