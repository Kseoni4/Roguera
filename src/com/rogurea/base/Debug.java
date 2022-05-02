/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.base;

import com.rogurea.resources.Colors;
import com.rogurea.view.ViewObjects;

import java.util.ArrayList;
import java.util.Calendar;

import static com.rogurea.Roguera.isDebug;

public class Debug {

    private static final ArrayList<String> debugLog = new ArrayList<>();

    private static final ArrayList<String> debugEventLog = new ArrayList<>();

    public static void toLog(String message){
        if(isDebug){
            String time = ""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"
                    +Calendar.getInstance().get(Calendar.MINUTE)+":"
                    +Calendar.getInstance().get(Calendar.SECOND)+":"
                    +Calendar.getInstance().get(Calendar.MILLISECOND);
            if(message.startsWith("[EVENT]")){
                debugEventLog.add("["+time+"]"+ViewObjects.getTrimString(message));
            } else {
                debugLog.add("[" + time + "]" + ViewObjects.getTrimString(message));
            }
            System.out.println("["+time+"]"+message+Colors.R);
        }
    }

    public static ArrayList<String> getDebugLog(){
        return debugLog;
    }

    public static ArrayList<String> getDebugEventLog(){
        return debugEventLog;
    }
}
