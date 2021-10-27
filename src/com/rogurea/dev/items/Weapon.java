/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.resources.Model;

import java.util.concurrent.ThreadLocalRandom;

public class Weapon extends Equipment{

    private int _damage;

    public Weapon(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial, "atk");
        this.tag = this.tag.concat(".weapon").concat(".").concat(name);
        this._damage = GetRandom.RNGenerator.nextInt(10) + (GameVariables.WEAPON_BASE_DMG * (int) itemMaterial.getStrenght());
        System.out.println("W:"+name+" C:"+itemMaterial.getColor()+"color"+ Colors.R);
        System.out.println("Created weapon with color " + model.toString());
    }

    public Weapon(String name, Model model, Materials itemMaterial, int damage){
        super(name, model, itemMaterial, "atk");
        this._damage = damage * (int) itemMaterial.getStrenght();
        this.tag = this.tag.concat(".weapon").concat(".").concat(name);
    }


    public void setDamage(int dmg){
        this._damage = dmg;
    }

    @Override
    public Integer getStats() {
        return this._damage;
    }
}
