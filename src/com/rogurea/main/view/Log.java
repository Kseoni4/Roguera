package com.rogurea.main.view;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.player.Player;

public class Log {

    private static StringBuilder OutMessage = new StringBuilder();

    public static void Action(String message){
        OutMessage = new StringBuilder();

        OutMessage.append('>').append("You").append(' ');

        OutMessage.append(message);

        PutOnDisplay(OutMessage);
    }

    public static void Action(String message, Mob mob){
        OutMessage = new StringBuilder();

        OutMessage.append('>').append("Mob ").append(mob.Name).append(' ');

        OutMessage.append(message);

        PutOnDisplay(OutMessage);
    }

    public static void LookAt(String message){
        OutMessage = new StringBuilder();
        OutMessage.append('>').append("You").append(' ');
    }

    public static void Clear(){
        TerminalView.ClearLog();
    }

    private static void PutOnDisplay(StringBuilder sb){
        TerminalView.WriteLog(sb);
    }
}
