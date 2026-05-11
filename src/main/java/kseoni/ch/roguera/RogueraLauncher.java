package kseoni.ch.roguera;

import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.game.GameLoop;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.map.generator.MapGenerate;
import kseoni.ch.roguera.utils.ObjectPool;
import kseoni.ch.roguera.utils.RandomUtils;
import kseoni.ch.roguera.utils.SettingsLoader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.stream.Collectors;

public class RogueraLauncher {

    private static boolean debugShowSystemInfo;

    private static boolean printIntoFile;

    private static Properties settings;

    private static PrintStream printStream;

    public static void main(String[] args) throws FileNotFoundException {

        settings = SettingsLoader.load(SettingsLoader.Settings.GAME_SETTINGS);

        printIntoFile = Boolean.parseBoolean(settings.getProperty("debug.sout.print-into-file"));

        if(printIntoFile) {
            System.out.println("!!!Standard output stream override into file!!!");
            FileOutputStream fileOutputStream = new FileOutputStream("%s-fulllog.txt".formatted(
                    LocalDateTime.now()
            ));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            printStream = new PrintStream(bufferedOutputStream);
            System.setOut(printStream);
        }

        System.out.println("========================ROGUERA===========================");

        String version = settings.getProperty("game.version");

        Runtime.getRuntime().addShutdownHook(new Thread(RogueraLauncher::shutdownSequence));

        if(Boolean.parseBoolean(settings.getProperty("debug.show.system-info")))
            showSystemData();

        System.out.println("===Game Version===");
        System.out.println(version);

        System.out.println("===Load settings===");

        var props = new LinkedHashSet<>(settings.entrySet());

        props.stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(String::valueOf).reversed())).forEach(
                entry ->  System.out.println(entry.getKey() + " : " + entry.getValue())
        );

        AssetPool.get().loadAssets("/char-assets.rca");

        Window window = Window.create(version);

        if(!settings.getProperty("game.random.seed").isEmpty()){
            RandomUtils.setSeed(Long.parseLong(settings.getProperty("game.random.seed")));
        }

        long seed = RandomUtils.getSeed();

        System.out.println("Seed = " + seed);

        System.out.println("===Initializing Game Loop===");

        if(printIntoFile)
            EventLoop.get().send(new Event<>((Runnable) () -> {
                Thread.currentThread().setName("Print stream autoflush thread");
                while (window.isNotClosed()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    printStream.flush();
                }
            }));

        GameLoop gameLoop = new GameLoop();

        gameLoop.init();

        System.out.println("===Start Game Loop===");

        gameLoop.start();

        System.out.println("===Game Loop Ended===");

        window.refresh();
    }


    private static void shutdownSequence(){

        EventLoop.get().send(Event.raise("Shutdown sequence"));

        System.out.println("===Shutdown sequence===");

        System.out.println("===Dump logs into file===");

        boolean debugDumpEvents = Boolean.parseBoolean(settings.getProperty("debug.dump.events"));

        boolean debugDumpObjects = Boolean.parseBoolean(settings.getProperty("debug.dump.objects"));

        if(debugDumpObjects)
            ObjectPool.get().dumpPoolIntoFile();

        if(debugDumpEvents)
            EventLoop.get().dumpEventLog();

        System.out.println("Seed = %s".formatted(RandomUtils.getSeed()));

        if(printIntoFile && Objects.nonNull(printStream)) {
            printStream.flush();

            printStream.close();
        }
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