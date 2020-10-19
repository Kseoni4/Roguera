package com.rogurea.main;

import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.GameResources;

public class BotController_2 extends Thread {

    int BPointY = 6;
    int BPointX = 5;

    @Override
    public void run() {

        while (Scans.CheckWall(Dungeon.CurrentRoom[BPointY-1][BPointX])){

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
