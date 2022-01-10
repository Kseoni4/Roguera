/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.creatures;

import com.rogurea.base.GameObject;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.items.Equipment;
import com.rogurea.items.Item;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;

import java.util.ArrayList;

public class Creature extends GameObject implements Movable {
    protected int HP;

    protected String name;

    protected int baseATK = 0;

    protected int baseDEF = 0;

    public Creature(int HP, String name){
        this.HP = HP;
        this.name = name;
        this.tag = "creature";
        this.model = new Model(name, Colors.ORANGE,name.charAt(0));
    }

    public int getDamageByEquipment(){
        return baseATK + findEquipmentInInventoryByTag("item.equipment.weapon.").getStats();
    }

    public int getDefenceByEquipment(){
        return baseDEF +findEquipmentInInventoryByTag("item.equipment.armor.").getStats();
    }

    public int getHP(){
        return this.HP;
    }

    public String getName(){
        return this.name;
    }

    public void getHit(int incomingDamage){
        this.HP -= Math.abs(incomingDamage - this.baseDEF);
    }

    protected ArrayList<Item> creatureInventory = new ArrayList<>();

    protected Equipment findEquipmentInInventoryByTag(String tag){
        return java.util.Optional.of((Equipment) this.creatureInventory.stream().filter(eq -> eq.tag.startsWith(tag)).findFirst().get()).get();
    }

    public void moveTo(Position position){
        Dungeon.getCurrentRoom().getCell(this.cellPosition).removeFromCell(this);
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(this);
        this.cellPosition = position;
        Draw.call(ViewObjects.mapView);
    }

/*    public void reloadInventory(){
        this.creatureInventory.forEach(item -> item.model.reloadModel());
    }*/

    public void putInInventory(Item item){
        creatureInventory.add(item);
    }

    public Creature(){
        this.tag = "creature";
    }
}
