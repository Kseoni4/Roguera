package com.rogurea.workers;

import com.rogurea.base.Debug;
import com.rogurea.creatures.Mob;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.player.Player;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MapCleanWorker implements Runnable {
    @Override
    public void run() {
        Debug.toLog("[MAP_CLEAN_WORKER] Started");
        while(!Thread.currentThread().isInterrupted()) {
            try {
                if(isMobsNotAlive()) {
                    cleanPlayerDuplicates();

                    Draw.call(ViewObjects.mapView);

                    break;
                }

                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                break;
            }
        }
        Debug.toLog("[MAP_CLEAN_WORKER] Ended");
    }

    private boolean isMobsNotAlive(){
      return Arrays.stream(Dungeon.getCurrentRoom().getObjectsByTag("creature.mob")).allMatch(gameObject -> ((Mob) gameObject).isDead());
    }

    private void cleanPlayerDuplicates() {

        Debug.toLog("[MAP_CLEAN_WORKER] Try to clean duplicates");
        boolean isRemoved = Dungeon.getCurrentRoom().getObjectsSet()
                .removeIf(gameObject ->
                        gameObject.model.get().equals(Dungeon.player.getModel()) && gameObject.id != Dungeon.player.id);

        Debug.toLog("[MAP_CLEAN_WORKER] "+(isRemoved ? "duplicate(s) has been removed from room GameObject set" : "no duplicates has been found or removed from room GameObject set"));

        Dungeon.getCurrentRoom().getCells()
                .forEach(cell -> {
                    if (cell.getFromCell() instanceof Player && cell.getFromCell().id != Dungeon.player.id)
                        cell.clear();
                });
    }
}
