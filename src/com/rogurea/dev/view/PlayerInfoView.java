package com.rogurea.dev.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;

import java.io.IOException;

public class PlayerInfoView implements IViewBlock {

    private TextGraphics PlayerInfoViewGraphics = null;

    public Position infoPosition;

    public PlayerInfoView(){

    }

    @Override
    public void Init() {
        try{
            PlayerInfoViewGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Draw() {
        int i = 0;
        for(String info : Dungeon.player.getPlayerData().formPlayerData()) {
            TerminalView.drawBlockInTerminal(PlayerInfoViewGraphics, info, infoPosition.x, infoPosition.y+i);
            i++;
        }
    }

    @Override
    public void Reset() {

    }
}
