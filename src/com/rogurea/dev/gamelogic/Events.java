package com.rogurea.dev.gamelogic;

import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.items.Weapon;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.Model;

public class Events {

    public static Event<Position> putTestItemIntoPos = (position) -> {
        Model wp = GameResources.getModel("ShortSword");
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(new Weapon(wp.getModelName(), wp, Item.Materials.IRON));
    };
}
