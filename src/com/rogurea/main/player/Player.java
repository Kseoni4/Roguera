package com.rogurea.main.player;

import com.rogurea.main.items.*;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.Draw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import static com.rogurea.main.resources.ViewObject.logBlock;
import static com.rogurea.main.resources.ViewObject.playerInfoBlock;

public class Player {
    public final static char PlayerModel = GameResources.PlayerModel;

    public static class Pos{
        public static byte x = 1;
        public static byte y = 1;
    }

    public static final String nickName = "Player 1";
    public static short HP = 100;
    public static short MP = 30;
    public static byte Level = 0;
    public static byte CurrentRoom = 1;
    public static short Money = 0;
    public static byte attempt = 0;

    public static short XP = 0; //Experience Points
    public static byte ATK = 1; //Attack
    public static byte DEF = 1; //Defence
    public static byte DEX = 1; //Dexterity
    public static short XPForNextLevel = 0;

    public static ArrayList<Item> Inventory = new ArrayList<>();

    public static HashMap<String, Equipment> Equip;

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
        logBlock.Action("get the " + Colors.ORANGE + gold.Amount + " gold!");
        Money += Math.max(gold.Amount,0);
        Draw.call(playerInfoBlock);
    }

    public static int getArmor(){
        Armor armor = (Armor) Equip.get("Armor");
        if(armor != null)
            return armor.GetStats();
        return 0;
    }

    public static int getDamage(){
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
                if (i != null && i.getClass() == Weapon.class)
                    InventoryController.EquipItem((Equipment)
                            GetFromInventory(item -> item.equals(i)), "FirstWeapon");
                break;
            }
        if (Equip.get("Armor") == null){
            for (Item i : Inventory){
                if(i != null && i.getClass() == Armor.class)
                    InventoryController.EquipItem((Equipment)
                            GetFromInventory(item -> item.equals(i)), "Armor");
                break;
            }
        }
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
}
