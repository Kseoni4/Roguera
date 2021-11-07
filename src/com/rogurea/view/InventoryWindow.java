/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.input.CursorUI;
import com.rogurea.input.Input;
import com.rogurea.items.Equipment;
import com.rogurea.items.Item;
import com.rogurea.resources.Colors;

import java.util.ArrayList;
import java.util.Optional;

public class InventoryWindow extends Window {

    private final int _width = 25;
    private final int _height = 5;

    private CursorUI cursorUI;

    private final TerminalPosition _inventoryWindowPosition = new TerminalPosition(20,5);

    private final TerminalSize _inventoryWindowSize = new TerminalSize(_width, _height);

    private final ArrayList<Item> itemCollection;

    private ArrayList<Element> elements;

    public InventoryWindow(ArrayList<Item> itemCollection) {
        super();
        this.itemCollection = itemCollection;
        makeWindow(_inventoryWindowPosition, _inventoryWindowSize.withRelative(0,itemCollection.size()));
        fillElements();
        cursorUI = new CursorUI(elements);
    }

    private void fillElements(){
        elements = new ArrayList<>();
        //Debug.toLog("Size of collection: "+this.itemCollection.size());
        for(Item item : this.itemCollection){
            //Debug.toLog("Put item \n\t"+item.toString()+"\ninto element");
            elements.add(new Element(
                    item.getName(),
                    item.model.toString()+" "+item.getName()+" ["+ ((Equipment) item).getStats() +"]",
                    new Position(1,this.itemCollection.indexOf(item)),
                    () -> {
                            Dungeon.player.putUpItem(item);
                            this.itemCollection.remove(item);
                            //Debug.toLog(item.getFullInfo());
                            this.fillElements();
                            cursorUI = new CursorUI(elements);
                    }
            ));
            //elements.get(this.itemCollection.indexOf(item)).ElementPointerPosition.x++;
        }
    }

    @Override
    protected void content() {
        for (Element element :  elements){
            putElementIntoWindow(element);
        }
        if(!elements.isEmpty())
            try {
                setPointerToElement(elements.get(cursorUI.indexOfElement), cursorUI.cursorPointer);
                //Debug.toLog("Point to element: ".concat(elements.get(cursorUI.indexOfElement).ElementTitle));
            } catch (IndexOutOfBoundsException e){
                cursorUI.indexOfElement = Math.abs(cursorUI.indexOfElement - elements.size());
                setPointerToElement(elements.get(cursorUI.indexOfElement), cursorUI.cursorPointer);
            }
        else
            putStringIntoWindow("Chest is empty!", new Position(0,0));
    }

    @Override
    protected void input() {
        if(!elements.isEmpty()) {
            cursorUI.setFirstElementCursorPosition();
            while (!elements.isEmpty()) {
                Optional<KeyStroke> keyStroke = Input.waitForInput();
                if(keyStroke.isPresent()) {
                    if (Input.keyIsEscape(keyStroke.get()))
                        break;
                    cursorUI.SelectElementV(keyStroke.get().getKeyType());
                    Draw.call(this);
                }
            }
        }
        else
            super.input();
    }
}
