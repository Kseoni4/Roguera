/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.base;

import com.rogurea.resources.Colors;

import java.util.Calendar;

import static com.rogurea.Roguera.isDebug;

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
