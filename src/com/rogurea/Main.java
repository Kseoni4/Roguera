package com.rogurea;

import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.SavingSystem;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.GameLoop;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.MainMenu;
import com.rogurea.main.view.ViewObjects;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;

public class Main{

    public static final GameLoop gameLoop = new GameLoop();

    public static boolean NewGame = true;

    private static int Normal = 0;

    public static void main(String[] args) throws IOException, InterruptedException {

        Debug.log("=== Main instance has started ===");

        Debug.log("SYSTEM PROPERTIES: \n\t"
                + "OS: " + System.getProperties().getProperty("os.name") + "\n\t"
                + "Architecture: " + System.getProperties().getProperty("os.arch") + "\n\t"
                + "System version: " + System.getProperties().getProperty("os.version") + "\n\t"
                + "Java version: " + System.getProperties().getProperty("java.version") + "\n\t"
                + "Java RNE: " + System.getProperties().getProperty("java.runtime.version") + "\n\t"
                + "Java VM Version: " + System.getProperties().getProperty("java.vm.specification.version") + "\n\t"
                + "Java Сompiler Version: " + System.getProperties().getProperty("java.compiler") + "\n\t"
                + "Java Сlass version: " + System.getProperties().getProperty("java.class.version")
        );

        System.out.println(System.getProperties());

        Debug.log("GAME VERSION: " + GameResources.version);

        MainMenu.start(Normal);
    }
    public static void GameStart() throws IOException, InterruptedException {

        Debug.log("== INITIALIZING TERMINAL INSTANCE ==");

        TerminalView.InitTerminal();

        Debug.log("== STARTING AUTOSAVE LOG THREAD ==");

        Debug.AutoSaveLog autoSaveLog = new Debug.AutoSaveLog();

        autoSaveLog.start();

        Debug.log("== LOADING GAME RESOURCES ==");

/*      Debug.InitDebugWindow();

        Debug.TestFontUnicode(); */

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

        Debug.log("Draw\n" +
                "\t [Calls]: " + Draw.DrawCallCount+'\n'+
                "\t [Resets]: " + Draw.DrawResetCount+'\n'+
                "\t [Inits]: " + Draw.DrawInitCount+'\n');

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

            Dungeon.Generate();

            Dungeon.roomOlds.set(Player.CurrentRoom-1, Dungeon.savedRoomOld);

            if(!Dungeon.savedRoomOld.IsEndRoom)
                Dungeon.roomOlds.get(Player.CurrentRoom - 1).nextRoomOld = Dungeon.roomOlds.get(Player.CurrentRoom);

            Dungeon.CurrentRoom = Dungeon.savedRoomOld.RoomStructure;

            MapEditor.setIntoCell(GameResources.GetModel("SWall"),new Position(Dungeon.savedRoomOld.RoomStructure[0].length/2,0));

            BaseGenerate.PutPlayerInDungeon((byte) Player.GetPlayerPosition().x,
                    (byte) Player.GetPlayerPosition().y,
                    Dungeon.GetCurrentRoom().RoomStructure);

        } catch (IndexOutOfBoundsException e) {
            Debug.log("ERROR: load game is failed");

            e.printStackTrace();
        }
    }
}


