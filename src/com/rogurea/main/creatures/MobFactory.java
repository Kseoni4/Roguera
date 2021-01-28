package com.rogurea.main.creatures;

import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.items.Gold;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.Potion;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Room;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.resources.GetRandom;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public class MobFactory {

    static final String[] MobNames = {
            "Rat",
            "Goblin",
            "Skeleton",
            "Bandit"
    };

    private static final SecureRandom random = new SecureRandom();

    public static ArrayList<Item> GetLoot(){
        ArrayList<Item> bufferLoot = new ArrayList<>();

        for(int i = 0; i < random.nextInt(3); i++) {
            bufferLoot.add(new Weapon(GetRandom.WeaponName("MELEE"),
                    random.nextInt(90) + 10,
                    Weapon._weapontype.MELLE, 1));
        }

        for(int i = 0; i < random.nextInt(2); i++){
            bufferLoot.add(new Potion(10, GameResources.Potion, Potion.PotionType.HEAL));
        }

        bufferLoot.add(new Gold(random.nextInt(99)+1));

        return bufferLoot;
    }

    public static ArrayList<Mob> getMobs(Room room){

        ArrayList<Mob> OutMobList = new ArrayList<>();

        int MaxMobs = 1;
        int MinMobs = 0;

        switch (room.roomSize){
            case SMALL -> {MaxMobs = 2; }
            case MIDDLE -> {MaxMobs = 3; MinMobs = 1;}
            case BIG -> {MaxMobs = 6; MinMobs = 3;}
        }

        for(int i = 0; i < random.nextInt(MaxMobs)+MinMobs;i++){
            String name = MobNames[random.nextInt(MobNames.length)];
            OutMobList.add(
                    new Mob((Colors.RED_BRIGHT+name+Colors.R),
                            name.charAt(0), name, room.NumberOfRoom)
            );
            GameResources.ModelNameMap.put(name.charAt(0), Colors.RED_BRIGHT+name);
        }
        if(OutMobList.size() == 0){
            Formula.GetLvlForMob(room.NumberOfRoom);
        }
        return OutMobList;
    }

}
