package com.rogurea.main.gamelogic.rgs;

import com.rogurea.main.player.Player;
import com.rogurea.main.view.Draw;

import static com.rogurea.main.view.ViewObjects.logBlock;
import static com.rogurea.main.view.ViewObjects.playerInfoBlock;

public class Events {

    public static void getNewLevel(){
        Player.Level++;

        Player.ATK = (short) Formula.CalculatePlayerATK(Player.Level);

        Player.DEF = (short) Formula.CalculatePlayerDEF(Player.Level);

        if(Player.DEX < 100){
            Player.DEX = (byte) Formula.CalculatePlayerDEX(Player.Level);
        }

        Player.ReqXPForNextLevel = (short) Formula.GetRequirementXP();

        Player.XP = 0;

        logBlock.Action("Get the new level!");
        logBlock.Event("Requirement XP for the next level: " + Player.ReqXPForNextLevel);

        Draw.call(playerInfoBlock);
    }
}
