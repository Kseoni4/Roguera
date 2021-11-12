package com.rogurea.gamelogic;


import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GameVariables;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RogueraGameSystem {

    /**
     * Формула базовой прогрессии игрока<br>
     * Вычисляется как X = Level^p * ProgressionCoefficient
     * @return значение прогрессии на текущем уровне игрока
     */
    public static int getBaseLevelProgression(){
        return (int) ((int) Math.pow(Dungeon.player.getPlayerData().getLevel(),GameVariables.Psmall) * GameVariables.PROGRESSION_COEFFICIENT);
    }

    public static int getBaseFloorProgression(){
        AtomicInteger floorNumber = new AtomicInteger(1);
        Dungeon.getCurrentFloor().ifPresent(floor -> floorNumber.set(floor.getFloorNumber()));

        return (int) ((int) Math.pow(floorNumber.get(),GameVariables.Psmall) * GameVariables.PROGRESSION_COEFFICIENT);
    }

    /**
     * Формула требуемого опыта для перехода на следующий уровень<br>
     * ReqEXP = X * 10^2;
     * @return количество требуемого опыта округлённоё до целого.
     */
    public static int getRequirementEXP(){
        return (int) Math.round(getBaseLevelProgression() * Math.pow(10,2));
    }

    public static int getNextHP(){
        return Dungeon.player.getHP() + 10;
    }

    public static int getBaseATK(){
        return getBaseLevelProgression()/5;
    }

    public static int getBaseDEF(){
        return getBaseLevelProgression()/5 + 1;
    }

    public static int getPBonus(){
        return (int) (Math.sqrt(getBaseFloorProgression())*2);
    }

    public static int getPScoreBonus(){
        return Math.max((int) ((Math.pow(Math.log(getBaseFloorProgression()),2)) / 2) + 4,2);
    }

    public static boolean isVariablesOk(){
        float sum = 0;
        for(Field field : GameVariables.class.getFields()) {
            try {
                if(field.getType().equals(int.class)) {
                    sum += field.getInt(field);

                } else if(field.getType().equals(double.class)) {
                    sum += field.getDouble(field);

                } else if(field.getType().equals(float.class)){
                    sum += field.getFloat(field);

                } else if(field.getType().equals(HashMap.class)) {
                    for(Object obj : ((HashMap) field.get(field)).values()){
                        sum += (float) obj;
                    }
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        int intSum = Float.floatToIntBits(sum);

        byte[] encodedValues = Base64.getEncoder().encode(("" + intSum).getBytes());

        String s = new String(encodedValues);

        return false;
    }
}
