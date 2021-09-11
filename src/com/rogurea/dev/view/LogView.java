package com.rogurea.dev.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.resources.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LogView implements IViewBlock {

    private TextGraphics logTextGraphics;

    private ArrayList<String> logHistory;

    private ArrayList<String> assembleStrings = new ArrayList<>();

    private StringBuilder logMessage;

    public Position logPosition;

    private String colorFade = "";

    private int logHeightsize;

    private int logWightSize;

    private int logLineIndex;

    public LogView(){}

    public void setLogHeightsize(int heightSize){
        logHeightsize = heightSize;
    }

    public void setLogWightSize(int wightSize) { logWightSize = wightSize;}

    @Override
    public void Init() {
        try{
            logTextGraphics = TerminalView.terminal.newTextGraphics();

            logLineIndex = 0;

            if(logMessage == null) {
                logMessage = new StringBuilder();
            }
            if(logHistory == null) {
                logHistory = new ArrayList<>();
            } else {
                logHistory = new ArrayList<>(assembleStrings);
                logLineIndex = logHistory.size();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void action(String message){
        putLog(message);
    }

    public void playerActionSee(String message){
        String prefixSee = Colors.GREEN_BRIGHT.concat("You").concat(Colors.R).concat(" see the ");
        putLog(prefixSee.concat(message));
    }

    public void putLog(String message){
        if(logLineIndex > logHeightsize){
            logLineIndex = 0;
            logHistory.clear();
            assembleStrings.clear();
            Draw.reset(this);
        }
        logMessage.append(message);
        assembleStrings.add(assembleStrings.size()+logMessage.toString());
        Draw.call(this);
    }

    @Override
    public void Draw() {
        if(logMessage.toString().equals("")){
            redrawHistory();
            return;
        }
        if(logHistory.size() >= 1){
            redrawHistory();
        }
        writeLog(logMessage.toString(), logLineIndex);
        incResetLog();
    }

    private void redrawHistory() {
        clearLog();
        ArrayList<String> logHistory_buffer = new ArrayList<>(logHistory);
        for (String log : logHistory_buffer) {
            //colorFadeDecrement(log);
            int substr = logHistory_buffer.indexOf(log) < 10 ? 1 : 2;
            System.out.println("write log (index): (" + logHistory_buffer.indexOf(log) + ")" + log);
            writeLog(colorFade.concat(log.substring(substr)), logHistory_buffer.indexOf(log));
        }
    }

    private Boolean checkMessageLength(String message){

        return getTrimString(message).length() > logWightSize;
    }

    private String getTrimString(String message){
        String trimmedMessage = message.trim();
        return trimmedMessage.replaceFirst("\\[\\d{2};\\d{1};\\d{2}m", "");
    }

    private void splitMessage(String message){
        String[] longMessage = message.split(" ");

        int i = 0;

        System.out.println("remove "+getTrimString(message)+"("+logHistory.removeIf(s -> s.endsWith(getTrimString(message))));
        if(logLineIndex > 1)
            logLineIndex--;


        logMessage = new StringBuilder();


        recurrentPrint(longMessage, i);
    }

    private void recurrentPrint(String[] longMessage, int startIndex) {
        int i = startIndex;
        if (i < longMessage.length) {
            try {
                do {
                    logMessage.append(longMessage[i]).append(" ");
                    i++;
                } while ((logMessage + longMessage[i]).length() < logWightSize);
            } catch (ArrayIndexOutOfBoundsException ignored) { }
            finally {
                writeLog(logMessage.toString(), logLineIndex);
                incResetLog();
            }
            recurrentPrint(longMessage, i);
        }
    }

    private void incResetLog(){

        logHistory.add(logLineIndex+logMessage.toString());


        logLineIndex++;


        logMessage = new StringBuilder();
    }

    private void clearLog(){
        logTextGraphics.fillRectangle(new TerminalPosition(logPosition.x, logPosition.y), new TerminalSize(logWightSize, logHeightsize+1), ' ');
    }

    private void colorFadeDecrement(String log){
        colorFade = "\u001b[38;5;_fade_m";
        colorFade = colorFade.replaceFirst("_fade_", String.valueOf(Math.max(
                232, 232 + (10 - (logHistory.size() - logHistory.indexOf(log)))
        )));
    }

    private void writeLog(String msg, int line){
        if(checkMessageLength(msg))
        {
            splitMessage(msg);
        }
        else{
            TerminalView.drawBlockInTerminal(logTextGraphics, msg, logPosition.x, logPosition.y+line);
        }
/*        else {
            writeLog(logMessage.toString(), logLineIndex);

            incResetLog();
        }*/
    }

    @Override
    public void Reset() {
        clearLog();
        Draw.flush();
    }
}