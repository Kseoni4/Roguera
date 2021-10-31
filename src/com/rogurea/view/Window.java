/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.gamemap.EditorEntity;
import com.rogurea.gamemap.Position;
import com.rogurea.resources.Colors;

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

    protected TerminalSize _windowSize;

    private TerminalPosition _localZeroPoint;

    private TerminalPosition _positionOnScreen;

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
        TerminalView.drawBlockInTerminal(_windowGraphics,
                element.ElementTitle,
                _localZeroPoint.withRelative(element.getTerminalPosition().getColumn(),
                element.getTerminalPosition().getRow()));
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
        makeWindow(_positionOnScreen, _windowSize);
    }

    protected Window(){
        _windowID = ++_idCounter;
    }

    protected void makeWindow(TerminalPosition _positionOnScreen, TerminalSize _windowSize){
        this._positionOnScreen = _positionOnScreen;
        this._windowSize = _windowSize;
        _localZeroPoint = _positionOnScreen.withRelative(1,1);
    }

    protected void setPointerToElement(Element element, char pointer){
        TerminalView.setPointerIntoPosition(_windowGraphics, pointer, _localZeroPoint.withRelative(
                element.ElementPointerPosition.toTerminalPosition())
        );
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
