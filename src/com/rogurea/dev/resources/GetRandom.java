/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.resources;

import com.rogurea.dev.gamemap.Position;

import java.security.SecureRandom;

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
}
