package com.rogurea.main.player;

import java.io.Serializable;

public class PlayerStatistics implements Serializable {

    public int MobKilled = 0;

    public int PlayerDead = 0;

    public int GoldCollected = 0;

    public int HighestRoomNumber = 0;

    public long HighestPlaySessionTime = 0;

    public int DrinkedPotions = 0;

    public String toString(){
        return "Player statistics of game:\n" +
                "\tMobs killed: " + MobKilled+'\n'
                +"\tPlayer dead: " + PlayerDead + " times\n" +
                "\tGold collected: " + GoldCollected + "$\n" +
                "\tHighest room number: " + HighestRoomNumber + '\n'
                +"\tHighest time of playing session: " + HighestPlaySessionTime + '\n'
                +"\tDrinker potions: " + DrinkedPotions+'\n';
    }
}
