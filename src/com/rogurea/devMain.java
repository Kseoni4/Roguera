package com.rogurea;

import com.rogurea.dev.GameLoop;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.view.IViewBlock;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.dev.view.ViewObjects;

import java.io.IOException;

public class devMain {

    public static void main(String[] args) throws IOException {
        TerminalView.initTerminal();

        GameResources.loadResources();

        ViewObjects.LoadViewObjects();

        TerminalView.setGameScreen();

        Dungeon.Generate();

        TerminalView.reDrawAll(IViewBlock.empty);

        GameLoop gameLoop = new GameLoop();

        gameLoop.start();
    }
}
