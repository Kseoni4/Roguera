/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamelogic;

import com.rogurea.creatures.Creature;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.items.Chest;
import com.rogurea.items.Item;
import com.rogurea.items.Weapon;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.GetRandom;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;
import com.rogurea.view.Effect;
import com.rogurea.view.ViewObjects;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.rogurea.view.ViewObjects.mapView;

public class Events {

    public static Event<Position> putTestItemIntoPos = (position) -> {
        Model wp = GameResources.getModel("ShortSword");
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(new Weapon(wp.getModelName(), wp, Item.Materials.DIAMOND,10));
    };

    public static Event<Position> putChestIntoPos = (position) -> {
        //Model chest = new Model("chest", Colors.GOLDEN, Colors.B_GREYSCALE_237, '≡');
        Dungeon.getCurrentRoom().getCell(position).putIntoCell(new Chest());
    };

    private static Consumer<Creature> hitEffectRun = target -> {
        target.model.changeBColor(Colors.B_RED_BRIGHT);
        Draw.call(mapView);
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        target.model.resetBColor();
        Draw.call(mapView);
    };

    public static void encounter(Creature attacker, Creature victim){
        ExecutorService ef = Executors.newSingleThreadExecutor();
        int dmg = attacker.getDamageByEquipment();
        int fullDef = victim.getDefenceByEquipment();
        victim.getHit(dmg);
        ef.execute(new Effect<>(victim, hitEffectRun));
        ef.shutdown();
        ViewObjects.logView.action(victim.getName()
                .concat(" get ")
                .concat(""+Math.max(0,dmg - fullDef))
                .concat(" damage")
                //.concat("(DEF:").concat(String.valueOf(victim.getDefenceByEquipment())).concat(")")
                .concat(" from ")
                .concat(attacker.getName())
                .concat(" ")
                .concat(GetRandom.getRandomWoops()));
        Draw.call(ViewObjects.infoGrid.getFirstBlock());
    }
}
