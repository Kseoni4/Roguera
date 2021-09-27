/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;
import com.rogurea.dev.base.Debug;
import com.rogurea.dev.resources.GameResources;

import java.util.HashMap;

public class KeyController {

    private static final HashMap<Character, Runnable> KEY_MAP = GameResources.getKeyMap();

    public static void getKey(Character key){
            try {
               KEY_MAP.get(key).run();
            }catch (NullPointerException e){
                System.out.printf("Binding for '%s' key has not found\n", key);
                Debug.toLog("Binding for " + key + " has not found");
            }
    }
}
