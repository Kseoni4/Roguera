/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.gamelogic;

import com.rogurea.gamemap.Dungeon;
import com.rogurea.items.*;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.GetRandom;
import com.rogurea.resources.Model;

import java.util.concurrent.ThreadLocalRandom;

public class ItemGenerator {

    private static String[] weaponModels = {"ShortSword", "LongSword", "Knife"};

    private static String[] armorModels = {"Chest", "Hat", "Leg"};

    private static String[] bossWeaponNames = {"Sword of Darkness", "Power of Doom", "Abyss Demolish", "Eternity Sufferer"};

    private static String[] bossArmorNames = {"Armor of Abyss", "Strength of Stars", "Armodium Suit", "Vortex of Power"};

    public static Equipment getRandomWeaponEquipment() {
        Model wp = GameResources.getModel(weaponModels[GetRandom.RNGenerator.nextInt(weaponModels.length)]);

        wp.changeName(GameResources.getWeaponNames().get(GetRandom.RNGenerator.nextInt(GameResources.getWeaponNames().size())));

        Model _wp = new Model(wp.getModelName(), wp.get().getCharacter());

        return new Weapon(_wp.getModelName(),
                _wp,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Math.min(Dungeon.getCurrentFloor().get().getFloorNumber(),Item.Materials.values().length))]);
    }
    public static Equipment getRandomArmorEquipment() {
        Model ar = GameResources.getModel("Chest");
        ar.changeName(GameResources.getArmorNames().get(GetRandom.RNGenerator.nextInt(GameResources.getArmorNames().size())));
        Model _ar = new Model(ar.getModelName(), ar.get().getCharacter());
        return new Armor(_ar.getModelName(),
                _ar,
                Item.Materials.values()[GetRandom.RNGenerator.nextInt(Math.min(Dungeon.getCurrentFloor().get().getFloorNumber(),Item.Materials.values().length))]);
    }

    public static Item getRandomPotion(){
        Model pt = new Model(GameResources.getModel("Potion"));
        return new Potion("Potion", pt);
    }

    public static Gold getRandomGold(){
        return new Gold();
    }

    public static Weapon getSpecialBossWeapon(){
        int randomNameIndex = ThreadLocalRandom.current().nextInt(bossWeaponNames.length);
        int weaponDamage = (int) (RogueraGameSystem.getBaseFloorProgression()*Math.sqrt((Dungeon.getCurrentFloor().get().getFloorNumber()/2)));
        Weapon bossWeapon = new Weapon(bossWeaponNames[randomNameIndex],GameResources.getModel("LongSword"), Item.Materials.DIAMOND);
        bossWeapon.setDamage(weaponDamage);
        bossWeapon.model.changeName(bossWeapon.getName());
        bossWeapon.model.changeColor(Colors.VIOLET);
        return bossWeapon;
    }

    public static Armor getSpecialBossArmor(){
        int randomNameIndex = ThreadLocalRandom.current().nextInt(bossArmorNames.length);
        int armorDef = (int) (RogueraGameSystem.getBaseFloorProgression()*Math.sqrt((Dungeon.getCurrentFloor().get().getFloorNumber()/2)));
        Armor bossArmor = new Armor(bossArmorNames[randomNameIndex],GameResources.getModel("Chest"), Item.Materials.DIAMOND);
        bossArmor.setDefence(armorDef);
        bossArmor.model.changeName(bossArmor.getName());
        bossArmor.model.changeColor(Colors.VIOLET);
        return bossArmor;
    }
}
