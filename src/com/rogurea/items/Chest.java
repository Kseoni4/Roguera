/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.items;

import com.rogurea.base.Entity;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;
import com.rogurea.view.InventoryWindow;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Chest extends Entity {
    private final ArrayList<Item> loot = new ArrayList<>();
    private static final Model chest = new Model("chest", Colors.GOLDEN, Colors.B_GREYSCALE_237, '≡');

/*    public void reload(){
        this.loot.forEach(item -> item.model.reloadModel());
    }*/

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
            new InventoryWindow(loot).show();
        };
    }

    private void putLoot(){
        int randomLootCount = ThreadLocalRandom.current().nextInt(0,5) * Dungeon.getCurrentFloor().getFloorNumber();
        for(int i = 0; i < randomLootCount; i++){
            loot.add(ItemGenerator.getRandomWeaponEquipment());
        }
        for(int i = 0; i < randomLootCount; i++){
            loot.add(ItemGenerator.getRandomArmorEquipment());
        }
    }
}
