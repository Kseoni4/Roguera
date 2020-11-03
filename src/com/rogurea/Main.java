package com.rogurea;

import com.rogurea.main.map.Dungeon;
import com.rogurea.main.GameLoop;
import com.rogurea.main.resources.GameResources;

public class Main{

    public static void main(String[] args) {

        GameResources.MakeMap();
        Dungeon.Generate();
        GameLoop.Start();

        System.out.println("Game session end");
    }
}


