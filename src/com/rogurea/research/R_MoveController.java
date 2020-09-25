package com.rogurea.research;

import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

import static com.rogurea.research.R_Player.Player;

public class R_MoveController {

    public static void MovePlayer(KeyStroke key) throws IOException {

        switch (key.getCharacter()) {
            case 'w' -> Move(R_Player.Pos.x, R_Player.Pos.y + 1);
            case 'a' -> Move(R_Player.Pos.x - 1, R_Player.Pos.y);
            case 's' -> Move(R_Player.Pos.x, R_Player.Pos.y - 1);
            case 'd' -> Move(R_Player.Pos.x + 1, R_Player.Pos.y);
        }
    }
    public static void Move(int x, int y) throws IOException {
        if(R_Dungeon.CheckWall(R_Dungeon.CurrentRoom[x][y])){
                R_Dungeon.CurrentRoom[R_Player.Pos.x][R_Player.Pos.y] = ' ';
                R_Dungeon.CurrentRoom[x][y] = Player;
                R_Player.Pos.x = x;
                R_Player.Pos.y = y;
            }
        if(R_Dungeon.CheckExit(R_Dungeon.CurrentRoom[x][y])) {
            T_View.NextRoomWindow(T_View.terminal);
        }
    }
}
