/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.view;

import com.rogurea.dev.base.Debug;

import java.io.IOException;
import java.util.Arrays;

public class Draw {

    public static int DrawCallCount = 0;
    public static int DrawResetCount = 0;
    public static int DrawInitCount = 0;

    public static void call(IViewBlock viewBlock){
        DrawCallCount++;
        viewBlock.Draw();
        flush();
    }

    public static void reset(IViewBlock viewBlock){
        DrawResetCount++;
        viewBlock.Reset();
        flush();
    }

    public static void init(IViewBlock viewBlock){
        DrawInitCount++;
        viewBlock.Init();
        flush();
    }

    public static void flush(){
        try {
            TerminalView.terminal.flush();
        } catch (IOException e) {
            Debug.toLog(Arrays.toString(e.getStackTrace()));
        }
    }

    public static void clear(){
        try{
            TerminalView.terminal.clearScreen();
        } catch (IOException e){
            Debug.toLog(Arrays.toString(e.getStackTrace()));
        }
    }

}
