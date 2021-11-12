package com.rogurea.workers;

import com.rogurea.base.Debug;
import com.rogurea.net.RogueraSpring;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class UpdaterWorker implements Runnable{
    @Override
    public void run() {
        Debug.toLog("[UPDATER_WORKER]Update game session worker has started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                TimeUnit.SECONDS.sleep(5);

                RogueraSpring.updateGameSession();

            } catch (InterruptedException e) {
                Debug.toLog("[UPDATER_WORKER]Error in update game session ("+e.getMessage()+")");
                break;
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
