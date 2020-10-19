package com.rogurea.main;

import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;

public class BotController extends Thread{

    public int BPointX = 0;
    public int BPointY = 0;

    public void run(){

        BPointX = 3;
        BPointY = 4;

        Dungeon.CurrentRoom[BPointY][BPointX] = GameResources.Bot;

        while(Scans.CheckWall(Dungeon.CurrentRoom[BPointY-1][BPointX])){

            Dungeon.CurrentRoom[BPointY][BPointX] = MapEditor.EmptyCell;

            Dungeon.CurrentRoom[--BPointY][BPointX] = GameResources.Bot;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
