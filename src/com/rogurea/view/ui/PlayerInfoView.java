/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.view.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.view.Draw;
import com.rogurea.view.IViewBlock;
import com.rogurea.view.TerminalView;

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
        Draw.reset(this);
        int i = 0;
        for(String info : Dungeon.player.getPlayerData().formPlayerData()) {
            TerminalView.drawBlockInTerminal(PlayerInfoViewGraphics, info, infoPosition.x, infoPosition.y+i);
            i++;
        }
    }


    @Override
    public void Reset() {
        PlayerInfoViewGraphics.fillRectangle(new TerminalPosition(infoPosition.x, infoPosition.y), new TerminalSize(20,10), ' ');
    }
}
