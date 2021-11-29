package com.rogurea.workers;

import com.rogurea.GameLoop;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.Colors;
import com.rogurea.view.ViewObjects;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AutoSaveLogWorker implements Runnable{

    private ObjectOutputStream outputLogStream;

    private FileOutputStream fileOutputStream;

    private File logFile;

    private final String FILE_EXTENSION = ".txt";

    private String fileName;

    private final File logDirectory = new File("logs/");

    @Override
    public void run() {

        this.fileName = ViewObjects.getTrimString(Dungeon.player.getPlayerData().getPlayerName()+"_"+getLocalTime()+FILE_EXTENSION);

        logDirectory.mkdir();

        logFile = new File(logDirectory+"/"+fileName);

        Debug.toLog("[AUTO_SAVE_LOG_WORKER] Starting...");

        Debug.toLog("[AUTO_SAVE_LOG_WORKER] File name to save: "+fileName);

        try{
            while(!Thread.currentThread().isInterrupted()){
                TimeUnit.SECONDS.sleep(3);
                saveLogToFile();
            }
        } catch (InterruptedException | FileNotFoundException e){
            if(Optional.ofNullable(e.getCause()).isPresent()) {
                Debug.toLog(Colors.RED_BRIGHT + "[ERROR] Error in autosave log worker:");
                e.printStackTrace();
            } else {
                try {
                    saveLogToFile();
                } catch (IOException | ConcurrentModificationException ex) {
                    Debug.toLog("[AUTO_SAVE_LOG_WORKER] Try to restart...");
                    run();
                }
            }
        } catch (IOException e) {
            Debug.toLog(Colors.RED_BRIGHT + "[ERROR] Error in autosave log worker:");
            e.printStackTrace();
        }
        Debug.toLog("[AUTO_SAVE_LOG_WORKER] Ended");
    }
    private void saveLogToFile() throws IOException {
        fileOutputStream = new FileOutputStream(logFile);

        ArrayList<String> bufferLog = Debug.getDebugLog();

        try {
            for (String string : bufferLog) {
                string += "\n";
                fileOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            Debug.toLog(Colors.RED_BRIGHT + "[ERROR] Error in autosave log worker:");
            e.printStackTrace();
        } finally {
            fileOutputStream.close();
        }

    }

    private String getLocalTime(){
        int year = java.time.Year.now().getValue();
        int month = java.time.LocalDate.now().getMonth().getValue();
        int day = LocalDate.now().getDayOfMonth();
        int hour = LocalTime.now().getHour();
        int minute = LocalTime.now().getMinute();
        int second = LocalTime.now().getSecond();
        return ""+year + month + day+"_"+ hour + minute + second;
    }
}
