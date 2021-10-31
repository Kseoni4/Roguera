package com.rogurea.net;

import java.util.concurrent.TimeUnit;

public class UpdaterWorker implements Runnable{
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                TimeUnit.SECONDS.sleep(10);
                JDBСQueries.updateGameSession();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
