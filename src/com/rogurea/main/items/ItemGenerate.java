package com.rogurea.main.items;

import com.rogurea.main.player.Player;

import java.util.ArrayList;
import java.util.Random;

public class ItemGenerate {

    private static Random rng = new Random();

    public static void WeaponForge(int _damage, int _minLevel){

        _minLevel += rng.nextInt(Player.CurrentRoom);

        _damage = ((2 * _minLevel) * 3);

    }

    public static ArrayList<Item> PutItemsIntoRoom(){
        ArrayList<Item> tempItemsList = new ArrayList<>();

        tempItemsList.add(new Weapon("long sword", 50, Weapon._weapontype.MELLE));
        tempItemsList.add(new Weapon("sword", 50, Weapon._weapontype.MELLE));
        tempItemsList.add(new Weapon("bow", 10, Weapon._weapontype.RANGE));
        tempItemsList.add(new Weapon("short bow", 10, Weapon._weapontype.RANGE));

        return tempItemsList;
    }


}
