package com.rogurea.main.resources;

import com.rogurea.main.items.Armor;
import com.rogurea.main.items.Equipment;
import com.rogurea.main.items.Weapon;

import java.security.SecureRandom;

public class GetRandom {

    public static SecureRandom RNGenerator = new SecureRandom();

    public static void SetRNGSeed(byte[] seed){
        RNGenerator.setSeed(seed);
    }

    public static char WeaponModel(String type){

        if(type.equals("MELEE")){
            return GameResources.MeleeAtlas[RNGenerator.nextInt(GameResources.MeleeAtlas.length)];
        }
        else if (type.equals("RANGE")){
            return GameResources.RangeAtlas[RNGenerator.nextInt(GameResources.RangeAtlas.length)];
        }
        return '?';
    }

    public static String ArmorName(String mat){

        return mat + " " + "chest";
    }

    public static String WeaponName(String type){

        StringBuilder name = new StringBuilder();

        switch (type){
            case "MELEE" -> {
                return name.append(Lenght())
                        .append(" ")
                        .append("%mat%")
                        .append(" ")
                        .append("%name%").toString();
            }
            case "RANGE" -> {
                return name
                        .append("%mat%")
                        .append(" ")
                        .append("bow").toString();
            }
        }

        return "";
    }

    private static String Lenght(){
        return GameResources.SwordLenght[RNGenerator.nextInt(GameResources.SwordLenght.length)];
    }

    public static String Material(Equipment equipment){
        if(equipment.getClass().equals(Weapon.class)){
            return WeaponMaterial();
        }
        else if(equipment.getClass().equals(Armor.class)){
            return ArmorMaterial();
        }
        return "";
    }

    public static String WeaponMaterial(){
        return GameResources.MaterialName[RNGenerator.nextInt(GameResources.MaterialName.length)];
    }

    public static String ArmorMaterial(){
        return GameResources.ArmorMaterialName[RNGenerator.nextInt(GameResources.ArmorMaterialName.length)];
    }

    public static String HitLog(int dmg){
        String s = GameResources.HitsMessages[RNGenerator.nextInt(GameResources.HitsMessages.length)];
        s = s.replace("%dmg%", Colors.RED_BRIGHT+dmg+" damage!");
        return s;
    }

}
