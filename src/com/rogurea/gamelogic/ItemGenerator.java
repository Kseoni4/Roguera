/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.gamelogic;

import com.rogurea.gamemap.Dungeon;
import com.rogurea.items.*;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.GetRandom;
import com.rogurea.resources.Model;

public class ItemGenerator {

    private static String[] weapons = {"ShortSword", "LongSword"};

    private static String[] armors = {"Chest", "Hat", "Leg"};

    public static Equipment getRandomWeaponEquipment() {
        Model wp = GameResources.getModel(weapons[GetRandom.RNGenerator.nextInt(weapons.length)]);
        Model _wp = new Model(wp.getModelName(), wp.get().getCharacter());
        return new Weapon(_wp.getModelName(),
                _wp,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Math.min(Dungeon.getCurrentFloor().getFloorNumber()+3,Item.Materials.values().length))]);
    }
    public static Equipment getRandomArmorEquipment() {
        Model ar = GameResources.getModel("ArmorChest");
        Model _ar = new Model(ar.getModelName(), ar.get().getCharacter());
        return new Armor(_ar.getModelName(),
                _ar,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Math.min(Dungeon.getCurrentFloor().getFloorNumber()+3,Item.Materials.values().length))]);
    }

    public static Item getRandomPotion(){
        Model pt = new Model(GameResources.getModel("Potion"));
        return new Potion("Potion", pt);
    }

    public static Gold getRandomGold(){
        return new Gold();
    }

}
