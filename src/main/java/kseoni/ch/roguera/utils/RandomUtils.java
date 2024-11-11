package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.render.Window;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

public class RandomUtils {

    @Getter
    private static SecureRandom random;

    @Getter
    private static byte[] seed;

    static {
        init();
    }

    private static void init(){
        random = new SecureRandom();
        seed = random.generateSeed(12);
    }

    private static void init(byte[] seed){
        RandomUtils.seed = seed;
        random = new SecureRandom(seed);
    }

    public static void setSeed(byte[] seed){
        init(seed);
    }

    public static Position getRandomPosition(){
        return getRandomPosition(Window.get().getWight(), Window.get().getHeight());
    }

    public static Position getRandomPosition(int boundX, int boundY){
        return new Position(random.nextInt(0,boundX), random.nextInt(0,boundY));
    }


}
