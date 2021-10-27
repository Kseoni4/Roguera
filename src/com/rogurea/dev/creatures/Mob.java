/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.creatures;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamelogic.ItemGenerator;
import com.rogurea.dev.resources.Colors;



public class Mob extends Creature{

    @Override
    public int getDamageByEquipment() {
        return super.getDamageByEquipment();
    }

    @Override
    public void getHit(int incomingDamage) {
        int fullDef = this.baseDEF + findEquipmentInInventoryByTag("item.equipment.armor.").getStats();
        int deltaDmg = incomingDamage - fullDef;
        this.HP -= Math.max(0,deltaDmg);
    }

    public Mob(int HP, String name) {
        super(HP, name);
        this.tag += ".mob";
        this.baseATK = 1;
        this.baseDEF = 1;
        this.model.changeModel(name.charAt(0));
        this.model.changeColor(Colors.RED_BRIGHT);
        initialPutInventory();
    }

    @Override
    public String getName(){
        return Colors.RED_BRIGHT+this.name+Colors.R;
    }

    private void initialPutInventory(){
        this.creatureInventory.add(ItemGenerator.getRandomWeaponEquipment());
        this.creatureInventory.add(ItemGenerator.getRandomArmorEquipment());
    }
}
