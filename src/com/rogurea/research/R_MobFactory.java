package com.rogurea.research;

import com.rogurea.main.Creature;

import java.util.ArrayList;
import java.util.Random;

public class R_MobFactory {

    static String[] MobNames = {
            "Rat",
            "Goblin",
            "Skelleton"
    };

    static Random random = new Random();

    static ArrayList<R_Mob> getMobs(){

        ArrayList<R_Mob> OutMobList = new ArrayList<>();

        for(int i = 0; i < 3;i++){
            String name = MobNames[random.nextInt(MobNames.length-1)];
            OutMobList.add(
                    new R_Mob(name, name.charAt(0), 15)
            );
        }

        return OutMobList;
    }

}
