/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.ViewObjects;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PlayerData {
    private String PlayerName;
    private short MP = 30;
    private short HP;
    private byte Level = 1;
    private short Money = 0;
    private byte attempt = 0;
    private int score = 0;
    private int scoreMultiplier = 1;
    private int _atk = 1;
    private int _baseAtk = GameVariables.BASE_PLAYER_ATK;
    private int _def = 1;
    private int _baseDef = GameVariables.BASE_PLAYER_DEF;
    private byte[] scoreHashMD5;
    private static MessageDigest md;

    public void recountStats(String stat, int amount){
        try{
            Field baseStat = this.getClass().getDeclaredField("_base"+Character.toUpperCase(stat.charAt(0))+stat.substring(1));

            this.getClass().getDeclaredField("_"+stat).setInt(this,baseStat.getInt(this)+amount);

        } catch (NoSuchFieldException | IllegalAccessException e){
            Debug.toLog("No such field: "+"_"+stat);
        }
    }

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PlayerData() throws NoSuchAlgorithmException {
        scoreHashMD5 = md.digest(String.valueOf(this.score).getBytes());
        Debug.toLog("Current hash\n"+ Arrays.toString(scoreHashMD5));
    }

    public void setScore(int score) {
        if(checkScoreHash()){
            Debug.toLog(Colors.GREEN_BRIGHT+"Score is not compromised!");
            Debug.toLog("Current hash\n"+ Arrays.toString(scoreHashMD5));
            this.score += score * scoreMultiplier;
            updateHash();
            Debug.toLog("New hash\n"+ Arrays.toString(scoreHashMD5));
        } else {
            Debug.toLog(Colors.RED_BRIGHT+"Score is compromised!");
            this.score = 0;
            updateHash();
        }
        Draw.call(ViewObjects.infoGrid.getFirstBlock());
    }

    public void addScoreMultiplier(int amount){
        this.scoreMultiplier += amount;
    }

    private void updateHash(){
        md.update(String.valueOf(this.score).getBytes());
        scoreHashMD5 = md.digest();
    }

    private boolean checkScoreHash(){
        byte[] currentScoreHashInMemory = md.digest(String.valueOf(this.score).getBytes());
        return Arrays.equals(scoreHashMD5, currentScoreHashInMemory);
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = Colors.GREEN_BRIGHT+playerName+Colors.R;
    }

    public String[] formPlayerData(){

        return new String[]{
                "Score: ".concat(Colors.VIOLET).concat(String.valueOf(score)).concat(" ").concat((scoreMultiplier > 1 ? "x"+scoreMultiplier : "")),
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
        Money += (short) money;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = (byte) attempt;
    }

    public int get_baseDef() {
        return _baseDef;
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
