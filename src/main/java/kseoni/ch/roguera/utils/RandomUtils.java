package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.Window;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class RandomUtils {

    @Getter
    private static Random random;

    @Getter
    private static SecureRandom strongRandom;

    @Getter
    private static long seed;

    static {
        init();
    }

    private static void init(){
        strongRandom = new SecureRandom();
        seed = strongRandom.nextLong();
        random = new Random(seed);
    }

    private static void init(long seed){
        RandomUtils.seed = seed;
        random.setSeed(seed);
    }

    public static void setSeed(long seed){
        init(seed);
    }

    public static Position getRandomPosition(){
        return getRandomPosition(Window.get().getWight(), Window.get().getHeight());
    }

    public static Position getRandomPosition(int boundX, int boundY){
        return new Position(random.nextInt(0,boundX), random.nextInt(0,boundY));
    }
}
