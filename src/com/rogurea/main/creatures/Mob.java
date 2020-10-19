package com.rogurea.main.creatures;

import com.rogurea.main.player.Player;

public class Mob extends Creature {

    private int Armor;
    private int Damage;

    public final int ScanZone = 2;

    public char MobSymbol;

    public int MobPosX;
    public int MobPosY;

    public Mob(String name, char mobSymbol, int HP) {
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
        return c == Player.PlayerModel;
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

