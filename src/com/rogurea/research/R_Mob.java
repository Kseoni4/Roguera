package com.rogurea.research;

import com.rogurea.main.Player;

public class R_Mob extends R_Creature {

    private int Armor;
    private int Damage;

    public final int ScanZone = 2;

    public char MobSymbol;

    public int MobPosX;
    public int MobPosY;

    public R_Mob(String name, char mobSymbol, int HP) {
        super(name);
        this.MobSymbol = mobSymbol;
        setHP(HP);
        setCreatureType(CreatureType.MOB);
    }

    public void setMobPosition(int x, int y){
        this.MobPosX = x;
        this.MobPosY = y;
    }

    public int getMobPosX() {
        return MobPosX;
    }

    public int getMobPosY() {
        return MobPosY;
    }

    public boolean ScanForPlayer(char c){
        return c == R_Player.Player;
    }

    @Override
    public char getCreatureSymbol() {
        return this.MobSymbol;
    }

    @Override
    public int getDamage() {
        return this.Damage;
    }

    @Override
    public int getArmor() {
        return this.Armor;
    }
}

