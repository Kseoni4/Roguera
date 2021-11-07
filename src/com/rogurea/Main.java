package com.rogurea;

import com.rogurea.base.AutoSaveLogWorker;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.GameResources;
import com.rogurea.view.IViewBlock;
import com.rogurea.view.TerminalView;
import com.rogurea.view.ViewObjects;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static boolean newGame = true;
    public static ExecutorService autoLogWorker;

    public Main() {
    }

    public static void loadGame() {
        newGame = false;
    }

    public static boolean isNewGame() {
        return newGame;
    }

    public static void newGame() {
        newGame = true;
    }

    public static void startSequence() throws IOException, InterruptedException {
        autoLogWorker = Executors.newSingleThreadExecutor();

        Debug.toLog("[GAME]Start sequence");

        GameResources.loadFont();

        TerminalView.initTerminal();

        GameResources.loadResources();

        ViewObjects.LoadViewObjects();

        TerminalView.setGameScreen();

        if (newGame) {
            Dungeon.generate();
        } else {
            Dungeon.loadDungeonFromSave();
        }

        TerminalView.reDrawAll(IViewBlock.empty);

        GameLoop gameLoop = new GameLoop();

        try {
            gameLoop.start();
        } catch (Exception var2) {
            var2.printStackTrace();
            GameLoop.endGameSequence();
        }

        gameLoop = null;
        System.out.println("\u001b[38;5;200m[SYSTEM] End of main");
        MainMenu.start(0);
    }
}