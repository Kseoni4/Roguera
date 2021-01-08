package com.rogurea.main.gamelogic.rgs;

import com.rogurea.main.items.Potion;
import com.rogurea.main.player.Player;

import java.security.SecureRandom;

public class Formula {

    private static final SecureRandom RNGFormula = new SecureRandom();

    private static short RequirementXP = 0;

    private static byte n = 100;

    private static short p = 0;

    private static byte levelDecade = 10;

    private static byte calcLD(byte currentLVL){
        return (byte) (((currentLVL / 10) + 1) * 10);
    }

    public static short CalculateXPForLevel(short currentRXP, byte currentLVL){
        levelDecade = calcLD(currentLVL);
        RequirementXP = (short) ((currentRXP + (p + n)) * (levelDecade / 10));
        p += p + n;
        return RequirementXP;
    }

    public static boolean IsMiss(int dex){
        return RNGFormula.nextInt(100) >= 100 - dex;
    }

    public static short GetXPForMob(short moblvl){
        return (short) (moblvl * (RNGFormula.nextInt(90) + 10));
    }

    public static byte GetLvlForMob(int currentRoomNumber){
        return (byte) ((currentRoomNumber) % levelDecade);
    }

    public static int GetPlayerATK(){
        return Player.ATK + Player.getDamageByWeapon();
    }

    public static int GetPlayerDEF(){
        return Player.DEF + Player.getArmor();
    }

    public static int GetPotionPointsByType(Potion.PotionType potionType, int potionLevel){
        switch (potionType){
            case HEAL -> {
                return GetHealingPoints(potionLevel);
            }
            case BUF_ATK -> {}

            case BUF_DEF -> {}
        }
        return 1;
    }

    private static int GetHealingPoints(int potionLevel){
        int points = (potionLevel + Player.Level) * (RNGFormula.nextInt(9)+1);
        return Math.min(points, 100);
    }

}
