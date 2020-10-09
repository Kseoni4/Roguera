package com.rogurea;

import com.googlecode.lanterna.Symbols;
import com.rogurea.research.R_Dungeon;
import com.rogurea.research.R_GameLoop;
import com.rogurea.research.T_View;

import java.io.IOException;

public class Main{

    public static void main(String[] args) throws IOException {

        R_Dungeon.Generate();
        R_GameLoop.Start();

        System.out.println("Closed");

    }
}


