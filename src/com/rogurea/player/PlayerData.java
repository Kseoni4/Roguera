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
            JDBСQueries.updateUserScore();
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
        Debug.toLog("[PLAYER_DATA] new hash code: "+scoreHashMD5.hashCode());
        Debug.toLog("[PLAYER_DATA] new hash code: "+scoreHashMD5.hashCode());
        JDBСQueries.updUserHash(String.valueOf(scoreHashMD5.hashCode()));
    }

    public boolean checkSavedScoreHash(int hash){
        Debug.toLog("[LOAD] score hash from save: "+scoreHashMD5.hashCode());
        Debug.toLog("[LOAD] score hash from database: "+hash);
        return scoreHashMD5.hashCode() == hash;
    }

    public byte[] getScoreHash(){
        return scoreHashMD5;
    }

    public void setScoreHash(byte[] hash){
        Debug.toLog("[PLAYER_DATA] Input hash = "+hash.hashCode());
        this.scoreHashMD5 = hash;
        Debug.toLog("[PLAYER_DATA] This hash = "+scoreHashMD5.hashCode());
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
                "ATK: "+Colors.ORANGE.concat(String.valueOf(_atk)),
                "DEF: "+Colors.BLUE_BRIGHT.concat(String.valueOf(_def))
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
