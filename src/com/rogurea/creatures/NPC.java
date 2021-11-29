/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.creatures;

import com.rogurea.base.Debug;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamelogic.NPCConsumeAction;
import com.rogurea.gamemap.Position;
import com.rogurea.items.Equipment;
import com.rogurea.items.Item;

import java.util.ArrayList;

public class NPC extends Creature {

    private final NPCConsumeAction<ArrayList<Item>> npcAction;

    public void executeLogic(){
        this.npcAction.take(creatureInventory);
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

    public NPC(String npcName, NPCConsumeAction<ArrayList<Item>> npcAction) {
        super(1, npcName);
        //Debug.toLog("[NPC] " + npcName + " creating");
        this.npcAction = npcAction;
        this.tag += ".npc";
        this.cellPosition = new Position(2, 2);
        this.creatureInventory.add(ItemGenerator.getRandomPotion());
        this.creatureInventory.add(ItemGenerator.getRandomPotion());
        this.creatureInventory.add(ItemGenerator.getRandomPotion());
    }
}
