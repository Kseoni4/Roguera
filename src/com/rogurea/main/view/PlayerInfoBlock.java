package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.items.Armor;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class PlayerInfoBlock {

    static TextGraphics PlayerInfoGraphics = null;

    static TerminalPosition topPlayerInfoLeft;

    static TerminalSize PlayerInfoSize = new TerminalSize(GameResources.getPlayerPositionInfo().length() + 2, 5);

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
                        + " (" + Objects.requireNonNull(Dungeon.Rooms.stream()
                                .filter(room -> room.NumberOfRoom == Player.CurrentRoom)
                                .findAny().orElse(null)).roomSize +
                                ")",
                topPlayerInfoLeft.withRelative(2,2));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.PlayerName,
                topPlayerInfoLeft.withRelative(2,3));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,
                GameResources.UpdatePlayerInfo(),
                topPlayerInfoLeft.withRelative(2,4));

        TerminalView.DrawBlockInTerminal(PlayerInfoGraphics,getEquipmentInfo()
                ,
                topPlayerInfoLeft.withRelative(2,5));

    }

    public static String getEquipmentInfo(){

        StringBuilder sb = new StringBuilder();

        sb.append("Equip: ");

        for(String s : Player.Equip.keySet()){
           if(Player.Equip.get(s) == null)
               continue;
           sb.append(s).append(": ").append(Player.Equip.get(s) != null ?
                    Colors.ORANGE + Player.Equip.get(s)._model
                    : "none").append(Colors.R).append(" ");
        }
        if(sb.length() < 10)
            sb.append("none");

        return sb.toString();
    }

    public static void Reset(){
        topPlayerInfoLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length + 1,1);
    }
}
