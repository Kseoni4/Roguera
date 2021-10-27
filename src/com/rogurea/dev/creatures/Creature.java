/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.creatures;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.ViewObjects;

import java.util.ArrayList;
import java.util.Optional;

import static com.rogurea.dev.gamemap.Dungeon.player;

public class Creature extends GameObject {
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
        Dungeon.getCurrentRoom().getCell(this.cellPosition).removeFromCell();
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(this);
        this.cellPosition = position;
        Draw.call(ViewObjects.mapView);
    }

    public void putInInventory(Item item){
        creatureInventory.add(item);
    }

    public Creature(){
        this.tag = "creature";
    };
}
