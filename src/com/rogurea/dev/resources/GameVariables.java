package com.rogurea.dev.resources;

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

    public static final HashMap<String, Float> WeaponMaterialPower;
    static {
        WeaponMaterialPower = new HashMap<>();

        WeaponMaterialPower.put("Wood",0.3f);
        WeaponMaterialPower.put("Stone",0.6f);
        WeaponMaterialPower.put("Copper",0.8f);
        WeaponMaterialPower.put("Iron",1f);
        WeaponMaterialPower.put("Golden",1.3f);
        WeaponMaterialPower.put("Steel",0.5f);
        WeaponMaterialPower.put("Diamond",2f);
    }
    /* Variables for mob */

    public static final int BaseMobDamageStat = 2;

    public static final int BaseMobDefence = 1;

    public static final int N = 5;

    public static int BaseMobLevel = 1;

    public static final int BaseMobHP = 10;

    public static HashMap<String, Float> MobTypeEmpower = new HashMap<>();

    static {
        MobTypeEmpower.put("Rat", 0.5f);
        MobTypeEmpower.put("Skeleton", 0.55f);
        MobTypeEmpower.put("Goblin", 0.7f);
        MobTypeEmpower.put("Bandit", 1f);
    }

    /* Variables for player */

    public static int BaseReqXP = 50;

    public static final double ProgressionCoefficient = 1.3;

    public static final short BasePlayerATK = 1;

    public static final short BasePlayerDEF = 1;

    public static final short BasePlayerDEX = 1;
}
