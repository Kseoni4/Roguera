/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea;

import com.rogurea.dev.GameLoop;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.view.IViewBlock;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.dev.view.ViewObjects;

import java.io.IOException;

public class devMain {

    public static boolean isDebug = false;

    public static boolean isSaveToLogFile = false;

    public static boolean isClearMap = false;

    public static void main(String[] args) throws IOException, InterruptedException {

        checkCLI(args);

        GameResources.loadFont();

        TerminalView.initTerminal();

        GameResources.loadResources();

        ViewObjects.LoadViewObjects();

        TerminalView.setGameScreen();

        Dungeon.Generate();

        TerminalView.reDrawAll(IViewBlock.empty);

        GameLoop gameLoop = new GameLoop();

        gameLoop.start();
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
}
