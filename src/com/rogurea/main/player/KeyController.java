package com.rogurea.main.player;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.resources.GameResources;

import java.util.HashMap;

public class KeyController {

    private static final HashMap<Character, Runnable> KeyMap = GameResources.GetKeyMap();

    public static void GetKey (Character key){
            try {
                KeyMap.get(key).run();
            }catch (NullPointerException e){
                System.out.printf("Binding for '%s' key has not found\n", key);
                Debug.log("Binding for " + key + " has not found");
            }
    }
}
