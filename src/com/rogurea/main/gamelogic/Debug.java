package com.rogurea.main.gamelogic;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Debug {

    private static final ArrayList<StringBuilder> logHistory = new ArrayList<>();
    private static File LogFile = null;

    public static TextGraphics DebugTG = null;
    public static Terminal DebugTerminal = null;

    public static void InitDebugWindow(){

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

        defaultTerminalFactory.setTerminalEmulatorTitle("Roguera " + GameResources.version);

        defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(GameResources.TerminalFont));

        defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(1000,1000));

        try {
            DebugTerminal = defaultTerminalFactory.createTerminal();

            DebugTG = DebugTerminal.newTextGraphics();

            DebugTerminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log("Debug window has been initialized");
    }

    public static int row = 0;
    public static int col = 0;

    public static void TestFontUnicode(){
        for(int i = 0; i < 10000; i++){
            try {
                System.out.println("row " + row);
                DebugTG.putString((i%500 == 1 ? col=0 : col++), (i%500 == 1 ? row++ : row), String.valueOf(Character.toChars(i)));
            }
            catch (IllegalArgumentException e){
                continue;
            }
        }
        try {
            DebugTerminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message){
        StringBuilder sb = new StringBuilder().append("[").append(Calendar.getInstance().getTime()).append("]//");

        message = message.replaceAll("\u001B","");
        message = message.replaceAll("\\[+\\d{2}\\;+\\d\\;+\\d*m+", "");
        message = message.replaceAll("\\[+[0]m+","");

        sb.append(message).append('\n');
        logHistory.add(sb);
    }

    public static void SaveLogToFile() throws FileNotFoundException {

        LogFile = new File(Player.nickName+"_log.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(LogFile);

        Debug.log("SYSTEM: Autosave success");

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
            log(Arrays.toString(e.getStackTrace()));
        }
    }
    public static class AutoSaveLog extends Thread{
        @Override
        public void run() {
            log("THREAD: AutoSave thread has been started");

            int CurrentLogSize = 0;

            while(!isInterrupted()) {
                try {
                    sleep(3000);
                    if(logHistory.size() > CurrentLogSize) {
                        log("SYSTEM: Autosave log");

                        SaveLogToFile();

                        CurrentLogSize = logHistory.size();

                    }
                } catch (IOException | InterruptedException e) {
                    log("THREAD: AutoSave thread is interrupted");
                    break;
                }
            }
            log("THREAD: AutoSave thread has been ended");
        }
    }
}
