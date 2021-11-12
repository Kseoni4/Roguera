/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.items;

import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GameVariables;
import com.rogurea.resources.Model;

public class Armor extends Equipment{

    private int defence;

    public Armor(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial, "def");
        this.tag = this.tag.concat(".armor.").concat(name);
        this.defence = (GameVariables.ARMOR_BASE_DEF * (int) itemMaterial.getStrenght() * Dungeon.getCurrentFloor().get().getFloorNumber());
    }

    public void setDefence(int amount){
        this.defence = amount;
    }

    @Override
    public Integer getStats() {
        return this.defence;
    }
}
