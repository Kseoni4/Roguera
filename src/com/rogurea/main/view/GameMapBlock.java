package com.rogurea.main.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

import java.io.IOException;

public class GameMapBlock {

    static TextGraphics MapDrawGraphics = null;

    public static void Init(){

        try {
            MapDrawGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void DrawDungeon() throws IOException {
        char cell = MapEditor.EmptyCell;
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Dungeon.CurrentRoom.length; i++) {
            for (int j = 0; j < Dungeon.CurrentRoom[0].length; j++) {
                cell = Dungeon.ShowDungeon(i, j).charAt(0);
                if (cell == Player.PlayerModel) {
                    out.append(Colors.GREEN_BRIGHT);
                } else if (Scans.CheckCreature(cell)) {
                    out.append(Colors.RED_BRIGHT);
                } else if (!Scans.CheckWall(cell)){
                    out.append(Colors.R);
                }
                else{
                    out.append(Colors.ORANGE);
                }
                out.append(Dungeon.ShowDungeon(i, j));
                TerminalView.DrawBlockInTerminal(MapDrawGraphics, out.toString(), j, i);
                out = new StringBuilder();
            }
        }
    }
}
