package com.rogurea.main.player;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.GameLoop;
import com.rogurea.main.view.InventoryMenu;
import com.rogurea.main.view.Log;

import java.io.IOException;


public class KeyController {
    public static void GetKey (Character key){
            switch (key){
                case 'r':
                    try {
                        GameLoop.RegenRoom();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case 'c':
                    Log.Clear();
                case 'i':
                    InventoryMenu.show();
                default:
                    break;
            }
    }
}
