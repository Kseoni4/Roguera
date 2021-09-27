/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.rogurea.main.input.CursorUI;
import com.rogurea.main.map.Position;

import java.util.ArrayList;

public class BasicMenu extends AbstractMenu {

    public BasicMenu(Position topLeftBasePoint, String menuTitle, TerminalSize menuSize) {
        super(topLeftBasePoint, menuTitle, menuSize);
        MenuElements = new ArrayList<>();
        MenuCursor = new CursorUI(MenuElements);
    }

    public void setPointer(char Pointer){
        this.Pointer = Pointer;
    }

    public void updateElements(){
        MenuCursor.setElements(MenuElements);
    }

    @Override
    protected void MenuElements() {

    }

    @Override
    protected void SetupElements() {

    }

    @Override
    protected void MenuContent() {

    }

    @Override
    public void openContext() {

    }
}
