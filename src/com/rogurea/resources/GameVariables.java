/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.resources;

import com.rogurea.gamelogic.RogueraGameSystem;

import java.util.HashMap;

public class GameVariables {

    /* Variables for Thread.sleep(). In milliseconds */

    public static final int THREAD_FAST = 300;

    public static final int THREAD_VERY_FAST = 200;

    public static final int THREAD_INSTANT = 50;

    public static final int THREAD_NORMAL = 500;

    public static final int FIGHT_SPEED = 350;

    public static final int THREAD_SECOND = 1000;

    public static final int THREAD_DCI = 20;

    /* Variables for items */

    public static final int WEAPON_BASE_DMG = 2;

    public static final int ARMOR_BASE_DEF = 1;

    public static final HashMap<String, Float> MATERIAL_POWER;
    static {
        MATERIAL_POWER = new HashMap<>();

        MATERIAL_POWER.put("wood",1.1f);
        MATERIAL_POWER.put("stone",1.3f);
        MATERIAL_POWER.put("copper",2f);
        MATERIAL_POWER.put("iron",2.5f);
        MATERIAL_POWER.put("gold",3f);
        MATERIAL_POWER.put("steel",3.2f);
        MATERIAL_POWER.put("diamond",5f);
    }
    /* Variables for mob */

    public static final int BASE_MOB_ATK = 2;

    public static final int BASE_MOB_DEF = 1;

    public static final int N = 5;

    public static final int BASE_MOB_LEVEL = 1;

    public static final int BASE_MOB_HP = 10;

    public static HashMap<String, Float> MobTypeEmpower = new HashMap<>();

    static {
        MobTypeEmpower.put("Rat", 0.5f);
        MobTypeEmpower.put("Skeleton", 0.55f);
        MobTypeEmpower.put("Goblin", 0.7f);
        MobTypeEmpower.put("Bandit", 1f);
    }

    /* Variables for player */

    public static int BaseReqXP = 50;

    public static final double Psmall = 1.2;

    public static final double PROGRESSION_COEFFICIENT = 5;

    public static final short BASE_PLAYER_ATK = 1;

    public static final short BASE_PLAYER_DEF = 1;

    public static final short BASE_PLAYER_DEX = 1;
}
