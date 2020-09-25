package com.rogurea.main;

import java.util.Random;

public class Mob extends Creature {

    private int Damage;
    private int Armor;

    Random rand = new Random();

    public enum MobType {
        GROUND,
        FLYING
    };

    public MobType mobType;

    @Override //Переопределение родительского метода
    public void changeHP(int dmg) {
        this.HP -= Math.max((dmg - this.Armor), 1);
    }

    public int getDamage(){
        return this.Damage;
    }

    public int getArmor(){
        return this.Armor;
    }

    public Mob(String name, MobType mobType){
        super(name);
        HP = rand.nextInt(30)*this.Level+1;
        Armor = rand.nextInt(5)*this.Level+1;
        this.Damage = rand.nextInt(50)*this.Level+1;
        this.mobType = mobType;
        Loot = Dungeon.InsertLoot();
    }
}
