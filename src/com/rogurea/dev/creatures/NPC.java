/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.creatures;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.items.Item;

import java.util.ArrayList;
import java.util.function.Consumer;

public class NPC extends Creature{

    private final Consumer<ArrayList<Item>> npcAction;

    public void executeLogic(){
        this.npcAction.accept(this.creatureInventory);
    }

    @Override
    public int getDamageByEquipment() {
        return super.getDamageByEquipment();
    }

    @Override
    public int getHP() {
        return super.getHP();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void getHit(int incomingDamage) {
        super.getHit(incomingDamage);
    }

    @Override
    protected Equipment findEquipmentInInventoryByTag(String tag) {
        return super.findEquipmentInInventoryByTag(tag);
    }

    @Override
    public void moveTo(Position position) {
        super.moveTo(position);
    }

    @Override
    public void putInInventory(Item item) {
        super.putInInventory(item);
    }

    public NPC(String npcName, Consumer<ArrayList<Item>> npcAction) {
        super(1, npcName);
        Debug.toLog("[NPC] "+npcName+" creating");
        this.npcAction = npcAction;
        this.tag += ".npc";
        this.cellPosition = new Position(2,2);
    }
}
