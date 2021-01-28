package com.rogurea.main.player;

import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.items.*;
import com.rogurea.main.map.Position;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.view.Draw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Predicate;

import static com.rogurea.main.view.ViewObjects.logBlock;
import static com.rogurea.main.view.ViewObjects.playerInfoBlock;

public class Player implements Serializable {
    public final static char PlayerModel = GameResources.PlayerModel;

    public static class Pos{
        public static byte x = 1;
        public static byte y = 1;
    }

    public static String nickName = "Player" + Calendar.getInstance().getTimeInMillis();
    public static short HP = 100;
    public static short MP = 30;
    public static byte Level = 0;
    public static byte CurrentRoom = 1;
    public static short Money = 0;
    public static byte attempt = 0;
    public static byte[] RandomSeed;


    public static short XP = 0; //Experience Points
    public static short ATK = GameVariables.BasePlayerATK; //Attack
    public static short DEF = GameVariables.BasePlayerDEF; //Defence
    public static byte DEX = GameVariables.BasePlayerDEX; //Dexterity
    public static short ReqXPForNextLevel = (short) GameVariables.BaseReqXP;

    public static ArrayList<Item> Inventory = new ArrayList<>();

    public static HashMap<String, Equipment> Equip;

    public static PlayerStatistics playerStatistics = new PlayerStatistics();

    static{
        Equip = new HashMap<>();
        Equip.put("FirstWeapon", null);
        Equip.put("SecondWeapon", null);
        Equip.put("Armor", null);
        Equip.put("Amulet", null);
    }

    public static void PutInInventory(Item item){
        if(Inventory.size() < 10) {
            Inventory.add(item);
            logBlock.Action("get the " + "\u001b[38;5;202m" + item.name + "\u001b[0m" + '!');
            Player.AutoEquip();
        }
        else{
            logBlock.Event("Your inventory is full!");
        }
    }

    public static void GetGold(Gold gold){
        try {
            logBlock.Action("get the " + Colors.ORANGE + gold.Amount + " gold!");
            Money += Math.max(gold.Amount, 0);
            playerStatistics.GoldCollected += Math.max(gold.Amount,0);
        } catch (NullPointerException e){
            Debug.log("ERROR: Player did not get a gold, cause NP exception");
            Debug.log("ERROR: Position of problem " + GetPlayerPosition().toString());
            Debug.log(Arrays.toString(e.getStackTrace()));
        }
        Draw.call(playerInfoBlock);
    }

    public static int getArmor(){
        Armor armor = (Armor) Equip.get("Armor");
        if(armor != null)
            return armor.GetStats();
        return 0;
    }

    public static int getDamageByWeapon(){
        Weapon wp = (Weapon) Equip.get("FirstWeapon");
        if(wp != null)
            return wp.GetStats();
        return 0;
    }

    public static Item GetFromInventory(Predicate<Item> predicate){
        Item _item = Inventory.stream().filter(predicate).findAny().orElse(null);
        Inventory.remove(_item);
        return _item;
    }

    public static void AutoEquip() {
        if (Equip.get("FirstWeapon") == null)
            for (Item i : Inventory) {
                if (i instanceof Weapon) {
                    InventoryController.EquipItem((Equipment) GetFromInventory(item -> item.equals(i)), "FirstWeapon");
                    break;
                }
            }
        if (Equip.get("Armor") == null){
            for (Item i : Inventory){
                if(i instanceof Armor) {
                    InventoryController.EquipItem((Equipment)
                            GetFromInventory(item -> item.equals(i)), "Armor");
                    break;
                }
            }
        }
    }

    public static String getPlayerWeaponInHands(){
        if(Equip.get("FirstWeapon") != null)
            return Equip.get("FirstWeapon").name;
        else
            return "fists";
    }

    public static Position GetPlayerPosition(){
        return new Position(Pos.x, Pos.y);
    }
    public static void PlayerReset(){
        HP = 100;
        MP = 30;
        Equip = new HashMap<>();
        Inventory = new ArrayList<>();
        Money = 0;
        CurrentRoom = 1;
        Level = 1;
    }

    public static void LoadPlayerDataFromFile(PlayerContainer SavedPlayerData){
        nickName = SavedPlayerData.nickName;
        HP = SavedPlayerData.HP;
        MP = SavedPlayerData.MP;
        Level = SavedPlayerData.Level;
        CurrentRoom = SavedPlayerData.CurrentRoom;
        Money = SavedPlayerData.Money;
        attempt = SavedPlayerData.attempt;
        RandomSeed = SavedPlayerData.RandomSeed;
        XP = SavedPlayerData.XP;
        ATK = SavedPlayerData.ATK;
        DEF = SavedPlayerData.DEF;
        DEX = SavedPlayerData.DEX;
        ReqXPForNextLevel = SavedPlayerData.XPForNextLevel;
        Inventory = SavedPlayerData.Inventory;
        Equip = SavedPlayerData.Equip;
        playerStatistics = SavedPlayerData.playerStatistics;
        Pos.x = (byte) SavedPlayerData.PlayerPosition.x;
        Pos.y = (byte) SavedPlayerData.PlayerPosition.y;
    }
}
