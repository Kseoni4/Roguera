/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.view.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.items.*;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;
import com.rogurea.view.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.rogurea.view.ViewObjects.logView;

public class InventoryView implements IViewBlock {
    public Position inventoryPosition;

    private TextGraphics invViewGraphics;

    private ArrayList<Element> quickEqpElements;

    private int indexOfSlot;

    public void useItem(int index){
        if(!quickEqpElements.isEmpty()) {
            this.indexOfSlot = index;
            getElement(indexOfSlot).ifPresentOrElse(this::elementActionRun, ()-> logView.putLog("Quick slot is empty!"));
        } else {
            logView.putLog("Quick slots is empty!");
        }
        Draw.call(this);
    }

    private void elementActionRun(Element element){
        element.ElementAction.run();
    }

    private Optional<Element> getElement(int indexOfSlot){
        try {
            return Optional.ofNullable(quickEqpElements.get(indexOfSlot));
        } catch (IndexOutOfBoundsException e){
            return Optional.empty();
        }
    }

    private final Runnable accessItem = () -> {
        Equipment eq = Dungeon.player.quickEquipment.get(indexOfSlot);
        if(eq instanceof Usable){
            Draw.reset(this);

            ((Usable) eq).use();

            logView.playerAction("have used a "+eq.model.getModelColorName() +"!");

            Dungeon.player.quickEquipment.remove(eq);
        } else {
            Dungeon.player.equipItemFromQuickSlot(eq);
            Draw.reset(this);
        }
        Draw.reset(this);
    };

    @Override
    public void Init() {
        try {
            invViewGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Draw() {
        Reset();
        drawEquipment();
        drawItemQuickSlots();
        drawItems();
    }

    private void drawEquipment() {
        int i = 0;
        if (!Dungeon.player.Equipment.isEmpty()) {
            for (String key : Dungeon.player.Equipment.keySet()) {
                Equipment eq = Dungeon.player.getEquipmentFromSlot(key).orElse(Equipment.BLANK);
                placeModels(eq.model, key, eq.getStats(), i);
                i++;
            }
        }
    }

    private void placeModels(Model m, String place, int d, int y) {
        String msg = "".concat("[").concat("%").concat("]").concat("_dmg_");
        TerminalView.drawBlockInTerminal(invViewGraphics, msg, inventoryPosition.x, inventoryPosition.y + y);
        TerminalView.drawBlockInTerminal(invViewGraphics, "|" + d + "|"+"("+m.getModelName()+")", inventoryPosition.getRelative(msg.indexOf("_dmg_"), 0).x, inventoryPosition.y + y);
        TerminalView.putCharInTerminal(invViewGraphics, m.get(), inventoryPosition.getRelative(msg.indexOf("%"), 0).x, inventoryPosition.y + y);
    }

    private void drawItemQuickSlots(){
        int i = 0;
        for(;i < 5; i++){
            String msg = "".concat("[").concat(""+(i+1)).concat("]");
            TerminalView.drawBlockInTerminal(invViewGraphics, msg, inventoryPosition.getRelative(32,i));
        }
    }

    private void drawItems(){
        quickEqpElements = new ArrayList<>();
        if(!Dungeon.player.quickEquipment.isEmpty()){
            List<Equipment> quickEqpList = Dungeon.player.quickEquipment;
            for(Equipment eqp : quickEqpList){
                quickEqpElements.add(new Element(
                        eqp.getName(),
                        eqp.model.toString(),
                        inventoryPosition.getRelative(35, quickEqpList.indexOf(eqp)),
                        accessItem
                ));
                Element element = quickEqpElements.get(quickEqpList.indexOf(eqp));
                TerminalView.drawBlockInTerminal(invViewGraphics, eqp.model.toString() +" "+eqp.model.getModelColorName() + " " + "[+"+eqp.getStats()+"]"+ Colors.R, element.ElementPosition);
            }
        }
    }

    @Override
    public void Reset() {
        invViewGraphics.fillRectangle(inventoryPosition.toTerminalPosition(), new TerminalSize(TerminalView.windowWight*2,5), ' ');
    }

    /*private TextGraphics invViewGraphics;


    private boolean inventoryIsOpen = false;

    private boolean contextMenuIsOpen = false;

    private ArrayList<Element> inventoryElements = new ArrayList<>();

    private ArrayList<Element> contextMenuElements = new ArrayList<>();

    private CursorUI inventoryCursor;

    private CursorUI contextCursor;

    private String previousElementTitle = "";

    private final String[] menuOptions = {"Equip", "Drop", "Back"};

    private final Runnable equip = ()-> {
        int index = inventoryCursor.indexOfElement;
        Item item = Dungeon.player.Inventory.get(index);
        Dungeon.player.equipItemIntoFirstSlot((Equipment) item);
        Dungeon.player.Inventory.remove(item);
        inventoryElements.remove(index);
        Draw.reset(this);
    };

    private KeyStroke invKey;

    private final Runnable drop = new Runnable() {
        @Override
        public void run() {
            int index = inventoryCursor.indexOfElement;
            Dungeon.getCurrentRoom().getCell(Dungeon.player.playerPosition.getRelative(1, 0)).putIntoCell(Dungeon.player.Inventory.remove(index));
            inventoryElements.remove(index);
            drawItems();
            Draw.flush();
            ViewObjects.mapView.drawAround();
        }
    };

    private final Runnable back = () -> {
        contextMenuIsOpen = false;
        Draw.reset(this);
    };

    private final Runnable[] menuActions = {equip, drop, back};

    @Override
    public void Init() {
        try {
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
        drawItemCount();
    }

    private void drawEquipment() {
        int i = 0;
        if (!Dungeon.player.Equipment.isEmpty()) {
            for (String key : Dungeon.player.Equipment.keySet()) {
                Equipment eq = Dungeon.player.getEquipmentFromSlot(key).orElse(Equipment.BLANK);
                placeModels(eq.model, key, (double) eq.getStats(), i);
                i++;
            }
        }
    }

    private void drawItems() {
        inventoryElements = new ArrayList<>();
        if (!Dungeon.player.Inventory.isEmpty()) {
            for (Item item : Dungeon.player.Inventory) {
                Position pos = new Position().getRelative(
                        inventoryPosition.getRelative(
                                Dungeon.player.Inventory.indexOf(item) > 5 ? 23 : 22,
                                Dungeon.player.Inventory.indexOf(item)
                        ));
                inventoryElements.add(
                        new Element(
                                String.valueOf(item.id),
                                (inventoryIsOpen ? item.model.toString() + " " + item.getName() + " [" + ((Equipment) item).getStats().intValue() + "]" : item.model.toString()),
                                pos,
                                openContextMenu
                        )
                );
                TerminalView.drawBlockInTerminal(invViewGraphics, inventoryElements.get(Dungeon.player.Inventory.indexOf(item)).ElementTitle, pos);
                //TerminalView.putCharInTerminal(invViewGraphics, item.model.get(), pos);
            }
            if (inventoryIsOpen && inventoryCursor != null) {
                placePointerNearTitle(inventoryElements.get(inventoryCursor.indexOfElement).ElementTitle, inventoryCursor);
            }
        }
    }

    private void drawContextMenu() {
        if (contextMenuIsOpen) {
            if (!inventoryElements.isEmpty()) {
                int offsetX;
                Element element = inventoryElements.get(inventoryCursor.indexOfElement);
                if (Dungeon.player.Inventory.get(inventoryCursor.indexOfElement) instanceof Usable) {
                    menuOptions[0] = "Use";
                    menuActions[0] = () -> {
                        Item item = Dungeon.player.Inventory.get(inventoryCursor.indexOfElement);
                        ((Usable) item).use();
                        logView.playerAction("have used a " + item.model.getModelColorName() + "!");
                        Dungeon.player.Inventory.remove(item);
                    };
                } else {
                    menuOptions[0] = "Equip";
                    menuActions[0] = equip;
                }
                String elementTitle = truncateTitle(element.ElementTitle);
                for (int i = 0; i < menuOptions.length; i++) {

                    offsetX = elementTitle.length() + ((menuOptions[i].length() * i * 2));
                    contextMenuElements.add(new Element(
                            menuOptions[i], menuOptions[i],
                            element.ElementPosition.getRelative(offsetX, 0),
                            menuActions[i]));
                    TerminalView.drawBlockInTerminal(invViewGraphics, contextMenuElements.get(i).ElementTitle, contextMenuElements.get(i).ElementPosition);
                }
                clearPointer(previousElementTitle, inventoryCursor.previousCursorPosition);
                TerminalView.setPointerIntoPosition(invViewGraphics, inventoryCursor.cursorPointer, contextMenuElements.get(contextMenuElements.size() - 1).ElementPosition.getRelative(4, 0));
                if (contextCursor != null)
                    placePointerUnderTitle(contextCursor);
            }
        }
    }

    private void drawItemCount(){
        if(inventoryIsOpen) {
            int countItems = Dungeon.player.Inventory.size();
            String itemsInInventory = "Items in inventory: ";
            TerminalView.drawBlockInTerminal(invViewGraphics, itemsInInventory + countItems, infoGrid.get_pointYX().getRelative(-10, 0));
        }
    }

    private final Runnable openContextMenu = this::openContextInput;

    private void placePointerNearTitle(String elementTitle, CursorUI currentCursor) {
        clearPointer(previousElementTitle, currentCursor.previousCursorPosition);

        elementTitle = truncateTitle(elementTitle);

        previousElementTitle = elementTitle;

        TerminalView.setPointerIntoPosition(invViewGraphics, currentCursor.cursorPointer, currentCursor.cursorPosition.getRelative(elementTitle.length(), 0));

    }

    private void placePointerUnderTitle(CursorUI currentCursor) {
        clearPointer("", currentCursor.previousCursorPosition);

        TerminalView.setPointerIntoPosition(invViewGraphics, currentCursor.cursorPointer, currentCursor.cursorPosition.getRelative(1, 1));
    }

    private void clearPointer(String previousElementTitle, Position previousCursorPosition) {
        if (!previousElementTitle.equals(""))
            TerminalView.setPointerIntoPosition(invViewGraphics, ' ', previousCursorPosition.getRelative(previousElementTitle.length(), 0));
        else if (contextMenuIsOpen) {
            TerminalView.setPointerIntoPosition(invViewGraphics, ' ', previousCursorPosition.getRelative(1, 1));
        }
    }

    private String truncateTitle(String elementTitle) {
        return elementTitle.replaceAll("\\[\\d{2};\\d{1};\\d{3}m", "")
                .replaceAll("\\[0m", "")
                .trim();
    }

    private void placeModels(Model m, String place, double d, int y) {
        String msg = "".concat("[").concat("%").concat("]").concat("_dmg_");
        TerminalView.drawBlockInTerminal(invViewGraphics, msg, inventoryPosition.x, inventoryPosition.y + y);
        TerminalView.drawBlockInTerminal(invViewGraphics, "|" + d + "|".concat(place), inventoryPosition.getRelative(msg.indexOf("_dmg_"), 0).x, inventoryPosition.y + y);
        TerminalView.putCharInTerminal(invViewGraphics, m.get(), inventoryPosition.getRelative(msg.indexOf("%"), 0).x, inventoryPosition.y + y);
    }

    public void openToInput() {
        if (!Dungeon.player.Inventory.isEmpty()) {
            inventoryIsOpen = true;

            *//*Draw.call(this);*//*

            drawItems();

            drawItemCount();

            inventoryCursor = new CursorUI(inventoryElements);

            inventoryCursor.cursorPointer = '<';

            inventoryCursor.setFirstElementCursorPosition();

            placePointerNearTitle(inventoryElements.get(inventoryCursor.indexOfElement).ElementTitle, inventoryCursor);

            Draw.flush();

            selectingElementsLoop();

            inventoryIsOpen = false;
            Draw.call(infoGrid);
            Draw.reset(this);
        }
    }

    public void openContextInput() {
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
        clearPointer("", contextCursor.previousCursorPosition);
        Draw.reset(this);
    }

    private void selectingElementsLoop() {
        while (!keyIsEscape(invKey = waitForInput()) && !inventoryElements.isEmpty()) {
            if (!contextMenuIsOpen) {
                inventoryCursor.SelectElementV(invKey.getKeyType());
            } else {
                contextCursor.SelectElementH(invKey.getKeyType());
            }
            Draw.call(this);
        }
    }



    @Override
    public void Reset() {
        this.invViewGraphics.fillRectangle(new TerminalPosition(inventoryPosition.x, inventoryPosition.y-1), new TerminalSize(70,6), ' ');
        Draw.call(this);
        Draw.call(infoGrid);
    }*/
}
