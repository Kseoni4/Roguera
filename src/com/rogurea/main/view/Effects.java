package com.rogurea.main.view;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.player.Player;

import java.io.IOException;

public class Effects {

    private static TextGraphics Effects = null;

    public static void Init(){
        try {
            Effects = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void PlayerHitEffect(){
        TerminalView.PutCharInTerminal(Effects,new TextCharacter(Player.PlayerModel).withBackgroundColor(TextColor.ANSI.RED_BRIGHT),Player.GetPlayerPosition().x, Player.GetPlayerPosition().y);
    }

    public static void MobHitEffect(Mob mob){
        TerminalView.PutCharInTerminal(Effects,new TextCharacter(mob.MobSymbol).withBackgroundColor(TextColor.ANSI.RED_BRIGHT),mob.HisPosition.x, mob.HisPosition.y);
    }
}
