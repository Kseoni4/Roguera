package com.rogurea.main.items;

import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

public class Armor extends Item{

    private final int Defense;

    public int getDefense(){
        return this.Defense;
    }

    @Override
    public String getMaterialColor() {
        return GameResources.ArmorMaterialColor.get(this.Material);
    }

    public Armor(String name, int SellPrice, char model, int level) {
        super(name, SellPrice, model);
        this.Material = GetRandom.ArmorMaterial();
        this.Defense = ItemGenerate.ArmorForge(level, this.Material);
        super.name = GetRandom.ArmorName(this.Material);
    }
}
