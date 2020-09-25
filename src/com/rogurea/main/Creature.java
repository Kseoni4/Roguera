package com.rogurea.main;

import java.util.ArrayList;

public abstract class Creature {

    public String Name;

    public int HP;

    public int Level = 1;

    public ArrayList<Item> Loot = new ArrayList<Item>();

    public void changeHP(int dmg){
        this.HP -= dmg;
    }

    public Creature(String name){
        this.Name = name;
    }

    public abstract int getDamage();

    public abstract int getArmor();
}
