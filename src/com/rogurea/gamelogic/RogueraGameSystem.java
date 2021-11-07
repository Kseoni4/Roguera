package com.rogurea.gamelogic;


import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GameVariables;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class RogueraGameSystem {

    /**
     * Формула базовой прогрессии игрока<br>
     * Вычисляется как X = Level^p * ProgressionCoefficient
     * @return значение прогрессии на текущем уровне игрока
     */
    public static int getBaseProgression(){
        return (int) ((int) Math.pow(Dungeon.player.getPlayerData().getLevel(),GameVariables.Psmall) * GameVariables.PROGRESSION_COEFFICIENT);
    }

    /**
     * Формула требуемого опыта для перехода на следующий уровень<br>
     * ReqEXP = X * 10^2;
     * @return количество требуемого опыта округлённоё до целого.
     */
    public static int getRequirementEXP(){
        return (int) Math.round(getBaseProgression() * Math.pow(10,2));
    }

    public static int getNextHP(){
        return Dungeon.player.getHP() + 10;
    }

    public static int getBaseATK(){
        return getBaseProgression()/5;
    }

    public static int getPBonus(){
        return (int) (Math.sqrt(getBaseProgression())*2);
    }

    public static int getPScoreBonus(){
        return (int) ((Math.pow(Math.log(getBaseProgression()),2)) / 2) + 1;
    }
}
