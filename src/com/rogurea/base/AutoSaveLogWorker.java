package com.rogurea.base;

import com.rogurea.gamemap.Dungeon;
import com.rogurea.view.ViewObjects;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AutoSaveLogWorker implements Runnable{

    private ObjectOutputStream outputLogStream;

    private FileOutputStream fileOutputStream;

    private File logFile;

    private final String FILE_EXTENSION = ".txt";

    private String fileName;

    @Override
    public void run() {
        this.fileName = ViewObjects.getTrimString(Dungeon.player.getPlayerData().getPlayerName()+"_"+Dungeon.player.getPlayerData().hashCode()+FILE_EXTENSION);

        logFile = new File(fileName);

        Debug.toLog("[AUTO_SAVE_LOG_WORKER] Starting...");

        Debug.toLog("[AUTO_SAVE_LOG_WORKER] File name to save: "+fileName);

        try{
            while(!Thread.currentThread().isInterrupted()){
                TimeUnit.SECONDS.sleep(1);
                saveLogToFile();
            }
        } catch (InterruptedException | FileNotFoundException e){
            Debug.toLog("[ERROR] Error in autosave log worker "+ Arrays.toString(e.getStackTrace()));
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
}
