package com.rogurea.main.creatures;

import com.rogurea.main.items.Gold;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.resources.GetRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MobFactory {

    static final String[] MobNames = {
            "Rat",
            "Goblin",
            "Skelleton",
            "Bandit"
    };

    static final HashMap<String, Integer> MobPower;

    static {
        MobPower = new HashMap<>();

        int Power = (int) Math.floor((GameVariables.MobDamageEmpower * GameVariables.BaseMobDamageStat) * 1.2);

        for(String mob : MobNames) {
            MobPower.put(mob, Power);

            Power = (int) Math.floor(Power * GameVariables.MobDamageEmpower);
        }
    }

    static final Random random = new Random();

    public static int GetDamage(String mob, int moblevel){
        return (int) Math.ceil(
                    ((GameVariables.BaseMobDamageStat * MobPower.get(mob))
                * GameVariables.MobDamageEmpower) * moblevel
        );
    }

    public static ArrayList<Item> GetLoot(){
        ArrayList<Item> bufferLoot = new ArrayList<>();

        for(int i = 0; i < random.nextInt(3); i++) {
            bufferLoot.add(new Weapon(GetRandom.WeaponName("MELEE"),
                    random.nextInt(90) + 10,
                    Weapon._weapontype.MELLE));
        }

        bufferLoot.add(new Gold(random.nextInt(99)+1));

        return bufferLoot;
    }

    public static ArrayList<Mob> getMobs(int roomnum){

        ArrayList<Mob> OutMobList = new ArrayList<>();

        for(int i = 0; i < 3;i++){
            String name = MobNames[random.nextInt(MobNames.length)];
            OutMobList.add(
                    new Mob((Colors.RED_BRIGHT+name+Colors.R),
                            name.charAt(0),
                            random.nextInt(50)+15, name, roomnum)
            );
            GameResources.ModelNameMap.put(name.charAt(0), Colors.RED_BRIGHT+name);
        }
        return OutMobList;
    }

}
