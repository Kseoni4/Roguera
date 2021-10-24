/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;

import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameVariables;

public class PlayerData {
    private String PlayerName;
    private short MP = 30;
    private short HP;
    private byte Level = 1;
    private short Money = 0;
    private byte attempt = 0;
    private int _atk = 1;
    private int _baseAtk = GameVariables.BASE_PLAYER_ATK;
    private int _def = 1;


    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }

    public String[] formPlayerData(){

        return new String[]{
                "Room: ".concat(Colors.MAGENTA).concat(String.valueOf(Dungeon.getCurrentRoom().RoomNumber)),
                "Name: ".concat(Colors.GREEN_BRIGHT).concat(PlayerName),
                "Health: " + Colors.RED_BRIGHT.concat(String.valueOf(HP)),
                "Mana: "+Colors.BLUE_BRIGHT.concat(String.valueOf(MP)),
                "Level: "+Colors.CYAN.concat(String.valueOf(Level)),
                "Gold: "+Colors.GOLDEN.concat(String.valueOf(Money)),
                "ATK: "+Colors.ORANGE.concat(String.valueOf(_atk))
        };
    }

    private byte[] RandomSeed;

    public int getHP(){
        return HP;
    }

    public int getMP(){
        return MP;
    }

    public int getLevel(){
        return Level;
    }

    public void setMP(int MP) {
        this.MP = (short) MP;
    }

    public void setHP(int HP) {
        this.HP = (short) HP;
    }

    public void setLevel(int level) {
        Level = (byte) level;
    }

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = (short) money;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = (byte) attempt;
    }

    public byte[] getRandomSeed() {
        return RandomSeed;
    }

    public void setRandomSeed(byte[] randomSeed) {
        RandomSeed = randomSeed;
    }

    public int get_atk() {
        return _atk;
    }

    public int get_baseAtk(){
        return _baseAtk;
    }

    public void set_atk(int _atk) {
        this._atk = _atk;
    }

    public int get_def() {
        return _def;
    }

    public void set_def(int _def) {
        this._def = _def;
    }
}
