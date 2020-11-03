package com.rogurea.main.player;

import com.rogurea.main.items.*;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.LogBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class Player {
    public final static char PlayerModel = GameResources.PlayerModel;

    public static class Pos{
        public static int x = 1;
        public static int y = 1;
    }

    public static final String nickName = "Player 1";
    public static int HP = 100;
    public static int MP = 30;
    public static int Level = 1;
    public static int CurrentRoom = 1;
    public static int Money = 0;
    public static int attempt = 0;

    public static ArrayList<Item> Inventory = new ArrayList<>();

    public static HashMap<String, Item> Equip;

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
            LogBlock.Action("get the " + "\u001b[38;5;202m" + item.name + "\u001b[0m" + '!');
            Player.AutoEquip();
        }
        else{
            LogBlock.Event("Your inventory is full!");
        }
    }

    public static void GetGold(Gold gold){
        LogBlock.Action("get the " + Colors.ORANGE + gold.Amount + " gold!");
        Money += gold.Amount;
    }

    public static int getArmor(){
        Armor armor = (Armor) Equip.get("Armor");
        if(armor != null)
            return armor.getDefense();
        return 0;
    }

    public static int getDamage(){
        Weapon wp = (Weapon) Equip.get("FirstWeapon");
        if(wp != null)
            return wp.getDamage();
        return 0;
    }

    public static Item GetFromInventory(Predicate<Item> predicate){
        Item _item = Inventory.stream().filter(predicate).findAny().orElse(null);
        Inventory.remove(_item);
        return _item;
    }

    public static void EquipWeapon(String place, Weapon weapon){
        InventoryController.EquipItem(weapon, place);
/*
        LogBlock.Action("equip an " + Colors.ORANGE + weapon.name);
*/
    }

    public static void AutoEquip() {
        if (Equip.get("FirstWeapon") == null)
            for (Item i : Inventory) {
                if (i != null && i.getClass() == Weapon.class)
                    InventoryController.EquipItem(GetFromInventory(item -> item.equals(i)), "FirstWeapon");
                break;
            }
        if (Equip.get("Armor") == null){
            for (Item i : Inventory){
                if(i != null && i.getClass() == Armor.class)
                    InventoryController.EquipItem(GetFromInventory(item -> item.equals(i)), "Armor");
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
