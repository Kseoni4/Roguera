package com.rogurea;

import com.rogurea.base.Debug;
import com.rogurea.resources.GameResources;
import com.rogurea.view.Draw;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Roguera {

    public static boolean isDebug = true;

    public static boolean isSaveToLogFile = true;

    public static boolean isClearMap = false;

    public static ArrayList<Closeable> terminals = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        checkCLI(args);

        Debug.toLog("[START_UP] CLI args: "+Arrays.toString(args));

        Debug.toLog("SYSTEM PROPERTIES: \n\t"
                + "OS: " + System.getProperties().getProperty("os.name") + "\n\t"
                + "Architecture: " + System.getProperties().getProperty("os.arch") + "\n\t"
                + "System version: " + System.getProperties().getProperty("os.version") + "\n\t"
                + "Java version: " + System.getProperties().getProperty("java.version") + "\n\t"
                + "Java RE: " + System.getProperties().getProperty("java.runtime.version") + "\n\t"
                + "Java VM Version: " + System.getProperties().getProperty("java.vm.specification.version") + "\n\t"
                + "Java Сompiler Version: " + System.getProperties().getProperty("java.compiler") + "\n\t"
                + "Java Сlass version: " + System.getProperties().getProperty("java.class.version")
        );

        Debug.toLog("[VERSION]"+ GameResources.VERSION);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Debug.toLog("[DISCORD_RP]Closing Discord hook." );
            Debug.toLog("[DRAW_CALL] Count: "+ Draw.DrawCallCount);
            Debug.toLog("[DRAW_RESET] Count: "+ Draw.DrawResetCount);
            Debug.toLog("[DRAW_INIT] Count: "+ Draw.DrawInitCount);
            DiscordRPC.discordShutdown();
            //JDBСQueries.closeConnection();
        }));

        startDRP();

        DiscordRPC.discordRunCallbacks();

        MainMenu.start(0);

        DiscordRPC.discordShutdown();
    }
    private static void checkCLI(String[] args){
        for(String argument : args){
            if(argument.equals("--debug")){
                isDebug = true;
            }
            if(argument.equals("--savelog")){
                isSaveToLogFile = true;
            }
            if(argument.equals("--clearmap")){
                isClearMap = true;
            }
        }
    }

    private static void startDRP(){
        DiscordEventHandlers discordEventHandlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            Debug.toLog("[DISCORD_RP] Start RP for user "+ user.username + "#"+user.discriminator);
            DiscordRichPresence.Builder welcomePresence = new DiscordRichPresence.Builder("hi");
            welcomePresence.setDetails("world");
            DiscordRPC.discordUpdatePresence(welcomePresence.build());
        }).build();

        DiscordRPC.discordInitialize("904706382639562823", discordEventHandlers, true);
        DiscordRPC.discordRegister("904706382639562823","");

    }
}
