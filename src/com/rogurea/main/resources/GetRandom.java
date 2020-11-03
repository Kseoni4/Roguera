package com.rogurea.main.resources;

import java.util.Random;

public class GetRandom {

    public static final Random rnd = new Random();

    public static char WeaponModel(String type){
        if(type.equals("MELEE")){
            return GameResources.MeleeAtlas[rnd.nextInt(GameResources.MeleeAtlas.length)];
        }
        else if (type.equals("RANGE")){
            return GameResources.RangeAtlas[rnd.nextInt(GameResources.RangeAtlas.length)];
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
        return GameResources.SwordLenght[rnd.nextInt(GameResources.SwordLenght.length)];
    }

    public static String WeaponMaterial(){
        return GameResources.MaterialName[rnd.nextInt(GameResources.MaterialName.length)];
    }

    public static String ArmorMaterial(){
        return GameResources.ArmorMaterialName[rnd.nextInt(GameResources.ArmorMaterialName.length)];
    }

    public static String HitLog(int dmg){
        String s = GameResources.HitsMessages[rnd.nextInt(GameResources.HitsMessages.length)];
        s = s.replace("%dmg%", Colors.RED_BRIGHT+dmg+" damage!");
        return s;
    }

}
