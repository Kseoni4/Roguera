/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.view.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.input.CursorUI;
import com.rogurea.dev.input.Input;
import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.*;

import java.io.IOException;
import java.util.ArrayList;

public class InventoryView implements IViewBlock {

    private TextGraphics invViewGraphics;

    public Position inventoryPosition;

    private boolean inventoryIsOpen = false;

    private boolean contextMenuIsOpen = false;

    private ArrayList<Element> inventoryElements = new ArrayList<>();
    
    private ArrayList<Element> contextMenuElements = new ArrayList<>();


    private CursorUI inventoryCursor;

    private CursorUI inventoryContextCursor;

    private String previousElementTitle = "";

    @Override
    public void Init() {
        try{
            invViewGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Draw() {
        drawEquipment();
        drawItems();
        if(contextMenuIsOpen){
            drawContextMenu();
        }
    }

    private void drawEquipment(){
        int i = 0;
        if(!Dungeon.player.Equipment.isEmpty()) {
            for (String key : Dungeon.player.Equipment.keySet()) {
                Equipment eq = Dungeon.player.Equipment.get(key);
                placeModels(eq.model, key, (double) eq.getStats(), i);
                i++;
            }
        }
    }

    private void drawItems(){
        inventoryElements = new ArrayList<>();
        if(!Dungeon.player.Inventory.isEmpty()){
            for (Item item : Dungeon.player.Inventory){
                Position pos = new Position().getRelative(
                        inventoryPosition.getRelative(
                            Dungeon.player.Inventory.indexOf(item) > 5 ? 23 : 22,
                            Dungeon.player.Inventory.indexOf(item)
                ));
                inventoryElements.add(
                            new Element(
                                    String.valueOf(item.id),
                                    (inventoryIsOpen ? item.model.toString()+" "+item.getName()+" ["+((Equipment)item).getStats().intValue()+"]" : item.model.toString()),
                                    pos,
                                    openContextMenu
                                    )
                );
                TerminalView.drawBlockInTerminal(invViewGraphics, inventoryElements.get(Dungeon.player.Inventory.indexOf(item)).ElementTitle, pos);
                //TerminalView.putCharInTerminal(invViewGraphics, item.model.get(), pos);
            }
            if(inventoryIsOpen && inventoryCursor != null){
                placePointerNearTitle();
            }
        }
    }

    private void drawContextMenu(){
        Element itemElement = inventoryElements.get(inventoryCursor.indexOfElement);
        int offsetX = 0;
        for(Element menuOption : contextMenuElements){
            TerminalView.drawBlockInTerminal(invViewGraphics, menuOption.ElementTitle, menuOption.ElementPosition);
            offsetX += menuOption.ElementTitle.length()+1;
        }
        placePointerNearTitle(itemElement, offsetX);

        clearContextPointer();
        TerminalView.setPointerIntoPosition(invViewGraphics,
                '^',
                contextMenuElements.get(inventoryContextCursor.indexOfElement).ElementPointerPosition);
    }

    private void clearContextPointer(){
        TerminalView.setPointerIntoPosition(invViewGraphics, ' ', inventoryContextCursor.previousCursorPosition);
    }

    private final Runnable openContextMenu = this::openContextMenuToInput;

    private void openContextMenuToInput(){
        contextMenuIsOpen = true;

        contextMenuElements.clear();

        placePointerNearTitle();

        assembleContextOptions();

        inventoryContextCursor = new CursorUI(contextMenuElements);

        inventoryContextCursor.cursorPointer = '^';

        inventoryContextCursor.setFirstElementCursorPosition();

        Draw.call(this);

        selectingElementsLoop();

        contextMenuIsOpen = false;

        Draw.reset(this);
    }


    private void assembleContextOptions(){
        Element itemElement = inventoryElements.get(inventoryCursor.indexOfElement);

        int offsetX = 0;

        contextMenuElements.add(new Element(
                "Context Menu of item " + itemElement.ElementName,
                "Equip",
                itemElement.ElementPosition.getRelative(new Position(truncateTitle(itemElement.ElementTitle).length()+offsetX,0)),
                ()->{
                    Dungeon.player.putUpItem(Dungeon.player.Inventory.get(inventoryCursor.indexOfElement));
                }
        ));

        offsetX += contextMenuElements.get(0).ElementTitle.length()+1;

        contextMenuElements.add(new Element(
                "Context Menu of item " + itemElement.ElementName,
                "Drop",
                itemElement.ElementPosition.getRelative(new Position(truncateTitle(itemElement.ElementTitle).length()+offsetX,0)),
                ()->{
                    Dungeon.getCurrentRoom().getCell(Dungeon.player.playerPosition.getRelative(1,0)).putIntoCell(
                            Dungeon.player.Inventory.remove(inventoryCursor.indexOfElement)
                    );
                    drawItems();

                }
        ));

        offsetX += contextMenuElements.get(1).ElementTitle.length()+1;

        contextMenuElements.add(new Element(
                "Context Menu of item " + itemElement.ElementName,
                "Back",
                itemElement.ElementPosition.getRelative(new Position(truncateTitle(itemElement.ElementTitle).length()+offsetX,0)),
                ()->{}
        ));

        contextMenuElements.forEach(element -> element.ElementPointerPosition = element.ElementPointerPosition.getRelative(new Position(1,1)));
    }

    private void placePointerNearTitle(){
        clearPointer(inventoryCursor);

        String elementTitle = truncateTitle(inventoryElements.get(inventoryCursor.indexOfElement).ElementTitle);

        previousElementTitle = elementTitle;

        TerminalView.setPointerIntoPosition(invViewGraphics, '<', inventoryCursor.cursorPosition.getRelative(elementTitle.length(),0));
    }

    private void placePointerNearTitle(Element element, int offsetX){
        clearPointer(inventoryCursor);

        String elementTitle = truncateTitle(element.ElementTitle);

        previousElementTitle = elementTitle;

        TerminalView.setPointerIntoPosition(invViewGraphics, '<', inventoryCursor.cursorPosition.getRelative(new Position(elementTitle.length()+offsetX,0)));
    }

    private void clearPointer(CursorUI cursor){
        if(!previousElementTitle.equals(""))
            TerminalView.setPointerIntoPosition(invViewGraphics, ' ', cursor.previousCursorPosition.getRelative(previousElementTitle.length(),0));
    }

    private String truncateTitle(String elementTitle){
        return elementTitle.replaceAll("\\[\\d{2};\\d{1};\\d{3}m", "")
                .replaceAll("\\[0m","")
                .trim();
    }

    private void placeModels(Model m, String place, double d, int y){
        String msg = "".concat("[").concat("%").concat("]").concat("_dmg_");
        TerminalView.drawBlockInTerminal(invViewGraphics, msg, inventoryPosition.x, inventoryPosition.y+y);
        TerminalView.drawBlockInTerminal(invViewGraphics, "|"+d+"|".concat(place), inventoryPosition.getRelative(msg.indexOf("_dmg_"),0).x, inventoryPosition.y+y);
        TerminalView.putCharInTerminal(invViewGraphics,m.get(),inventoryPosition.getRelative(msg.indexOf("%"),0).x, inventoryPosition.y+y);
    }

    public void openToInput(){
        if(!Dungeon.player.Inventory.isEmpty()) {
            inventoryIsOpen = true;

            Draw.call(this);

            inventoryCursor = new CursorUI(inventoryElements);

            inventoryCursor.setFirstElementCursorPosition();

            selectingElementsLoop();

            inventoryIsOpen = false;
            Draw.reset(this);
        }
    }

    private void selectingElementsLoop(){
        while(true){
            KeyStroke key = waitForInput();
            if(keyNotNull(key)){
                if(keyIsEscape(key)){
                    break;
                }
                if(!contextMenuIsOpen) {
                    inventoryCursor.SelectElementV(key.getKeyType());
                }
                else{
                    inventoryContextCursor.SelectElementH(key.getKeyType());
                }
                Draw.call(this);
            }
        }
    }

    private KeyStroke waitForInput(){
        return Input.GetKey();
    }

    private boolean keyNotNull(KeyStroke key){
        return key != null;
    }

    private boolean keyIsEscape(KeyStroke key){
        return key.getKeyType().equals(KeyType.Escape);
    }

    @Override
    public void Reset() {
        this.invViewGraphics.fillRectangle(new TerminalPosition(inventoryPosition.x, inventoryPosition.y), new TerminalSize(60,5), ' ');
        Draw.call(this);
    }
}
