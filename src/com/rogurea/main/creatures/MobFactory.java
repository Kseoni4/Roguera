package com.rogurea.main.creatures;

import java.util.ArrayList;
import java.util.Random;

public class MobFactory {

    static String[] MobNames = {
            "Rat",
            "Goblin",
            "Skelleton"
    };

    static Random random = new Random();

    public static ArrayList<Mob> getMobs(){

        ArrayList<Mob> OutMobList = new ArrayList<>();

        for(int i = 0; i < 3;i++){
            String name = MobNames[random.nextInt(MobNames.length-1)];
            OutMobList.add(
                    new Mob(name, name.charAt(0), 15)
            );
        }

        return OutMobList;
    }

}
