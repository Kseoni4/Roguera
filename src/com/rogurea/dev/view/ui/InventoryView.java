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

    private CursorUI contextCursor;

    private String previousElementTitle = "";

    private final String[] menuOptions = {"Equip", "Drop", "Back"};

    private final Runnable equip = new Runnable() {
        @Override
        public void run() {
            int index = inventoryCursor.indexOfElement;
            Dungeon.player.equipItemIntoFirstSlot((Equipment) Dungeon.player.Inventory.get(index));
            inventoryElements.remove(index);
            drawItems();
        }
    };

    private final Runnable drop = new Runnable() {
        @Override
        public void run() {
            int index = inventoryCursor.indexOfElement;
            Dungeon.getCurrentRoom().getCell(Dungeon.player.playerPosition.getRelative(1,0)).putIntoCell(Dungeon.player.Inventory.remove(index));
            inventoryElements.remove(index);
            drawItems();
            ViewObjects.mapView.drawAround();
        }
    };

    private final Runnable back = () -> {contextMenuIsOpen = false; Draw.reset(this);};

    private final Runnable[] menuActions = {equip, drop, back};

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
        drawContextMenu();
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
                placePointerNearTitle(inventoryElements.get(inventoryCursor.indexOfElement).ElementTitle, inventoryCursor);
            }
        }
    }

    private void drawContextMenu(){
        if(contextMenuIsOpen) {
            int offsetX;
            Element element = inventoryElements.get(inventoryCursor.indexOfElement);
            String elementTitle = truncateTitle(element.ElementTitle);
            for (int i = 0; i < menuOptions.length; i++) {

                offsetX = elementTitle.length() + ((menuOptions[i].length() * i*2));
                contextMenuElements.add(new Element(
                        menuOptions[i], menuOptions[i],
                        element.ElementPosition.getRelative(offsetX, 0),
                        menuActions[i]));
                TerminalView.drawBlockInTerminal(invViewGraphics, contextMenuElements.get(i).ElementTitle, contextMenuElements.get(i).ElementPosition);
            }
            clearPointer(previousElementTitle, inventoryCursor.previousCursorPosition);
            TerminalView.setPointerIntoPosition(invViewGraphics, inventoryCursor.cursorPointer, contextMenuElements.get(contextMenuElements.size()-1).ElementPosition.getRelative(4,0));
            if(contextCursor != null)
                placePointerUnderTitle(contextCursor);
        }
    }

    private final Runnable openContextMenu = this::openContextInput;

    private void placePointerNearTitle(String elementTitle, CursorUI currentCursor){
        clearPointer(previousElementTitle, currentCursor.previousCursorPosition);

        elementTitle = truncateTitle(elementTitle);

        previousElementTitle = elementTitle;

        TerminalView.setPointerIntoPosition(invViewGraphics, currentCursor.cursorPointer, currentCursor.cursorPosition.getRelative(elementTitle.length(),0));

    }

    private void placePointerUnderTitle(CursorUI currentCursor){
        clearPointer("", currentCursor.previousCursorPosition);

        TerminalView.setPointerIntoPosition(invViewGraphics, currentCursor.cursorPointer, currentCursor.cursorPosition.getRelative(1,1));
    }

    private void clearPointer(String previousElementTitle, Position previousCursorPosition){
        if(!previousElementTitle.equals(""))
            TerminalView.setPointerIntoPosition(invViewGraphics, ' ', previousCursorPosition.getRelative(previousElementTitle.length(),0));
        else if(contextMenuIsOpen){
            TerminalView.setPointerIntoPosition(invViewGraphics, ' ', previousCursorPosition.getRelative(1,1));
        }
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

            /*Draw.call(this);*/

            drawItems();

            inventoryCursor = new CursorUI(inventoryElements);

            inventoryCursor.cursorPointer = '<';

            inventoryCursor.setFirstElementCursorPosition();

            placePointerNearTitle(inventoryElements.get(inventoryCursor.indexOfElement).ElementTitle, inventoryCursor);

            Draw.flush();

            selectingElementsLoop();

            inventoryIsOpen = false;
            Draw.reset(this);
        }
    }

    public void openContextInput(){
        contextMenuIsOpen = true;
        contextMenuElements = new ArrayList<>();
        drawContextMenu();
        contextCursor = new CursorUI(contextMenuElements);

        contextCursor.cursorPointer = '^';

        contextCursor.setFirstElementCursorPosition();

        placePointerUnderTitle(contextCursor);

        Draw.flush();
        selectingElementsLoop();
        contextMenuIsOpen = false;
        clearPointer("",contextCursor.previousCursorPosition);
        Draw.reset(this);
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
                } else{
                    contextCursor.SelectElementH(key.getKeyType());
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
        this.invViewGraphics.fillRectangle(new TerminalPosition(inventoryPosition.x, inventoryPosition.y), new TerminalSize(70,6), ' ');
        Draw.call(this);
    }
}
