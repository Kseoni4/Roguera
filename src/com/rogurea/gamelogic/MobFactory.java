package com.rogurea.gamelogic;

import com.rogurea.creatures.Boss;
import com.rogurea.creatures.Mob;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GetRandom;

import java.util.concurrent.ThreadLocalRandom;

public class MobFactory {
    public static Mob newMob(){
        int randomMobHP = GetRandom.getRandomMobHP();
        return new Mob(randomMobHP, GetRandom.getRandomMobName());
    }

    public static Mob newBoss(){
        return new Boss();
    }
}
