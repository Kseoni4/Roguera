package com.rogurea.main.creatures;

import com.rogurea.main.items.Gold;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.resources.GetRandom;

import java.util.ArrayList;
import java.util.Random;

public class MobFactory {

    static String[] MobNames = {
            "Rat",
            "Goblin",
            "Skelleton"
    };

    static Random random = new Random();

    public static int GetDamage(){
        return GameVariables.BaseMobDamageStat + random.nextInt(3+Player.CurrentRoom);
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

    public static ArrayList<Mob> getMobs(){

        ArrayList<Mob> OutMobList = new ArrayList<>();

        for(int i = 0; i < 3;i++){
            String name = MobNames[random.nextInt(MobNames.length)];
            OutMobList.add(
                    new Mob((Colors.RED_BRIGHT+name+Colors.R),
                            name.charAt(0),
                            15)
            );
            GameResources.ModelNameMap.put(name.charAt(0), Colors.RED_BRIGHT+name);
        }
        return OutMobList;
    }

}
