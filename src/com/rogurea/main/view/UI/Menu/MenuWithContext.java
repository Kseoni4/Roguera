/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.rogurea.main.input.CursorUI;
import com.rogurea.main.map.Position;
import com.rogurea.main.resources.GameResources;

import java.util.ArrayList;

public class MenuWithContext extends AbstractMenu {

    protected ArrayList<Element> MenuContextElements;

    protected CursorUI ContextCursor;

    public MenuWithContext(Position topLeftBasePoint, String menuTitle, TerminalSize menuSize) {
        super(topLeftBasePoint, menuTitle, menuSize);
        MenuElements = new ArrayList<>();
        MenuContextElements = new ArrayList<>();
        MenuCursor = new CursorUI(MenuElements);
        ContextCursor = new CursorUI(MenuContextElements);
        setMainPointer(GameResources.GetModel("PointerUp"));
        MenuCursor.CursorPonter = Pointer;
    }

    public void setMainPointer(char Pointer){
        this.Pointer = Pointer;
    }

    public void setContextPointer(char Pointer) {
        this.ContextPointer = Pointer;
    }

    public void updateElements(){
        MenuCursor.setElements(MenuElements);
    }

    public void setContextElements(){
        ContextCursor.setElements(MenuContextElements);
    }

    @Override
    protected void MenuElements() {

    }

    protected void MenuContext() {

    }

    @Override
    protected void MenuContextContent() {

    }

    public void openContext() {

    }

    @Override
    protected void SetupElements() {

    }

    @Override
    protected void MenuContent() {

    }
}
