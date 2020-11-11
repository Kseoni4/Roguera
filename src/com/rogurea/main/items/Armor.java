package com.rogurea.main.items;

import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

public class Armor extends Equipment{

    public int GetStats() {
        return super.GetStats();
    }

    public String getMaterialColor() {
        return GameResources.ArmorMaterialColor.get(this.Material);
    }

    public Armor(String name, int SellPrice, char model, int level) {
        super(name, SellPrice, model, level);
        super.name = GetRandom.ArmorName(this.Material);
    }
}
