package com.rogurea.dev.items;

import com.rogurea.dev.resources.Model;

public class Weapon extends Equipment{

    private float Damage = 1;

    public Weapon(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial);
    }

    @Override
    public Number getStats() {
        return this.Damage;
    }
}
