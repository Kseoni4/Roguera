/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.resources;

import com.rogurea.gamelogic.RogueraGameSystem;
import com.rogurea.gamemap.Position;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class GetRandom {

    public static SecureRandom RNGenerator = new SecureRandom();

    public static void SetRNGSeed(byte[] seed){
        RNGenerator.setSeed(seed);
    }

    public static Position getPosition(){
        return new Position(RNGenerator.nextInt(5), RNGenerator.nextInt(6));
    }

    public static String getRandomWoops() {
        return GameResources.getWhoopsText().get(RNGenerator.nextInt(GameResources.getWhoopsText().size()));
    }

    public static String getRandomMobName(){
        return GameResources.getMobNames().get(RNGenerator.nextInt(GameResources.getWhoopsText().size()));
    }

    public static int getRandomGoldAmount(){
        int leftBound = RogueraGameSystem.getBaseFloorProgression();
        int rightBound = RogueraGameSystem.getBaseFloorProgression() * 2;
        return ThreadLocalRandom.current().nextInt(leftBound, rightBound);
    }

    public static int getRandomMobHP(){
        int leftBound = RogueraGameSystem.getBaseFloorProgression();
        int rightBound = RogueraGameSystem.getBaseFloorProgression() * 3;
        return ThreadLocalRandom.current().nextInt(leftBound,rightBound);
    }
}
