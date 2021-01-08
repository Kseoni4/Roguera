package com.rogurea.main.gamelogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Debug {

    private static final ArrayList<StringBuilder> logHistory = new ArrayList<>();
    private static final File LogFile = new File(Calendar.getInstance().getTime() + "_logHistory.txt");


    public static void log(String message){
        StringBuilder sb = new StringBuilder().append("[").append(Calendar.getInstance().getTime()).append("]//");

        message = message.replaceAll("\u001B","");
        message = message.replaceAll("\\[+\\d{2}\\;+\\d\\;+\\d*m+", "");
        message = message.replaceAll("\\[+[0]m+","");

        sb.append(message).append('\n');
        logHistory.add(sb);
    }

    public static void SaveLogToFile() throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(LogFile);

        for(StringBuilder sb : logHistory){
            try {
                fileOutputStream.write(sb.toString().getBytes());
            } catch (IOException e) {
                System.out.println("Write log history was failed");
            }
        }

        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static class AutoSaveLog extends Thread{
        @Override
        public void run() {
            Debug.log("THREAD: AutoSave thread has been started");

            int CurrentLogSize = 0;

            while(!isInterrupted()) {
                try {
                    sleep(3000);
                    if(Debug.logHistory.size() > CurrentLogSize) {
                        Debug.log("SYSTEM: Autosave log");

                        Debug.SaveLogToFile();

                        CurrentLogSize = Debug.logHistory.size();
                    }
                } catch (InterruptedException | FileNotFoundException e) {
                    Debug.log("THREAD: AutoSave thread is interrupted");
                    break;
                }
            }
            Debug.log("THREAD: AutoSave thread has been ended");
        }
    }
}
