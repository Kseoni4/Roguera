/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.creatures;

import com.rogurea.dev.gamelogic.ItemGenerator;

public class Mob extends Creature{
    @Override
    public int getDamageByEquipment() {
        return super.getDamageByEquipment();
    }

    @Override
    public void getHit(int incomingDamage) {
        this.HP -= Math.abs((this.baseDEF + findEquipmentInInventoryByTag("item.equipment.armor.").getStats().intValue()) - incomingDamage);
    }

    public Mob(int HP, String name) {
        super(HP, name);
        this.tag += ".mob";
        this.baseATK = 1;
        this.baseDEF = 1;
        this.model.changeModel(name.charAt(0));
        initialPutInventory();
    }

    private void initialPutInventory(){
        this.creatureInventory.add(ItemGenerator.getRandomWeaponEquipment());
        this.creatureInventory.add(ItemGenerator.getRandomArmorEquipment());
    }
}
