package com.rogurea.base;

import com.rogurea.GameLoop;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AutoSaveLogWorker implements Runnable{

    private ObjectOutputStream outputLogStream;

    private FileOutputStream fileOutputStream;

    private File logFile;

    private final String FILE_EXTENSION = ".txt";

    private String fileName;

    private File logDirectory = new File("logs/");

    @Override
    public void run() {

        this.fileName = ViewObjects.getTrimString(Dungeon.player.getPlayerData().getPlayerName()+"_"+getLocalTime()+FILE_EXTENSION);

        logDirectory.mkdir();

        logFile = new File(logDirectory+"/"+fileName);

        Debug.toLog("[AUTO_SAVE_LOG_WORKER] Starting...");

        Debug.toLog("[AUTO_SAVE_LOG_WORKER] File name to save: "+fileName);

        try{
            while(!Thread.currentThread().isInterrupted()){
                TimeUnit.SECONDS.sleep(1);
                saveLogToFile();
            }
        } catch (InterruptedException | FileNotFoundException e){
            if(Optional.ofNullable(e.getCause()).isPresent() && !(e.getCause().equals(InterruptedException.class))) {
                Debug.toLog(Colors.RED_BRIGHT + "[ERROR] Error in autosave log worker:");
                e.printStackTrace();
            } else {
                try {
                    saveLogToFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveLogToFile() throws IOException {
        fileOutputStream = new FileOutputStream(logFile);

        for(String string : Debug.getDebugLog()){
            string += "\n";
            fileOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
        }

        fileOutputStream.flush();

        fileOutputStream.close();
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
