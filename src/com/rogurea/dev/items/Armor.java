/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.resources.Model;

public class Armor extends Equipment{

    private int defence = 1;

    public Armor(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial, "def");
        this.tag = this.tag.concat(".armor.").concat(name);
        this.defence = GetRandom.RNGenerator.nextInt(3) + (GameVariables.ARMOR_BASE_DEF * (int) itemMaterial.getStrenght());
    }

    @Override
    public Integer getStats() {
        return this.defence;
    }
}
