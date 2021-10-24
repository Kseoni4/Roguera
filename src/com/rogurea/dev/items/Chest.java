/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.base.Entity;
import com.rogurea.dev.gamelogic.ItemGenerator;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.InventoryWindow;

import java.util.ArrayList;

public class Chest extends Entity {
    private final ArrayList<Item> loot = new ArrayList<>();
    private static final Model chest = new Model("chest", Colors.GOLDEN, Colors.B_GREYSCALE_237, '≡');

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
        for(int i = 0; i < 5; i++){
            loot.add(ItemGenerator.getRandomWeaponEquipment());
        }
    }
}
