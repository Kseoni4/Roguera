package com.rogurea.main.player;
import com.rogurea.main.map.Dungeon;

import static com.rogurea.main.resources.ViewObject.inventoryMenu;
import static com.rogurea.main.resources.ViewObject.logBlock;


public class KeyController {
    public static char GetKey (Character key){
            switch (key){
                case 'r':
                    Dungeon.RegenRoom();
                    return 'r';
                case 'c':
                    logBlock.Clear();
                    return 'c';
                case 'i':
                    inventoryMenu.show();
                    return 'i';
                default:
                    break;
            }
            return ' ';
    }
}
