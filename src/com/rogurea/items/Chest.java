/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.items;

import com.rogurea.base.Entity;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;
import com.rogurea.view.ChestContainsWindow;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Chest extends Entity {
    private final ArrayList<Item> loot = new ArrayList<>();
    public static final Model chest = new Model("chest", Colors.GOLDEN, Colors.B_GREYSCALE_237, '≡');

    public Chest() {
        super(chest);
        putLoot();
        action = ()->{
            String itemList = "";
            for(Item item : loot){
                itemList = itemList.concat(item.getItemMaterial().getColor()
                        + item.model.get().getCharacter()
                        + Colors.R
                        + item.getName()).concat(" ");
            }
            new ChestContainsWindow(loot).show();
        };
    }

    private void putLoot(){
        int randomLootCount = Math.min((ThreadLocalRandom.current().nextInt(0,2) * Dungeon.getCurrentFloor().get().getFloorNumber()),5);
        for(int i = 0; i < randomLootCount; i++){
            loot.add(ItemGenerator.getRandomWeaponEquipment());
        }
        randomLootCount = Math.min((ThreadLocalRandom.current().nextInt(0,2) * Dungeon.getCurrentFloor().get().getFloorNumber()),5);
        for(int i = 0; i < randomLootCount; i++){
            loot.add(ItemGenerator.getRandomArmorEquipment());
        }
    }
}
