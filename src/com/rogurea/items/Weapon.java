/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.items;

import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GameVariables;
import com.rogurea.resources.Model;

public class Weapon extends Equipment{

    private int _damage;

    public Weapon(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial, "atk");
        this.tag = this.tag.concat(".weapon").concat(".").concat(name);
        this._damage = ((GameVariables.WEAPON_BASE_DMG * (int) itemMaterial.getStrenght()) * Dungeon.getCurrentFloor().get().getFloorNumber());
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
    public String toString(){
        return  "Weapon ".concat(getName()).concat("\n")
                .concat("\t")
                .concat("ID: ")
                .concat(String.valueOf(this.id))
                .concat("\n\t")
                .concat("Model: ")
                .concat(model.toString())
                .concat("\n\t")
                .concat("Tag: ")
                .concat(this.tag)
                .concat("\n\t")
                .concat("Material: ")
                .concat(getItemMaterial().name())
                .concat("\n\t")
                .concat("Damage: "+this._damage)
                .concat("\n");
    }

    @Override
    public Integer getStats() {
        return this._damage;
    }
}
