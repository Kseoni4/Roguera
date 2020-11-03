package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

import java.io.IOException;
import java.util.Objects;

public class PlayerInfoBlock {

    static TextGraphics PlayerInfoGraphics = null;

    static TerminalPosition topPlayerInfoLeft;

    static final TerminalSize PlayerInfoSize = new TerminalSize(GameResources.getPlayerPositionInfo().length() + 2, 5);

    public static BaseGenerate.RoomSize roomSize;


    public static void Init(){

        try {
            PlayerInfoGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        topPlayerInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,1);

        TerminalView.InitGraphics(PlayerInfoGraphics, topPlayerInfoLeft, PlayerInfoSize);
    }

    public static void GetInfo(){

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics, GameResources.getPlayerPositionInfo(),
                topPlayerInfoLeft.withRelative(2,1));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                "Room size: " + Dungeon.CurrentRoom.length + "x" + Dungeon.CurrentRoom[0].length
                        + " (" + roomSize + ")",
                topPlayerInfoLeft.withRelative(2,2));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.PlayerName,
                topPlayerInfoLeft.withRelative(2,3));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.UpdatePlayerInfo().toString(),
                topPlayerInfoLeft.withRelative(2,4));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,getEquipmentInfo().toString(),
                topPlayerInfoLeft.withRelative(2,5));

    }

    public static StringBuilder getEquipmentInfo(){

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

    public static void Reset(){
        topPlayerInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,1);
    }
}
