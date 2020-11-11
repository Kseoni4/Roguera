package com.rogurea;

import com.rogurea.main.map.Dungeon;
import com.rogurea.main.GameLoop;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.TerminalView;

public class Main{

    public static final GameLoop gameLoop = new GameLoop();

    public static void main(String[] args) {

        GameResources.LoadResources();
        GameResources.MakeMap();
        TerminalView.InitTerminal();
        Dungeon.Generate();
        gameLoop.Start();

        System.out.println("Game session end");
    }
}


