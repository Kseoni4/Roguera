package kseoni.ch.roguera.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class Clock {


    private final static long NANOS_PER_SECOND = 1_000_000_000L;
    private final static long NANOS_PER_MILLI = 1_000_000L;

    @Getter
    private static Clock instance = new Clock(30);

    @Setter
    private long fpsCap;

    @Getter
    private double currentFps;

    @Getter
    private double deltaTime;

    private long lastFpsSampleNanos = System.nanoTime();
    private int framesSinceSample = 0;

    private Clock(long fps){
        this.fpsCap = fps;
    }

    @SneakyThrows
    public void tick(long frameStartNanos){
        long now = System.nanoTime();
        long elapsedNanos = now - frameStartNanos;
        deltaTime = elapsedNanos / (double) NANOS_PER_SECOND;

        long targetNanos = NANOS_PER_SECOND / fpsCap;
        long sleepNanos = targetNanos - elapsedNanos;

        if(sleepNanos > 0) {
            TimeUnit.NANOSECONDS.sleep(sleepNanos);
        }

        framesSinceSample++;
        long sinceSample = System.nanoTime() - lastFpsSampleNanos;
        if (sinceSample >= NANOS_PER_SECOND / 2) { // обновляем каждые 0.5с
            currentFps = framesSinceSample * (NANOS_PER_SECOND / (double) sinceSample);
            framesSinceSample = 0;
            lastFpsSampleNanos = System.nanoTime();
        }
    }

    public double deltaTime(long millis){
        long now = System.nanoTime();
        long elapsedTime = now - millis;

        double deltaTime = elapsedTime / (double) NANOS_PER_MILLI;

        if(deltaTime > 1.0/fpsCap){
            deltaTime = 1.0/fpsCap;
        }

        System.out.println("delta time "+deltaTime);
        return deltaTime;
    }

}
