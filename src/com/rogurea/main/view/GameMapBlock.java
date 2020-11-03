package com.rogurea.main.view;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;

import java.io.IOException;

public class GameMapBlock {

    private static TextGraphics MapDrawGraphics = null;

    public static void Init(){

        try {
            MapDrawGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void DrawDungeon(){
        int yLenght = Dungeon.CurrentRoom.length;
        int xLenght = Dungeon.CurrentRoom[0].length;

        /*StringBuffer out = new StringBuffer();*/
        TextCharacter data = new TextCharacter(' ');

        for (int i = 0; i < yLenght; i++) {
            for (int j = 0; j < xLenght; j++) {
                char cell = Dungeon.CurrentRoom[i][j];
                if (cell == Player.PlayerModel) {
                    data = data.withForegroundColor(Colors.GetTextColor(Colors.GREEN_BRIGHT,"\u001b[38;5;"));
                    /*out.append(Colors.GREEN_BRIGHT);*/
                } else if (Scans.CheckCreature(cell)) {
                    data = data.withForegroundColor(Colors.GetTextColor(Colors.RED_BRIGHT,"\u001b[38;5;"));
                    /*out.append(Colors.RED_BRIGHT);*/
                } else if (!Scans.CheckWall(cell)){
                    data = data.withForegroundColor(TextColor.ANSI.DEFAULT);
                    /*out.append(Colors.R);*/
                } else if(Scans.CheckItems(cell)) {
                    data = data.withForegroundColor(Colors.GetTextColor(getItemColor(cell),"\u001b[38;5;"));
                    /*out.append(getItemColor(cell));*/
                }
                else{
                 data = data.withForegroundColor(Colors.GetTextColor(Colors.ORANGE,"\u001b[38;5;"));
                    /*out.append(Colors.ORANGE);*/
                }
                /*out.append(Dungeon.CurrentRoom[i][j]);*/
                data = data.withCharacter(Dungeon.CurrentRoom[i][j]);

                TerminalView.PutCharInTerminal(MapDrawGraphics, data, j, i);
                /*TerminalView.DrawBlockInTerminal(MapDrawGraphics, out.toString(), j, i);*/
                /*out = new StringBuffer();*/
            }
        }
    }

    private static String getItemColor(char cell){

        Item item_ = Dungeon.GetCurrentRoom().RoomItems.stream().filter(
                item -> item._model == cell
        ).findAny().orElse(null);
        if(item_ != null)
            return (item_.getMaterialColor());
        return "?Color?";
    }
}
