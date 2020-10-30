package com.rogurea.main.player;

import com.rogurea.main.GameLoop;
import com.rogurea.main.view.InventoryMenu;
import com.rogurea.main.view.LogBlock;

import java.io.IOException;


public class KeyController {
    public static char GetKey (Character key){
            switch (key){
                case 'r':
                    GameLoop.RegenRoom();
                    return 'r';
                case 'c':
                    LogBlock.Clear();
                    return 'c';
                case 'i':
                    InventoryMenu.show();
                    return 'i';
                default:
                    break;
            }
            return ' ';
    }
}
