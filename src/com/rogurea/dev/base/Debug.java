/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.base;

import static com.rogurea.devMain.isDebug;

public class Debug {
    public static void toLog(String message){
        if(isDebug){
            System.out.println(message);
        }
    }
}
