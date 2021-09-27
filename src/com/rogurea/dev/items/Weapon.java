/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;
public class Weapon extends Equipment{

    private double _damage = 1.0d;

    public Weapon(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial);
        System.out.println("W:"+name+" C:"+itemMaterial.getColor()+"color"+ Colors.R);
        System.out.println("Created weapon with color " + model.toString());
    }

    public Weapon(String name, Model model, Materials itemMaterial, double damage){
        super(name, model, itemMaterial);
        this._damage = damage;
    }

    public void setDamage(double dmg){
        this._damage = dmg;
    }

    @Override
    public Number getStats() {
        return this._damage;
    }
}
