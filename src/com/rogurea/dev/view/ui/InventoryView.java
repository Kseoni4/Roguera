/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.view.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.IViewBlock;
import com.rogurea.dev.view.TerminalView;

import java.io.IOException;

public class InventoryView implements IViewBlock {

    private TextGraphics invViewGraphics;

    public Position inventoryPosition;

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
        if(!Dungeon.player.Inventory.isEmpty()){
            for (Item item : Dungeon.player.Inventory){
                Position pos = new Position().getRelative(
                        inventoryPosition.getRelative(
                            Dungeon.player.Inventory.indexOf(item) > 5 ? 23 : 22,
                            Dungeon.player.Inventory.indexOf(item)
                ));
                TerminalView.putCharInTerminal(invViewGraphics, item.model.get(), pos);
            }
        }
    }

    private void placeModels(Model m, String place, double d, int y){
        String msg = "".concat("[").concat("%").concat("]").concat("_dmg_");
        TerminalView.drawBlockInTerminal(invViewGraphics, msg, inventoryPosition.x, inventoryPosition.y+y);
        TerminalView.drawBlockInTerminal(invViewGraphics, "|"+d+"|".concat(place), inventoryPosition.getRelative(msg.indexOf("_dmg_"),0).x, inventoryPosition.y+y);
        TerminalView.putCharInTerminal(invViewGraphics,m.get(),inventoryPosition.getRelative(msg.indexOf("%"),0).x, inventoryPosition.y+y);
    }

    @Override
    public void Reset() {

    }
}
