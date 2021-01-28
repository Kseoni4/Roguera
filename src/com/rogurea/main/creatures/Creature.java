package com.rogurea.main.creatures;

import com.rogurea.main.items.Item;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Creature implements Serializable {

    public String Name = "";

    public final short id;

    public static short CreatureCount = 0;

    public abstract char getCreatureSymbol();

    public abstract void setMobPosition(int x, int y);

    public abstract byte getMobPosX();

    public abstract byte getMobPosY();

    public enum CreatureType {
        MOB,
        NPC
    }

    private short HP;

    public byte Level = 1;

    public ArrayList<Item> Loot = new ArrayList<>();

    public void setCreatureType(CreatureType creatureType){
    }

    public short getHP(){
        return this.HP;
    }

    public void changeHP(short dmg){
        this.HP -= dmg;
    }

    public void setHP(short HP){
        this.HP = HP;
    }

    public Creature(String name){
        this.Name += name;
        id = CreatureCount++;
    }

    public abstract short getATKm();

    public abstract short getDEFm();
}
