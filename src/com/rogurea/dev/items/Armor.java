/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.resources.Model;

public class Armor extends Equipment{

    private double defence = 1.0d;

    public Armor(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial);
        this.tag = this.tag.concat(".armor.").concat(name);
        this.defence = GetRandom.RNGenerator.nextInt(3) + (GameVariables.ARMOR_BASE_DEF * itemMaterial.getStrenght());
    }

    @Override
    public Number getStats() {
        return this.defence;
    }
}
