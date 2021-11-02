/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.player;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Floor;
import com.rogurea.gamemap.Position;
import com.rogurea.gamemap.Room;
import com.rogurea.items.Equipment;
import com.rogurea.items.Item;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameVariables;
import com.rogurea.net.JDBСQueries;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PlayerData implements Serializable {
    private String playerName;
    private int playerID = 1;
    private short HP;
    private byte Level = 1;
    private int Money = 0;
    private byte attempt = 0;
    private int score = 0;
    private int scoreMultiplier = 1;
    private int _atk = 0;
    private int _atkPotionBonus = 0;
    private int _baseAtk = GameVariables.BASE_PLAYER_ATK;
    private int _def = 0;
    private int _defPotionBonus = 0;
    private int _baseDef = GameVariables.BASE_PLAYER_DEF;
    private byte[] scoreHashMD5;
    private static MessageDigest md;
    private int kills = 0;
    private Position playerPositionData;
    private Room currentRoom;
    private Floor currentFloor;
    private String saveFileVersion;
    private ArrayList<Item> playerInventory;
    private HashMap<String, Equipment> playerEquipment;

    public HashMap<String, Equipment> getPlayerEquipment() {
        return playerEquipment;
    }

    public void setPlayerEquipment(HashMap<String, Equipment> playerEquipment) {
        this.playerEquipment = playerEquipment;
    }

    public ArrayList<Item> getPlayerInventory() {
        return playerInventory;
    }

    public void setPlayerInventory(ArrayList<Item> playerInventory) {
        this.playerInventory = playerInventory;
    }

    public Position getPlayerPositionData() {
        return playerPositionData;
    }

    public void setPlayerPositionData(Position playerPositionData) {
        this.playerPositionData = playerPositionData;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Floor currentFloor) {
        this.currentFloor = currentFloor;
    }

    public String getSaveFileVersion() {
        return saveFileVersion;
    }

    public void setSaveFileVersion(String saveFileVersion) {
        this.saveFileVersion = saveFileVersion;
    }

    public void recountStats(String stat, int amount){
        try{
            Field baseStat = this.getClass().getDeclaredField("_base"+Character.toUpperCase(stat.charAt(0))+stat.substring(1));
            int _statPotion  = this.getClass().getDeclaredField("_"+stat+"PotionBonus").getInt(this);

            Debug.toLog("[REFLECTION] base stat "+baseStat.getInt(this)+" _statPotionBonus "+_statPotion+" amount "+amount);

            this.getClass().getDeclaredField("_"+stat).setInt(this,_statPotion+amount);

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
        //Debug.toLog("Current hash\n"+ Arrays.toString(scoreHashMD5));
    }

    public void setScore(int score) {
        if(checkScoreHash()){
            Debug.toLog("[PLAYER_DATA]"+Colors.GREEN_BRIGHT+"Score is not compromised!");
            this.score += score * scoreMultiplier;
            updateHash();
            JDBСQueries.updateUserScore();
        } else {
            Debug.toLog("[PLAYER_DATA]"+Colors.RED_BRIGHT+"Score is compromised!");
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
        JDBСQueries.updUserHash(String.valueOf(scoreHashMD5.hashCode()));
    }

    public boolean checkSavedScoreHash(int hash){
        return scoreHashMD5.hashCode() == hash;
    }

    public byte[] getScoreHash(){
        return scoreHashMD5;
    }

    public void setScoreHash(byte[] hash){
        this.scoreHashMD5 = hash;
    }

    private boolean checkScoreHash(){
        byte[] currentScoreHashInMemory = md.digest(String.valueOf(this.score).getBytes());
        return Arrays.equals(scoreHashMD5, currentScoreHashInMemory);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = Colors.GREEN_BRIGHT+playerName+Colors.R;
    }

    public String[] formPlayerData(){

        return new String[]{
                "Score: ".concat(Colors.VIOLET).concat(String.valueOf(score)).concat(" ").concat((scoreMultiplier > 1 ? "x"+scoreMultiplier : "")),
                "Floor: ".concat(Colors.PINK).concat(String.valueOf(Dungeon.getCurrentFloor().getFloorNumber()))+Colors.R+" Room: ".concat(Colors.MAGENTA).concat(String.valueOf(Dungeon.getCurrentRoom().roomNumber)),
                "Name: ".concat(Colors.GREEN_BRIGHT).concat(playerName),
                "Health: " + Colors.RED_BRIGHT.concat(String.valueOf(HP)),
                "Level: "+Colors.CYAN.concat(String.valueOf(Level)),
                "Gold: "+Colors.GOLDEN.concat(String.valueOf(Money)),
                "ATK: "+Colors.ORANGE.concat(""+(_atk+_baseAtk)),
                "DEF: "+Colors.BLUE_BRIGHT.concat(""+(_def+_baseDef))
        };
    }

    private byte[] RandomSeed;

    public int getHP(){
        return HP;
    }

    public int getLevel(){
        return Level;
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
        setScore(money/3);
        Money += money;
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

    public int getScore() {
        return score;
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

    public int get_atkPotionBonus() {
        return _atkPotionBonus;
    }

    public void set_atkPotionBonus(int _atkPotionBonus) {
        this._atkPotionBonus = _atkPotionBonus;
    }

    public int get_defPotionBonus() {
        return _defPotionBonus;
    }

    public void set_defPotionBonus(int _defPotionBonus) {
        this._defPotionBonus = _defPotionBonus;
    }


    public int getKills() {
        return kills;
    }

    public void setKill() {
        this.kills += 1;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}
