package com.rogurea;

import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.SavingSystem;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.GameLoop;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.MainMenu;
import com.rogurea.main.view.ViewObjects;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;

public class Main{

    public static final GameLoop gameLoop = new GameLoop();

    public static boolean NewGame = true;

    public static void main(String[] args) throws IOException, InterruptedException {

        Debug.log("=== Game instance has started ===");

        Debug.log("SYSTEM PROPERTIES: \n\t"
                + "OS: " +             System.getProperties().getProperty("os.name")+"\n\t"
                + "Architecture: " +   System.getProperties().getProperty("os.arch")+"\n\t"
                + "System version: " + System.getProperties().getProperty("os.version")+"\n\t"
                + "Java version: " +   System.getProperties().getProperty("java.version"));

        Debug.log("GAME VERSION: " + GameResources.version);

        MainMenu.start();

        Debug.log("== INITIALIZING TERMINAL INSTANCE ==");

        TerminalView.InitTerminal();

        Debug.AutoSaveLog autoSaveLog = new Debug.AutoSaveLog();

        autoSaveLog.start();

        Debug.log("== LOADING GAME RESOURCES ==");

        GameResources.LoadResources();

        GameResources.MakeMap();

        Debug.log("== LOADING VIEW BLOCKS ==");

        ViewObjects.LoadViewObjects();

        TerminalView.SetGameScreen();

        Debug.log("== STARTING GAME ==");

        if(NewGame){
            StartNewGame();
        } else{
            StartGameFromSave();
        }

        gameLoop.Start();

        Debug.log("=== Game session end ===");

        autoSaveLog.interrupt();

        autoSaveLog.join();

        Debug.log(Player.playerStatistics.toString());

        Debug.SaveLogToFile();

        System.out.println("Game session end");
    }

    private static void StartNewGame(){
        Debug.log("SYSTEM: Starting new game");

        Player.RandomSeed = GetRandom.RNGenerator.generateSeed(32);

        Dungeon.Generate();
    }

    private static void StartGameFromSave(){
        try {
            Debug.log("SYSTEM: Starting game from saved file " + SavingSystem.GetSaveFileName());

            SavingSystem.loadGame(SavingSystem.GetSaveFileName());

            Dungeon.Generate();

            Dungeon.ChangeRoom(Dungeon.GetCurrentRoom());

            BaseGenerate.PutPlayerInDungeon((byte) Player.GetPlayerPosition().x,
                    (byte) Player.GetPlayerPosition().y,
                    Dungeon.GetCurrentRoom().RoomStructure);

            TerminalView.terminal.flush();
        } catch (ClassNotFoundException | IOException e) {
            Debug.log("ERROR: load game is failed");

            e.printStackTrace();
        }
    }

}


