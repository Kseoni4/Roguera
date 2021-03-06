package com.rogurea.main.items;

import com.rogurea.main.resources.GetRandom;

public class Equipment extends Item implements Wearable, Usable{

    private final int EquipStats;

    public String Material;

    public Equipment(String name, int SellPrice, char model, int RoomNumber) {
        super(name, SellPrice, model);
        this.Material = GetRandom.Material(this);
        this.EquipStats = ItemGenerate.MoveToForge(this, RoomNumber);
    }

    public int GetStats(){
        return this.EquipStats;
    }

    @Override
    public void use() {

    }
}
