package com.rogurea.main.items;

import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.resources.GetRandom;

import java.util.ArrayList;
import java.util.Random;

public class ItemGenerate {

    private static Random rng = new Random();

    public static int WeaponForge(int _minLevel){
        return ((2 * GameVariables.WeaponBaseStat) + rng.nextInt(5));
    }

    public static int ArmorForge(){
        return GameVariables.ArmorBaseStat + (rng.nextInt(4));
    }

    public static ArrayList<Item> PutItemsIntoRoom(){
        ArrayList<Item> tempItemsList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {

            tempItemsList.add(new Weapon(GetRandom.WeaponName("MELEE"), 50, Weapon._weapontype.MELLE));

        }
        tempItemsList.add(new Weapon(GetRandom.WeaponName("RANGE"), 10, Weapon._weapontype.RANGE));
        tempItemsList.add(new Armor("leather chest", 10, GameResources.ArmorChest));
        return tempItemsList;
    }


}
