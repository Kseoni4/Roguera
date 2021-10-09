/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.gamelogic;

import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.items.Weapon;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.resources.Model;

public class ItemGenerator {

    private static String[] weapons = {"ShortSword", "LongSword"};

    public static Equipment getItemEquipment() {
        Model wp = GameResources.getModel(weapons[GetRandom.RNGenerator.nextInt(weapons.length)]);
        Model _wp = new Model(wp.getModelName(), wp.get().getCharacter());
        return new Weapon(_wp.getModelName(),
                _wp,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Item.Materials.values().length)]);
    }

}