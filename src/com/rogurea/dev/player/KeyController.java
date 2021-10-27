/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;
import com.rogurea.dev.base.Debug;
import com.rogurea.dev.resources.GameResources;

import java.util.HashMap;
import java.util.Optional;

public class KeyController {

    private static final HashMap<Character, Runnable> KEY_MAP = GameResources.getKeyMap();

    public static void getKey(Character key){
        Optional<Runnable> keyAction = Optional.ofNullable(KEY_MAP.get(key));
        keyAction.ifPresent(Runnable::run);
    }
}
