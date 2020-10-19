package com.rogurea;

import com.rogurea.main.map.Dungeon;
import com.rogurea.main.GameLoop;
import com.rogurea.main.resources.GameResources;

import java.io.IOException;

public class Main{

    public static void main(String[] args) throws IOException, IllegalAccessException {

        GameResources.MakeMap();
        Dungeon.Generate();
        GameLoop.Start();

        System.out.println("Closed");

    }
}


