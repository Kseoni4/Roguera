/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.gamelogic;

import com.rogurea.dev.items.*;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.resources.Model;

public class ItemGenerator {

    private static String[] weapons = {"ShortSword", "LongSword"};

    private static String[] armors = {"Chest", "Hat", "Leg"};

    public static Equipment getRandomWeaponEquipment() {
        Model wp = GameResources.getModel(weapons[GetRandom.RNGenerator.nextInt(weapons.length)]);
        Model _wp = new Model(wp.getModelName(), wp.get().getCharacter());
        return new Weapon(_wp.getModelName(),
                _wp,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Item.Materials.values().length)]);
    }
    public static Equipment getRandomArmorEquipment() {
        Model ar = GameResources.getModel("ArmorChest");
        Model _ar = new Model(ar.getModelName(), ar.get().getCharacter());
        return new Armor(_ar.getModelName(),
                _ar,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Item.Materials.values().length)]);
    }

    public static Item getRandomPotion(){
        Model pt = GameResources.getModel("Potion");
        return new Potion("Potion", pt);
    }

}
