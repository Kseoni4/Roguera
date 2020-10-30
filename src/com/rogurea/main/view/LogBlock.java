package com.rogurea.main.view;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.resources.Colors;

import java.io.IOException;

public class LogBlock {

    public static int LogHistorySize = 11;

    static TextGraphics LoggerGraphics = null;

    public static String[] LogHistory = new String[LogHistorySize];

    public static int LogHistoryIndex = 0;

    static TerminalSize LogSize = new TerminalSize(256, LogHistory.length);

    static TerminalPosition topLoggerLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 4,12);

    private static StringBuilder OutMessage = new StringBuilder();

    public static void Init(){
        try {
            LoggerGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TerminalView.InitGraphics(LoggerGraphics, topLoggerLeft, LogSize);
    }

    public static void Action(String message){

        OutMessage.append('>').append(Colors.GREEN_BRIGHT).append("You").append(Colors.R).append(' ');

        OutMessage.append(message);

        WriteLog(OutMessage);
    }

    public static void Action(String message, Mob mob){

        OutMessage.append('>').append("Mob ").append(mob.Name).append(' ');

        OutMessage.append(message);

        WriteLog(OutMessage);
    }

    public static void Event(String message){

        OutMessage.append('>').append(message);

        WriteLog(OutMessage);
    }

    public static void LookAt(String message){
        OutMessage = new StringBuilder();
        OutMessage.append('>').append("You").append(' ');
    }

    public static void Clear(){
        ClearLog();
    }

    private static void WriteLog(StringBuilder sb){
        if(LogHistoryIndex < LogHistory.length) {
            sb.insert(0,"\u001b[38;5;n" + "m");
            LogHistory[LogHistoryIndex] = sb.toString();
            LogHistoryIndex++;
        }
        else {
            ClearLog();
            sb.insert(0,"\u001b[38;5;255m");
            LogHistory[LogHistoryIndex] = sb.toString();
        }
        OutMessage = new StringBuilder();
    }

    static void RedrawLog(){
        DrawLogBorders();
        if(LogHistory[0] != null) {
            int i = 0;
            for (String s : LogHistory) {
                if (s != null) {

                    s = s.replaceFirst("(\\[38;5;nm)", "[38;5;" + (245+(LogHistory.length-(LogHistoryIndex*2))+i) + "m");
                    TerminalView.DrawBlockInTerminal(LoggerGraphics, s, topLoggerLeft.withRelative(0,i));
                    i++;
                }
                else
                    break;
            }
        }
    }
    static void DrawLogBorders(){
        TerminalView.DrawBlockInTerminal(LoggerGraphics, "Log", topLoggerLeft.withRow(10));
        LoggerGraphics.drawLine(
                topLoggerLeft.withRow(11),
                new TerminalPosition(topLoggerLeft.getColumn()+20,topLoggerLeft.getRow()-1),
                Symbols.SINGLE_LINE_HORIZONTAL);
    }

    public static void ClearLog(){
        LogHistory = new String[LogHistorySize];
        LogHistoryIndex = 0;
    }

    public static void Reset(){
        topLoggerLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 4,12);
    }
}
