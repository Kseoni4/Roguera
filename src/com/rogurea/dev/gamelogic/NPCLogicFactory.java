/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.gamelogic;

import com.rogurea.dev.items.Item;
import com.rogurea.dev.view.InventoryWindow;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NPCLogicFactory {

    private static final Consumer<ArrayList<Item>> tradeItems = (ArrayList<Item> traderInventory) -> {
        new InventoryWindow(traderInventory).show();
    };

    public static Consumer<ArrayList<Item>> getTraderLogic(){
        return tradeItems;
    }
}
