/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamelogic;

import com.rogurea.dev.creatures.Creature;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.items.Chest;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.items.Weapon;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.GetRandom;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.ViewObjects;

public class Events {

    public static Event<Position> putTestItemIntoPos = (position) -> {
        Model wp = GameResources.getModel("ShortSword");
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(new Weapon(wp.getModelName(), wp, Item.Materials.DIAMOND,10));
    };

    public static Event<Position> putChestIntoPos = (position) -> {
        //Model chest = new Model("chest", Colors.GOLDEN, Colors.B_GREYSCALE_237, '≡');
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(new Chest());
    };

    public static void encounter(Creature attacker, Creature victim){
        int dmg = attacker.getDamageByEquipment();
        victim.getHit(dmg);
        ViewObjects.logView.action(victim.getName()
                .concat(" get ")
                .concat(""+dmg)
                .concat(" damage")
                .concat(" from ")
                .concat(attacker.getName())
                .concat(" ")
                .concat(GetRandom.getRandomWoops()));
        Draw.call(ViewObjects.infoGrid);
    }
}
