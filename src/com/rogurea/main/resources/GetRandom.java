package com.rogurea.main.resources;

import com.rogurea.main.items.Weapon;

import java.util.Random;

public class GetRandom {

    public static Random rnd = new Random();

    public static char WeaponModel(String type){
        if(type.equals("MELEE")){
            return GameResources.MeleeAtlas[rnd.nextInt(GameResources.MeleeAtlas.length)];
        }
        else if (type.equals("RANGE")){
            return GameResources.RangeAtlas[rnd.nextInt(GameResources.RangeAtlas.length)];
        }
        return '?';
    }

    public static String WeaponName(String type){

        StringBuilder name = new StringBuilder();

        switch (type){
            case "MELEE" -> {
                return name.append(GetLenght())
                        .append(" ")
                        .append(GetMaterial())
                        .append(" ")
                        .append("sword").toString();
            }
            case "RANGE" -> {
                return name
                        .append(GetMaterial())
                        .append(" ")
                        .append("bow").toString();
            }
        }

        return "";
    }

    private static String GetLenght(){
        return GameResources.SwordLenght[rnd.nextInt(GameResources.SwordLenght.length)];
    }

    private static String GetMaterial(){
        return GameResources.MaterialName[rnd.nextInt(GameResources.MaterialName.length)];
    }

    public static String HitLog(int dmg){
        String s = GameResources.HitsMessages[rnd.nextInt(GameResources.HitsMessages.length)];
        s = s.replace("%dmg%", Colors.RED_BRIGHT+dmg+" damage!");
        return s;
    }

}
