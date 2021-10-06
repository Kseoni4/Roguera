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

    private ArrayList<Element> inventoryElements = new ArrayList<>();

    private CursorUI inventoryCursor;

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
                                    item.getName(),
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

    private final Runnable openContextMenu = ()-> {
        redrawElementWithContextMenu();

        placePointerNearTitle();

        Draw.flush();

        selectingElementsLoop();
    };

    private void redrawElementWithContextMenu(){
        Element element = inventoryElements.get(inventoryCursor.indexOfElement);
        element.ElementTitle += " ".concat("equip ").concat("drop ").concat("back ");
        TerminalView.drawBlockInTerminal(invViewGraphics, element.ElementTitle, element.ElementPosition);
    }

    private void placePointerNearTitle(){
        clearPointer();

        String elementTitle = truncateTitle(inventoryElements.get(inventoryCursor.indexOfElement).ElementTitle);

        previousElementTitle = elementTitle;

        TerminalView.setPointerIntoPosition(invViewGraphics, '<', inventoryCursor.cursorPosition.getRelative(elementTitle.length(),0));
    }

    private void clearPointer(){
        if(!previousElementTitle.equals(""))
            TerminalView.setPointerIntoPosition(invViewGraphics, ' ', inventoryCursor.previousCursorPosition.getRelative(previousElementTitle.length(),0));
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
        inventoryIsOpen = true;

        Draw.call(this);

        inventoryCursor = new CursorUI(inventoryElements);

        inventoryCursor.setFirstElementCursorPosition();

        selectingElementsLoop();

        inventoryIsOpen = false;
        Draw.reset(this);
    }

    private void selectingElementsLoop(){
        while(true){
            KeyStroke key = waitForInput();
            if(keyNotNull(key)){
                if(keyIsEscape(key)){
                    break;
                }
                inventoryCursor.SelectElementV(key.getKeyType());
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
        this.invViewGraphics.fillRectangle(new TerminalPosition(inventoryPosition.x, inventoryPosition.y), new TerminalSize(50,5), ' ');
        Draw.call(this);
    }
}
