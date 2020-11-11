package com.rogurea.main.view;

import java.io.IOException;

public class Draw {

    public static void call(IViewBlock viewBlock){
        viewBlock.Draw();
        flush();
    }

    public static void reset(IViewBlock viewBlock){
        viewBlock.Reset();
        flush();
    }

    public static void init(IViewBlock viewBlock){
        viewBlock.Init();
        flush();
    }

    private static void flush(){
        try {
            TerminalView.terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
