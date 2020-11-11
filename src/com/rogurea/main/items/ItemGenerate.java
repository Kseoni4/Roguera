package com.rogurea.main.items;

import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.resources.GetRandom;

import java.util.ArrayList;
import java.util.Random;

public class ItemGenerate {

    private static final Random rng = new Random();

    public static int MoveToForge(Equipment equipment){
        if (equipment instanceof Armor) {
            return ArmorForge(equipment.level, equipment.Material);
        }
        else if (equipment instanceof Weapon){
            return WeaponForge(equipment.level, equipment.Material);
        }
        return 0;
    }

    private static int WeaponForge(int level, String mat){
        return (int) Math.ceil(
                    ((GameVariables.WeaponBaseDmg * GameVariables.WeaponMaterialPower.get(mat))
                    * GameVariables.EmpowerVar) * level
        );
    }

    private static int ArmorForge(int lvl, String mat){
        return (int) ((GameVariables.ArmorBaseStat * GameVariables.ArmorMaterialPower.get(mat))
                * GameVariables.ArmorEmpowerConst) * lvl;
    }

    public static String SetWeaponName(char _model){
        return GameResources.ModelNameMap.get(_model);
    }

    public static ArrayList<Item> PutItemsIntoRoom(int roomnum){
        ArrayList<Item> tempItemsList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {

            tempItemsList.add(new Weapon(GetRandom.WeaponName("MELEE"),
                    (rng.nextInt(100)+1), Weapon._weapontype.MELLE,roomnum));

        }
        tempItemsList.add(new Weapon(GetRandom.WeaponName("RANGE"), 10, Weapon._weapontype.RANGE,roomnum));
        tempItemsList.add(new Armor("armor", 10, GameResources.ArmorChest, roomnum));
        return tempItemsList;
    }


}
