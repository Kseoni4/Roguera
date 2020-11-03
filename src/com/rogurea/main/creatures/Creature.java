package com.rogurea.main.creatures;

import com.rogurea.main.items.Item;

import java.util.ArrayList;

public abstract class Creature {

    public String Name = "";

    public final int id;

    public static int CreatureCount = 0;

    public abstract char getCreatureSymbol();

    public abstract void setMobPosition(int x, int y);

    public abstract int getMobPosX();

    public abstract int getMobPosY();

    public enum CreatureType {
        MOB,
        NPC
    }

    private CreatureType creatureType;

    private int HP;

    public int Level = 1;

    public ArrayList<Item> Loot = new ArrayList<>();

    public void setCreatureType(CreatureType creatureType){
        this.creatureType = creatureType;
    }

    public int getHP(){
        return this.HP;
    }

    public void changeHP(int dmg){
        this.HP -= dmg;
    }

    public void setHP(int HP){
        this.HP = HP;
    }

    public Creature(String name){
        this.Name += name;
        id = CreatureCount++;
    }

    public abstract int getDamage();

    public abstract int getArmor();
}
