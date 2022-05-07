package com.rogurea.workers;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.net.ServerRequests;
import com.rogurea.net.SessionData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class UpdaterWorker implements Runnable{

    SessionData sessionData;

    public UpdaterWorker(SessionData sessionData){
        this.sessionData = sessionData;
    }

    @Override
    public void run() {
        Debug.toLog("[UPDATER_WORKER]Update game session worker has started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(3);

                updateSessionData();

                ServerRequests.updateGameSession(sessionData);

            } catch (InterruptedException e) {
                Debug.toLog("[UPDATER_WORKER]Error in update game session (" + e.getMessage() + ")");
                break;
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateSessionData(){
        sessionData.setFloor(Dungeon.currentFloorNumber);
        sessionData.setRoom(Dungeon.getCurrentRoom().roomNumber);
        sessionData.setKills(Dungeon.player.getPlayerData().getKills());
        sessionData.setScore(Dungeon.player.getPlayerData().getScore());
    }

    public void updateSessionAndInterrupt(){
        updateSessionData();
        try {
            ServerRequests.updateGameSession(sessionData);
            Debug.toLog("[UPDATE_WORKER] Session is updated finally");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
