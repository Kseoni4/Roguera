package com.rogurea.main.view.viewblocks;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;

public class LogBlock implements IViewBlock {

    private final int LogHistorySize = 11;

    private TextGraphics LoggerGraphics = null;

    private String[] LogHistory = new String[LogHistorySize];

    private int LogHistoryIndex = 0;

    private final TerminalSize LogSize = new TerminalSize(270, LogHistory.length);

    private TerminalPosition topLoggerLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 4,12);

    private StringBuilder OutMessage = new StringBuilder();

    private final TerminalPosition LogBorderSize = new TerminalPosition(topLoggerLeft.getColumn()+30,topLoggerLeft.getRow()-1);

    public void Init(){
        try {
            LoggerGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TerminalView.InitGraphics(LoggerGraphics, topLoggerLeft, LogSize);
    }

    public void Action(String message){

        OutMessage.append('>').append(Colors.GREEN_BRIGHT).append("You").append(Colors.R).append(' ');

        OutMessage.append(message);

        WriteLog(OutMessage);
    }

    public void Action(String message, Mob mob){

        OutMessage.append('>').append("Mob ").append(mob.Name).append(' ');

        OutMessage.append(message);

        WriteLog(OutMessage);
    }

    public void Event(String message){

        OutMessage.append('>').append(message);

        WriteLog(OutMessage);
    }

/*    public void LookAt(String message){
        OutMessage = new StringBuffer();
        OutMessage.append('>').append("You").append(' ');
    }*/

    public void Clear(){
        ClearLog();
    }

    private void WriteLog(StringBuilder sb){
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
        Draw.call(this);
        OutMessage = new StringBuilder();
    }

    public void Draw(){
        DrawLogBorders();
        if(LogHistory[0] != null) {
            TerminalView.InitGraphics(LoggerGraphics, topLoggerLeft, LogSize);
            int i = 0;
            for (String s : LogHistory) {
                if (s != null) {
                    s = s.replaceFirst("(\\[38;5;nm)", "[38;5;" + Math.min(245+(LogHistory.length-(LogHistoryIndex*2))+i,255) + "m");
                    TerminalView.DrawBlockInTerminal(LoggerGraphics, s, topLoggerLeft.withRelative(0,i));
                    i++;
                }
                else
                    break;
            }
        }
    }
    private void DrawLogBorders(){
        TerminalView.DrawBlockInTerminal(LoggerGraphics, "Log", topLoggerLeft.withRow(10));
        LoggerGraphics.drawLine(
                topLoggerLeft.withRow(11), LogBorderSize,
                Symbols.SINGLE_LINE_HORIZONTAL);
    }

    private void ClearLog(){
        LogHistory = new String[LogHistorySize];
        LogHistoryIndex = 0;
        Draw.reset(this);
    }

    public void Reset(){
        TerminalView.InitGraphics(LoggerGraphics, topLoggerLeft, LogSize);
        topLoggerLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 4,12);
        DrawLogBorders();
    }
}
