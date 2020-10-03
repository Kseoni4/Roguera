package com.rogurea;

import com.rogurea.research.R_Dungeon;
import com.rogurea.research.R_GameLoop;
import com.rogurea.research.T_View;

import java.io.IOException;

public class Main{

    public static void main(String[] args){

        R_Dungeon.Generate();
        R_GameLoop.Start();

    }
}


