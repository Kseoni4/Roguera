/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.view.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.IViewBlock;
import com.rogurea.dev.view.TerminalView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.rogurea.dev.view.ViewObjects.infoGrid;

public class LogView implements IViewBlock {

    private final String PREFIX_YOU = Colors.GREEN_BRIGHT.concat("You").concat(Colors.R);
    private final String SEE_THE = " see the ";
    private final String GET_THE = " get the ";

    private TextGraphics logTextGraphics;

    private ArrayList<String> logHistory;

    private ArrayList<String> assembleStrings = new ArrayList<>();

    private StringBuilder logMessage;

    public Position logPosition;

    private String colorFade = "";

    private int logHeightsize;

    private final int LOG_STRINGS_LIMIT = infoGrid.get_pointYX().y-1;

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

    public void playerAction(String message){
        putLog(PREFIX_YOU.concat(" "+message));
    }

    public void playerActionSee(String message){
        String prefixSee = PREFIX_YOU.concat(SEE_THE);
        putLog(prefixSee.concat(message));
    }

    public void playerActionPickUp(String message){
        String prefixPickUp = PREFIX_YOU.concat(GET_THE);
        putLog(prefixPickUp.concat(message).concat("!"));
    }

    public void putLog(String message){
        if(logLineIndex >= LOG_STRINGS_LIMIT){
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
        Reset();
        ArrayList<String> logHistory_buffer = new ArrayList<>(logHistory);
        for (String log : logHistory_buffer) {
            //colorFadeDecrement(log);
            int substr = logHistory_buffer.indexOf(log) < 10 ? 1 : 2;
            //Debug.toLog("write log (index): (" + logHistory_buffer.indexOf(log) + ")" + log);
            writeLog(colorFade.concat(log.substring(substr)), logHistory_buffer.indexOf(log));
        }
    }

    private Boolean checkMessageLength(String message){

        return getTrimString(message).length() > logWightSize;
    }

    private String getTrimString(String message){
        String trimmedMessage = message.trim();
        int len = trimmedMessage.split(" ").length;
        for(int i = 0; i <= len; i++) {
            trimmedMessage = trimmedMessage.replaceFirst("\\[\\d{2};\\d{1};((\\d{3}m)|(\\d{2}m)|(\\d{1}m))", "");
            trimmedMessage = trimmedMessage.replaceFirst("(\\u001b|\\u001b[ESC]\\[(\\d{1}m))", "");
            trimmedMessage = trimmedMessage.replaceFirst("(\\[(\\d{1}m))", "");
            trimmedMessage = trimmedMessage.replaceFirst("\\[\\u001b|\\u001b[ESC]", "");
        }
        return trimmedMessage;
    }

    private void splitMessage(String message){
        String[] longMessage = message.split(" ");

        int i = 0;

        //Debug.toLog("remove "+getTrimString(message)+"("+logHistory.removeIf(s -> s.endsWith(getTrimString(message)))+")");
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
                } while ((getTrimString(logMessage + longMessage[i])).length() < logWightSize);
            } catch (ArrayIndexOutOfBoundsException ignored) { }
            finally {
                writeLog(logMessage.toString(), logLineIndex);
                incResetLog();
            }
            if(i < 30)
                recurrentPrint(longMessage, i);
        }
    }

    private void incResetLog(){
        if(!logMessage.toString().equals("")) {
            logHistory.add(logLineIndex + logMessage.toString());

            logLineIndex++;

            logMessage = new StringBuilder();
        }
    }

    private void writeLog(String msg, int line){
        if(checkMessageLength(msg))
        {
            splitMessage(msg);
        }
        else{
            TerminalView.drawBlockInTerminal(logTextGraphics, msg, logPosition.x, logPosition.y+line);
        }
    }

    private void colorFadeDecrement(String log){
        colorFade = "\u001b[38;5;_fade_m";
        colorFade = colorFade.replaceFirst("_fade_", String.valueOf(Math.max(
                232, 232 + (10 - (logHistory.size() - logHistory.indexOf(log)))
        )));
    }

    @Override
    public void Reset() {
        clearLog();
        Draw.flush();
    }

    private void clearLog(){
        logTextGraphics.fillRectangle(new TerminalPosition(logPosition.x, logPosition.y), new TerminalSize(logWightSize, infoGrid.get_pointYX().y-1), ' ');
    }
}
