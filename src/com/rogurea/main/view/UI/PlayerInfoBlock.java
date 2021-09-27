/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.view.UI;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.ViewObjects;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;

public class PlayerInfoBlock implements IViewBlock {

    private TextGraphics PlayerInfoGraphics = null;

    private TerminalPosition topPlayerInfoLeft;

    private final TerminalSize PlayerInfoSize = new TerminalSize(GameResources.getPlayerPositionInfo().length() + 2, 5);

    public static BaseGenerate.RoomSize roomSize;

    public PlayerInfoBlock(){
        ViewObjects.ViewBlocks.add(this);
    }

    public void Init(){

        try {
            PlayerInfoGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        topPlayerInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,1);

        TerminalView.InitGraphics(PlayerInfoGraphics, topPlayerInfoLeft, PlayerInfoSize);
    }

    public void Draw() {

        TerminalSize terminalSize = null;

        try {
            terminalSize = TerminalView.terminal.getTerminalSize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TerminalView.InitGraphics(PlayerInfoGraphics, topPlayerInfoLeft, PlayerInfoSize);

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics, GameResources.getPlayerPositionInfo().toString(),
                        30, terminalSize.getRows()-2);

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                "Room size: " + Dungeon.CurrentRoom.length + "x" + Dungeon.CurrentRoom[0].length
                        + " (" + roomSize + ")",
                55, terminalSize.getRows()-2);

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.PlayerName,
                topPlayerInfoLeft.withRelative(2,1));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.UpdatePlayerHPMP().toString(),
                topPlayerInfoLeft.withRelative(2,2));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.UpdatePlayerATKDEFDEX().toString(),
                topPlayerInfoLeft.withRelative(2,3));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.UpdatePlayerMoneyXP().toString(),
                topPlayerInfoLeft.withRelative(2,4));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.UpdatePlayerLvlRoom().toString(),
                topPlayerInfoLeft.withRelative(2,5));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,getEquipmentInfo().toString(),
                topPlayerInfoLeft.withRelative(2,6));

    }

    private StringBuilder getEquipmentInfo(){

        StringBuilder sb = new StringBuilder();

        sb.append("Equip: ");

        for(String s : Player.Equip.keySet()){
           if(Player.Equip.get(s) == null)
               continue;

           sb.append(s).append(": ").append(
                   Player.Equip.get(s) != null ?
                             Player.Equip.get(s).getMaterialColor() + Player.Equip.get(s)._model
                   : "none").append(Colors.R).append(" ");
        }
        if(sb.length() < 10)
            sb.append("none");

        return sb;
    }

    public void Reset(){
        topPlayerInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,1);
        TerminalView.InitGraphics(PlayerInfoGraphics, topPlayerInfoLeft, PlayerInfoSize);
    }
}
