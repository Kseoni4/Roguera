package kseoni.ch.roguera;

import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.game.GameLoop;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.utils.ObjectPool;
import kseoni.ch.roguera.utils.RandomUtils;
import kseoni.ch.roguera.utils.SettingsLoader;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class RogueraLauncher {

    public static void main(String[] args) {
        System.out.println("========================ROGUERA===========================");

        showSystemData();

        Properties properties = SettingsLoader.load(SettingsLoader.Settings.GAME_SETTINGS);
        String version = properties.getProperty("game.version");

        System.out.println("===Game Version===");
        System.out.println(version);

        System.out.println("===Load settings===");

        for (Map.Entry entry : properties.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        AssetPool.get().loadAssets("src/main/resources/char-assets.rca");

        int width = Integer.parseInt(properties.getProperty("window.size.width"));
        int height = Integer.parseInt(properties.getProperty("window.size.height"));

        Window window = Window.create(width, height, version);

        System.out.println("===Initializing game loop===");

        GameLoop gameLoop = new GameLoop();

        gameLoop.init();

        gameLoop.start();

        window.refresh();

        ObjectPool.get().dumpPoolIntoFile();

        EventLoop.get().dumpEventLog();
    }

    private static void showSystemData(){

        System.out.println("===System Information===");

        String w = System.getProperty("os.name").toLowerCase();
        String osVersion = System.getProperty("os.version");
        String arch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String javaHome = System.getProperty("java.home");
        String userDir = System.getProperty("user.dir");
        String javaClassPath = System.getProperty("java.class.path");
        String userLanguage = System.getProperty("user.language");

        System.out.println("Operating System: " + w);
        System.out.println("OS Version: " + osVersion);
        System.out.println("Architecture: " + arch);
        System.out.println("Java Version: " + javaVersion);
        System.out.println("Java Vendor: " + javaVendor);
        System.out.println("Java Home: " + javaHome);
        System.out.println("User Directory: " + userDir);
        System.out.print("Java Class Path: ");
        if(javaClassPath.split(":").length > 0){
            System.out.println();
            for(String path: javaClassPath.split(":")){
                System.out.println(path);
            }
        } else {
            System.out.println(javaClassPath);
        }
        System.out.println("User Language: " + userLanguage);

        System.out.println("===Memory Usage===");

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        System.out.println("Used Memory: " + usedMemory / 1024 + " KB");
        System.out.println("Free Memory: " + freeMemory / 1024 + " KB");
        System.out.println("Total Memory: " + totalMemory / 1024 + " KB");
    }
}