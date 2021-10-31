/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.gamelogic;

import com.rogurea.items.Item;
import com.rogurea.view.TraderWindow;

import java.io.Serializable;
import java.util.ArrayList;

public class NPCLogicFactory implements Serializable {

    private static final NPCConsumeAction<ArrayList<Item>> tradeItems = (ArrayList<Item> traderInventory) -> {
        new TraderWindow(traderInventory).show();
    };

    public static NPCConsumeAction<ArrayList<Item>> getTraderLogic(){
        return tradeItems;
    }
}
