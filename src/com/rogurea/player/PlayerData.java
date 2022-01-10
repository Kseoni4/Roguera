/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.player;

import com.rogurea.base.Debug;
import com.rogurea.gamelogic.RogueraGameSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Floor;
import com.rogurea.gamemap.Position;
import com.rogurea.gamemap.Room;
import com.rogurea.items.Equipment;
import com.rogurea.items.Item;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameVariables;
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
    private String token = "";
    private int playerID = 1;
    private int HP;
    private int maxHP = 100;
    private int level = 1;
    private int requiredEXP = 0;
    private int exp = 0;
    private int Money = 0;
    private int attempt = 0;
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
    private Position playerSavePositionData;
    private Room currentRoomSave;
    private Floor currentFloorSave;
    private String saveFileVersion;
    private ArrayList<Item> playerSaveInventory;
    private HashMap<String, Equipment> playerSaveEquipment;
    private ArrayList<Equipment> playerSaveQuickEquipment;

    public ArrayList<Equipment> getPlayerQuickEquipment() {
        return playerSaveQuickEquipment;
    }

    public void setPlayerQuickEquipment(ArrayList<Equipment> playerQuickEquipment) {
        this.playerSaveQuickEquipment = playerQuickEquipment;
    }

    public HashMap<String, Equipment> getPlayerEquipment() {
        return playerSaveEquipment;
    }

    public void setPlayerEquipment(HashMap<String, Equipment> playerEquipment) {
        this.playerSaveEquipment = playerEquipment;
    }

    public ArrayList<Item> getPlayerInventory() {
        return playerSaveInventory;
    }

    public void setPlayerInventory(ArrayList<Item> playerInventory) {
        this.playerSaveInventory = playerInventory;
    }

    public Position getPlayerPositionData() {
        return playerSavePositionData;
    }

    public void setPlayerPositionData(Position playerPositionData) {
        this.playerSavePositionData = playerPositionData;
    }

    public Room getCurrentRoom() {
        return currentRoomSave;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoomSave = currentRoom;
    }

    public Floor getCurrentFloor() {
        return currentFloorSave;
    }

    public void setCurrentFloor(Floor currentFloor) {
        this.currentFloorSave = currentFloor;
    }

    public String getSaveFileVersion() {
        return saveFileVersion;
    }

    public void setSaveFileVersion(String saveFileVersion) {
        this.saveFileVersion = saveFileVersion;
    }

    /**
     * Обновление характеристик по имени поля класса
     * @param stat имя характеристики
     * @param amount значение
     */
    public void recountStats(String stat, int amount){
        try{
            Field baseStat = this.getClass().getDeclaredField("_base"+Character.toUpperCase(stat.charAt(0))+stat.substring(1));

            int _statPotion  = this.getClass().getDeclaredField("_"+stat+"PotionBonus").getInt(this);

            Debug.toLog("[REFLECTION] base stat "+baseStat.getInt(this)+" _statPotionBonus "+_statPotion+" amount "+amount);

            this.getClass().getDeclaredField("_"+stat).setInt(this,amount);

        } catch (NoSuchFieldException | IllegalAccessException e){
            Debug.toLog("[ERROR][REFLECTION]No such field: "+"_"+stat);
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

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public void setScore(int score) {
        if(checkScoreHash()){
            //Debug.toLog("[PLAYER_DATA]"+Colors.GREEN_BRIGHT+"Score is not compromised!");
            this.score += score * scoreMultiplier;
            updateHash();
            //JDBСQueries.updateUserScore();
        } else {
            //Debug.toLog("[PLAYER_DATA]"+Colors.RED_BRIGHT+"Score is compromised!");
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
        //JDBСQueries.updUserHash(String.valueOf(scoreHashMD5.hashCode()));
    }

    public boolean checkSavedScoreHash(int hash){
        return Arrays.hashCode(scoreHashMD5) == hash;
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
                "Floor: ".concat(Colors.PINK).concat(String.valueOf(Dungeon.getCurrentFloor().get().getFloorNumber()))+Colors.R+" Room: ".concat(Colors.MAGENTA).concat(String.valueOf(Dungeon.getCurrentRoom().roomNumber)),
                "Name: ".concat(Colors.GREEN_BRIGHT).concat(playerName),
                "Health: " + Colors.RED_BRIGHT.concat(String.valueOf(HP)),
                "Level: "+Colors.CYAN.concat(String.valueOf(level)),
                "EXP: "+Colors.CYAN_BRIGHT+exp+"/"+requiredEXP,
                "Gold: "+Colors.GOLDEN.concat(String.valueOf(Money)),
                "ATK: "+Colors.ORANGE.concat(""+(_atkPotionBonus+_atk+_baseAtk)).concat(_atkPotionBonus > 0 ? " "+Colors.RED_PASTEL+"(+"+_atkPotionBonus+")" :""),
                "DEF: "+Colors.BLUE_BRIGHT.concat(""+(_defPotionBonus+_def+_baseDef)).concat(_defPotionBonus > 0 ? " "+Colors.BLUE_PASTEL+"(+"+_defPotionBonus+")":"")
        };
    }

    private byte[] RandomSeed;

    public int getHP(){
        return HP;
    }

    public int getLevel(){
        return level;
    }

    public void setHP(int HP) {
        this.HP = (short) HP;
    }

    public void setLevel(int level) {
        this.level = (byte) level;
    }

    public int getMoney() {
        return Money;
    }

    public boolean setMoney(int money) {
        if(money > 0)
            setScore(money/3);
        Money += money;
        return true;
    }

    public int getAttempt() {
        return attempt;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp += exp;
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

    public void updBaseATK(){
        this._baseAtk = RogueraGameSystem.getPlayerBaseATK();
    }
    public void updBaseDEF(){
        this._baseDef = RogueraGameSystem.getPlayerBaseDEF();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void updRequiredEXP(){
        this.requiredEXP = RogueraGameSystem.getRequirementEXP();
        this.exp = 0;
    }

    public int getRequiredEXP() {
        return requiredEXP;
    }

    public void getInfo(){
        String info = "" +
                "Player: "+this.playerName+"\n" +
                "Base characteristics: \n\t" +
                    "ATK: "+this._baseAtk+"\n\t" +
                    "DEF: "+this._baseDef+"\n" +
                "Equipment: \n\t" +
                    "_ATK: "+this._atk+"\n\t" +
                    "_DEF: "+this._def+"\n" +
                "Potion bonus: \n\t" +
                    "_ATK_PBONUS: "+this._atkPotionBonus+"\n\t" +
                    "_DEF_PBONUS: "+this._defPotionBonus+"\n" +
                "Control sum: \n\t" +
                    "ATK + _ATK + _ATK_PBONUS: "+(this._baseAtk+this._atk+this._atkPotionBonus)+"\n\t" +
                    "DEF + _DEF + _DEF_PBONUS: "+(this._baseDef+this._def+this._defPotionBonus);
        Debug.toLog(info);
    }
    @Override
    public String toString(){
        return "Player: "+ this.playerName+"\n" +
        "Base characteristics: \n\t" +
        "ATK: "+ this._baseAtk+"\n\t" +
        "DEF: "+ this._baseDef+"\n" +
        "Equipment: \n\t" +
        "_ATK: "+ this._atk+"\n\t" +
        "_DEF: "+ this._def+"\n" +
        "Potion bonus: \n\t" +
        "_ATK_PBONUS: "+ this._atkPotionBonus+"\n\t" +
        "_DEF_PBONUS: "+ this._defPotionBonus+"\n\t" +
        "Score: "+ this.score+"\n\t" +
        "Score multiplier: "+ this.scoreMultiplier + "\n\t" +
        "Kills: "+ this.kills+"\n\t" +
        "Money: "+ this.Money+"\n";
    }
}
