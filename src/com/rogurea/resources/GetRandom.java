/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.resources;

import com.rogurea.base.Debug;
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
        int leftBound = (int) RogueraGameSystem.getBaseFloorProgression();
        int rightBound = (int) (RogueraGameSystem.getBaseFloorProgression() * 3);
        return ThreadLocalRandom.current().nextInt(leftBound, rightBound);
    }

    public static int getRandomMobHP(){
        int leftBound = (int) RogueraGameSystem.getBaseFloorProgression();
        int rightBound = (int) (RogueraGameSystem.getBaseFloorProgression() * 3);
        return ThreadLocalRandom.current().nextInt(leftBound,rightBound);
    }

    public static int getRandomPotionTypeIndex(){
       if(rollD20() >= 17){
           return 3;
       } else {
         return RNGenerator.nextInt(3);
       }
    }

    public static int rollD20(){
        int diceSum = 0;
        for(int i = 0; i < 20; i++){
            diceSum += RNGenerator.nextInt(2);
        }
        //Debug.toLog("[RANDOM][ROLL_D20] Dice sum = "+diceSum);
        return diceSum;
    }
}
