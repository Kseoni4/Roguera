package com.rogurea.main.resources;

import java.util.HashMap;

public class GameVariables {

    /* Variables for Thread.sleep(). In milliseconds */

    public static final int Fast = 300;

    public static final int VeryFast = 200;

    public static final int Instantly = 50;

    public static final int Normal = 500;

    public static final int FightSpeed = 350;

    public static final int Second = 1000;

    public static final int DCI = 20;

    /* Variables for items */

    public static final int WeaponBaseDmg = 2;

    public static final double EmpowerVar = 1.5;

    public static final HashMap<String, Integer> WeaponMaterialPower;

    static {
        WeaponMaterialPower = new HashMap<>();

        int epv = 1;

        epv *= (int) Math.floor(EmpowerVar * 3);

        for (String mat : GameResources.MaterialName){
            WeaponMaterialPower.put(mat, epv);

            epv = (int) Math.floor(epv * 1.3);
        }
    }

    public static final int ArmorBaseStat = 1;

    public static final int ArmorBaseDurability = 4;

    public static final double ArmorDurabilityEmpowerVar = 1.3;

    public static final double ArmorEmpowerConst = 2;

    public static final HashMap<String, Integer> ArmorMaterialPower;

    static {
        ArmorMaterialPower = new HashMap<>();

        int epv = 1;

        epv *= (int) Math.floor(ArmorBaseDurability * ArmorDurabilityEmpowerVar);

        for (String mat : GameResources.ArmorMaterialName){
            ArmorMaterialPower.put(mat, epv);

            epv = (int) Math.floor(epv * ArmorDurabilityEmpowerVar);
        }
    }


    /* Variables for mob */

    public static final int BaseMobDamageStat = 1;
    public static final double MobDamageEmpower = 1.6;
}
