package com.rogurea.gamelogic;

import com.rogurea.creatures.Mob;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GetRandom;

import java.util.concurrent.ThreadLocalRandom;

public class MobFactory {
    public static Mob newMob(){
        int randomMobHP = ThreadLocalRandom.current().nextInt(1,10) * Dungeon.getCurrentFloor().getFloorNumber();
        return new Mob(randomMobHP, GetRandom.getRandomMobName());
    }
}
