package com.rogurea.main.gamelogic.rgs;

import com.rogurea.main.player.Player;
import com.rogurea.main.view.Draw;

import static com.rogurea.main.view.ViewObjects.logBlock;
import static com.rogurea.main.view.ViewObjects.playerInfoBlock;

public class Events {

    public static void getNewLevel(){
        Player.Level++;
        Player.XPForNextLevel = Formula.CalculateXPForLevel(Player.XPForNextLevel, Player.Level);
        Player.XP = 0;
        logBlock.Action("Get the new level!");
        logBlock.Event("Requirement XP for the next level: " + Player.XPForNextLevel);
        Draw.call(playerInfoBlock);
    }
}
