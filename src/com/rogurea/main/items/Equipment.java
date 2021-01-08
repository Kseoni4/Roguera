package com.rogurea.main.items;

import com.rogurea.main.resources.GetRandom;

public class Equipment extends Item implements Wearable, Usable{

    private final int EquipStats;

    public String Material;

    public int level;

    public Equipment(String name, int SellPrice, char model, int level) {
        super(name, SellPrice, model);
        this.level = level;
        this.Material = GetRandom.Material(this);
        this.EquipStats = ItemGenerate.MoveToForge(this);
    }

    public int GetStats(){
        return this.EquipStats;
    }

    @Override
    public void use() {

    }
}
