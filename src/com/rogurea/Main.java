package com.rogurea;

import com.rogurea.base.Debug;
import com.rogurea.cinematic.TutorialScene;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Room;
import com.rogurea.mapgenerate.RoomGenerate;
import com.rogurea.resources.GameResources;
import com.rogurea.view.IViewBlock;
import com.rogurea.view.TerminalView;
import com.rogurea.view.ViewObjects;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static boolean newGame = true;

    private static boolean newGameWithExistCharacter = false;

    public static ExecutorService autoLogWorker;

    public Main() {
    }

    public static void disableNewGame() {
        newGame = false;
    }

    public static boolean isNewGame() {
        return newGame;
    }

    public static void enableNewGameWithExistCharacter(){
        newGameWithExistCharacter = true;
    }

    public static void disableNewGameWithExistCharacter(){
        newGameWithExistCharacter = false;
    }

    public static boolean isNewGameWithExistCharacter(){
        return newGameWithExistCharacter;
    }

    public static void enableNewGame() {
        newGame = true;
    }

    public static void startSequence() throws IOException {
        autoLogWorker = Executors.newSingleThreadExecutor();

        Debug.toLog("[GAME] =================================== START SEQUENCE ===================================");

        GameResources.loadFont();

        TerminalView.initTerminal();

        GameResources.loadResources();

        ViewObjects.LoadViewObjects();

        TerminalView.setGameScreen();

        Debug.toLog("[GAME] =================================== RESOURCES LOADED ===================================");

        if (isNewGame()) {
            Debug.toLog("[MAIN] New game");
            new TutorialScene(new Room(0, 60,60, RoomGenerate.RoomSize.BIG)).startSequence();
            Dungeon.generate();
        } else if(isNewGameWithExistCharacter()) {
            Debug.toLog("[MAIN] New game with exist character");
            Dungeon.generate();
        } else {
            Debug.toLog("[MAIN] Load game from save file");
            Dungeon.reloadDungeonAfterLoad();
        }

        TerminalView.reDrawAll(IViewBlock.empty);

        GameLoop gameLoop = new GameLoop();

        try {
            gameLoop.start();
        } catch (Exception e) {
            e.printStackTrace();
            GameLoop.endGameSequence();
        }

        if(autoLogWorker != null) {
            Debug.toLog("[SHUTDOWN] Auto log worker");
            autoLogWorker.shutdownNow();
            try {
                autoLogWorker.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Debug.toLog("=================================== END OF GAME ===================================");
        Debug.toLog("\u001b[38;5;200m[SYSTEM] End of main sequence");
    }
}