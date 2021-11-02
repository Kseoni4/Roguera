package com.rogurea.net;

import com.rogurea.base.Debug;

import java.util.concurrent.TimeUnit;

public class UpdaterWorker implements Runnable{
    @Override
    public void run() {
        Debug.toLog("[UPDATER_WORKER]Update game session worker has started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                TimeUnit.SECONDS.sleep(10);
                JDBСQueries.updateGameSession();
            } catch (InterruptedException e) {
                Debug.toLog("[UPDATER_WORKER]Error in update game session ("+e.getMessage()+")");
                break;
            }
        }
    }
}
