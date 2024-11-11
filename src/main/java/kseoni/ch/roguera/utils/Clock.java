package kseoni.ch.roguera.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Clock {

    private final long NANOS_PER_MILLIS = 1000000;

    @Getter
    private static Clock instance = new Clock(30);

    @Setter
    private long fpsCap;

    private Clock(long fps){
        this.fpsCap = fps;
    }

    @SneakyThrows
    public void tick(long millis){
        long now = System.nanoTime();
        long elapsedTime = (now - millis);

        TimeUnit.MILLISECONDS.sleep(fpsCap - elapsedTime);
    }

    public double deltaTime(long millis){
        long now = System.nanoTime();
        long elapsedTime = now - millis;

        double deltaTime = elapsedTime / (double) NANOS_PER_MILLIS;

        if(deltaTime > 1.0/fpsCap){
            deltaTime = 1.0/fpsCap;
        }

        System.out.println("delta time "+deltaTime);
        return deltaTime;
    }

}
