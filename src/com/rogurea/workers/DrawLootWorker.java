package com.rogurea.workers;

import com.rogurea.base.Debug;
import com.rogurea.base.GameObject;
import com.rogurea.creatures.Creature;
import com.rogurea.creatures.Mob;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.player.Player;
import com.rogurea.view.Draw;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.rogurea.view.ViewObjects.mapView;

public class DrawLootWorker implements Runnable{
    @Override
    public void run() {
        Debug.toLog("[DRAW_LOOT_WORKER] Start working in room "+Dungeon.getCurrentRoom().roomNumber);
        ArrayList<GameObject> gameObjects = Dungeon.getCurrentRoom().getObjectsSet();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (gameObjects.stream().noneMatch(gameObject -> ((gameObject instanceof Mob) && !(((Mob) gameObject).isDead())))) {
                    Draw.call(mapView);
                    break;
                } else {
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
        } catch (InterruptedException e){
            Debug.toLog("[DRAW_LOOT_WORKER] End of thread");
        }
        Debug.toLog("[DRAW_LOOT_WORKER] End of work");
    }
}
