/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.input.CursorUI;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.TerminalView;
import com.rogurea.main.view.ViewObjects;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static com.rogurea.main.view.ViewObjects.controlBlock;
import static com.rogurea.main.view.ViewObjects.gameMapBlock;

public abstract class AbstractMenu implements IViewBlock, IMenu, Serializable {

    private final TerminalPosition TopLeftBasePoint;

    private TerminalPosition RelativeTopLeftPoint;

    protected TerminalPosition InnerTopLeftPoint;

    private final String MenuTitle;

    private TextGraphics MenuGraphics;

    protected final TerminalSize MenuSize;

    private final TerminalSize MenuBorderSize;

    protected boolean UIHide = true;

    protected boolean UIContextHide = true;

    protected char Pointer;

    protected char ContextPointer;

    protected ArrayList<Element> MenuElements;

    protected CursorUI MenuCursor;

    public AbstractMenu(Position topLeftBasePoint, String menuTitle, TerminalSize menuSize) {
        TopLeftBasePoint = new TerminalPosition(topLeftBasePoint.x, topLeftBasePoint.y);
        MenuTitle = menuTitle;
        MenuSize = menuSize;
        MenuBorderSize = menuSize;
        InnerTopLeftPoint = TopLeftBasePoint.withRelative(1,1);
        ViewObjects.ViewBlocks.add(this);
    }

    @Override
    public void show() {
        UpdatePosition();

        SetupElements();

        MenuCursor.setDefaultCursorPosition();

        UIHide = Enable();

        SetInputIntoMenu();

        Draw.reset(this);

        TerminalView.ReDrawAll(new IViewBlock[]{this, gameMapBlock, controlBlock});
    }

    protected boolean Enable(){
        return false;
    }

    private void SetInputIntoMenu(){
        while (!UIHide){
            Draw.call(this);

            MenuElements();
        }
    }

    protected void PutElementOnPosition(Element element){
        MenuGraphics.putCSIStyledString(element.getTerminalPosition(), element.ElementTitle);
    }

    protected void PutPointerNearElement(CursorUI cursor){
        try {
            TerminalView.SetPointerIntoPosition(MenuGraphics, cursor.CursorPonter, new TerminalPosition(
                    cursor.CursorPosition.x, cursor.CursorPosition.y
            ));
        }catch (Exception e){
            Debug.log("UI ERROR: setting pointer has failed");
            e.printStackTrace();
        }
    }

    protected void MenuElements(){

    }


    protected void MenuContextContent(){

    }

    protected boolean Disable(){
        return true;
    }

    @Override
    public void Init() {
        try{
            MenuGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            Debug.log("UI ERROR: Cannot setting up text graphics for menu: " + MenuTitle);
            e.printStackTrace();
        }
    }

    protected void SetBackgroundColor(TextColor BackgroundColor){
        MenuGraphics.setBackgroundColor(BackgroundColor);
    }

    protected void PutStringOnPosition(String s, Position position){
        TerminalView.DrawBlockInTerminal(MenuGraphics, s, new TerminalPosition(position.x, position.y));
    }

    protected void PutStringOnPosition(String s, TerminalPosition position){
        TerminalView.DrawBlockInTerminal(MenuGraphics, s, position);
    }

    protected void PutCharacterOnPosition(TerminalPosition position, char character){
        MenuGraphics.setCharacter(position, character);
    }

    protected void FillSpaceByEmpty(TerminalPosition StartPosition, TerminalSize SpaceSize){
        MenuGraphics.fillRectangle(StartPosition, SpaceSize, MapEditor.EmptyCell);
    }

    protected void FillSpaceByChar(TerminalPosition StartPosition, TerminalSize SpaceSize, char Symbol){
        MenuGraphics.fillRectangle(StartPosition, SpaceSize, Symbol);
    }

    protected void DrawCustomBorder(TerminalPosition StartPosition, TerminalSize SpaceSize, char Symbol){
        MenuGraphics.drawRectangle(StartPosition, SpaceSize, Symbol);
    }

    protected void DrawCustomLine(TerminalPosition StartPoint, TerminalPosition EndPoint, char Symbol){
        MenuGraphics.drawLine(StartPoint, EndPoint, Symbol);
    }

    protected void SetupElements(){

    }

    @Override
    public void Draw() {
        if (!UIHide) {

            TerminalView.InitGraphics(MenuGraphics, RelativeTopLeftPoint, MenuSize);

            DrawBorders();

            MenuContent();

            if(!UIContextHide){
                MenuContextContent();
            }
        }
        else{
            Draw.reset(this);
        }
    }

    private void UpdatePosition(){
        RelativeTopLeftPoint = TopLeftBasePoint.withRelative(Dungeon.GetCurrentRoom().RoomStructure[0].length, 0);
        InnerTopLeftPoint = RelativeTopLeftPoint.withRelative(1,1);
    }

    private void DrawBorders(){
        MenuGraphics.setBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"))
                .fillRectangle(RelativeTopLeftPoint, MenuSize, MapEditor.EmptyCell)
                .drawRectangle(RelativeTopLeftPoint, MenuBorderSize, '█')
                .putCSIStyledString(RelativeTopLeftPoint,Colors.WHITE_BRIGHT + MenuTitle);
    }

    protected void MenuContent(){

    }

    @Override
    public void Reset() {
        MenuGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        UpdatePosition();

        TerminalView.InitGraphics(MenuGraphics, RelativeTopLeftPoint, MenuSize);
    }
}
