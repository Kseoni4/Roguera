package com.rogurea.research;

import java.io.IOException;
import java.util.Objects;

import com.googlecode.lanterna.input.KeyType;


import static com.rogurea.research.R_Player.Player;

public class R_MoveController {

    public static void MovePlayer(KeyType key) throws IOException {
        switch (key) {
            case ArrowUp -> Move(R_Player.Pos.y + 1, R_Player.Pos.x);
            case ArrowLeft -> Move(R_Player.Pos.y, R_Player.Pos.x-1);
            case ArrowDown -> Move(R_Player.Pos.y - 1, R_Player.Pos.x);
            case ArrowRight -> Move(R_Player.Pos.y, R_Player.Pos.x + 1);
        }
    }
    public static void Move(int y, int x) throws IOException {
        if(Scans.CheckWall(R_Dungeon.CurrentRoom[y][x])
                && !T_View.CheckCreature(R_Dungeon.CurrentRoom[y][x])){

            if(Scans.CheckExit(R_Dungeon.CurrentRoom[y][x])){

                R_Dungeon.CurrentRoom[R_Player.Pos.y][R_Player.Pos.x] = MapEditor.EmptyCell;
                R_GameLoop.ChangeRoom(
                        Objects.requireNonNull(R_Generate.GetRoom(R_Dungeon.Direction.NEXT)).nextRoom
                );
            }
            else if(Scans.CheckBack(R_Dungeon.CurrentRoom[y][x]))
                {
                    R_Player.CurrentRoom--;

                    R_Dungeon.CurrentRoom[R_Player.Pos.y][R_Player.Pos.x] = MapEditor.EmptyCell;

                    R_GameLoop.ChangeRoom(
                            Objects.requireNonNull(R_Generate.GetRoom(R_Dungeon.Direction.BACK))
                    );
                }
            else
                {
                    R_Dungeon.CurrentRoom[R_Player.Pos.y][R_Player.Pos.x] = MapEditor.EmptyCell;
                    R_Dungeon.CurrentRoom[y][x] = Player;
                    R_Player.Pos.x = x;
                    R_Player.Pos.y = y;
                }
            }
        }
//        if(R_Generate.CheckExit(R_Generate.CurrentRoom[x][y])) {
//            T_View.NextRoomWindow(T_View.terminal);
//        }
    }
