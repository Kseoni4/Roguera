package com.rogurea.main.items;

public class Armor extends Item{

    private int Defense;

    public int getDefense(){
        return this.Defense;
    }

    public Armor(String name, int SellPrice, char model) {
        super(name, SellPrice, model);
        this.Defense = ItemGenerate.ArmorForge();
    }
}
