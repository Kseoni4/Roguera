package com.rogurea.main.gamelogic.rgs;

import com.rogurea.main.player.Player;

import java.security.SecureRandom;

public class Formula {

    private static final SecureRandom rnd = new SecureRandom();

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
        return rnd.nextInt(100) >= 100 - dex;
    }

    public static short GetXPForMob(short moblvl){
        return (short) (moblvl * (rnd.nextInt(90) + 10));
    }

    public static byte GetLvlForMob(int currentRoomNumber){
        return (byte) ((currentRoomNumber * 2) % levelDecade);
    }

    public static int GetPlayerATK(){
        return Player.ATK + Player.getDamage();
    }

    public static int GetPlayerDEF(){
        return Player.DEF + Player.getArmor();
    }

}
