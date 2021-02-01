package com.rogurea.main.gamelogic.rgs;

import com.rogurea.main.items.Potion;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameVariables;

import java.security.SecureRandom;
import java.util.ArrayList;

public class Formula {

    private static final SecureRandom RNGFormula = new SecureRandom();

    public static ArrayList<Integer> RoomsForMobLevelUp;

    private static int CalculateBaseDMG(int CurrentRoomNumber, float EmPowerMaterial){

        int BaseDMG = GameVariables.WeaponBaseDmg;

        for(int i = 2; i < CurrentRoomNumber;i++){
            BaseDMG = (int) Math.floor(BaseDMG + (Math.sqrt(i) * EmPowerMaterial));
        }

        return BaseDMG;
    }

    public static int GetWeaponDMG(int RoomNumber, float EmpowerMaterial){
        int DMG = GameVariables.WeaponBaseDmg;
        if(RoomNumber > 2){
            DMG = CalculateBaseDMG(RoomNumber, EmpowerMaterial);
        }
        return (int) Math.floor(DMG + Math.sqrt(RoomNumber) * EmpowerMaterial);
    }

    public static int GetRequirementXP(){
        return GameVariables.BaseReqXP = (int) Math.floor((GameVariables.BaseReqXP * GameVariables.ProgressionCoefficient));
    }

    public static int CalculatePlayerATK(int NewLevel){
        return (int) Math.floor(Player.ATK + (Math.log10(NewLevel)*2));
    }

    public static int CalculatePlayerDEF(int NewLevel){
        return (int) Math.floor(Player.DEF + (Math.sqrt(NewLevel)));
    }

    public static int CalculatePlayerDEX(int NewLevel){
        return (int) Math.floor(Math.log(NewLevel) / Math.log(2));
    }

    public static int GetPlayerATK(){
        return Player.ATK + Player.getDamageByWeapon();
    }

    public static int GetPlayerDEF(){
        return 1;
    }

    public static int GetHPForMob(int LVLm){
        return GameVariables.BaseMobHP * LVLm;
    }

    public static int GetLvlForMob(int RoomNumber){
        if(RoomsForMobLevelUp.contains(RoomNumber)){
            return GameVariables.BaseMobLevel = RoomsForMobLevelUp.indexOf(RoomNumber)+2;
        }else{
            return GameVariables.BaseMobLevel;
        }
    }

    public static int GetXPForMob(String MobType, int LVLm){
        return (int) Math.floor(Math.pow(LVLm,2) * GameVariables.MobTypeEmpower.get(MobType));
    }

    private static int CalculateBaseMobATK(int LVLm, String MobType){
        int BaseATKm = GameVariables.BaseMobDamageStat;

        for(int i = 2; i < LVLm+1; i++){
            BaseATKm = (int) Math.nextUp(BaseATKm + (Math.log10(i) * GameVariables.N) * GameVariables.MobTypeEmpower.get(MobType));
        }
        return BaseATKm;
    }

    public static int GetATKForMob(String MobType, int LVLm, int RoomNumber){
        int BaseATKm = GameVariables.BaseMobDamageStat;

        if(RoomNumber > 3){
            BaseATKm = CalculateBaseMobATK(LVLm, MobType);
        }
        return (int) Math.nextUp(BaseATKm + (Math.log10(LVLm) * GameVariables.N) * GameVariables.MobTypeEmpower.get(MobType));
    }

    private static int CalculateBaseMobDEF(int LVLm, String MobType){
        int BaseDEFm = GameVariables.BaseMobDefence;

        for(int i = 2; i < LVLm+1; i++){
            BaseDEFm = (int) Math.floor(BaseDEFm + Math.sqrt(i) * GameVariables.MobTypeEmpower.get(MobType));
        }
        return BaseDEFm;
    }

    public static int GetDEFForMob(String MobType, int LVLm, int RoomNumber){
        int BaseDEFm = GameVariables.BaseMobDefence;

        if(RoomNumber > 3){
            BaseDEFm = CalculateBaseMobDEF(LVLm, MobType);
        }
        return (int) Math.floor(BaseDEFm + Math.sqrt(LVLm) * GameVariables.MobTypeEmpower.get(MobType));
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

    public static boolean IsMiss(int dex){
        return RNGFormula.nextInt(100) >= 100 - dex;
    }
}
