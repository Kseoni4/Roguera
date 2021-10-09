/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.base;

import com.rogurea.dev.resources.Colors;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.rogurea.devMain.isDebug;

public class Debug {
    public static void toLog(String message){
        if(isDebug){
            String time = ""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"
                    +Calendar.getInstance().get(Calendar.MINUTE)+":"
                    +Calendar.getInstance().get(Calendar.SECOND)+":"
                    +Calendar.getInstance().get(Calendar.MILLISECOND);
            System.out.println("["+time+"]"+message+Colors.R);
        }
    }
}
