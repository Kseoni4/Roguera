package com.rogurea.research;

import java.io.IOException;
import java.util.Objects;

import com.googlecode.lanterna.input.KeyType;


import static com.rogurea.research.R_Player.Player;

public class R_MoveController {

    public static void MovePlayer(KeyType key) throws IOException {
        switch (key) {
            case ArrowUp -> Move(R_Player.Pos.x, R_Player.Pos.y + 1);
            case ArrowLeft -> Move(R_Player.Pos.x - 1, R_Player.Pos.y);
            case ArrowDown -> Move(R_Player.Pos.x, R_Player.Pos.y - 1);
            case ArrowRight -> Move(R_Player.Pos.x + 1, R_Player.Pos.y);
        }
    }
    public static void Move(int x, int y) throws IOException {
        if(R_Generate.CheckWall(R_Dungeon.CurrentRoom[x][y])
                && !T_View.CheckCreature(R_Dungeon.CurrentRoom[x][y])){
            if(R_Generate.CheckExit(R_Dungeon.CurrentRoom[x][y])){

                R_Dungeon.CurrentRoom[R_Player.Pos.x][R_Player.Pos.y] = ' ';

                try {
                    R_Generate.GenerateRoom(
                            Objects.requireNonNull(R_Generate.GetRoom(R_Dungeon.Direction.NEXT)).nextRoom,
                            R_Dungeon.CurrentRoom);
                }
                catch (NullPointerException e){
                    e.getMessage();
                    R_Dungeon.CurrentRoom[1][1] = Player;
                }
                finally {
                    R_Generate.PutPlayerInDungeon(
                            R_Generate.GetCenterOfRoom(), 1,
                            R_Dungeon.CurrentRoom);
                }
            }
            else if(R_Generate.CheckBack(R_Dungeon.CurrentRoom[x][y]))
                {
                    R_Player.CurrentRoom--;
                    R_Dungeon.CurrentRoom[R_Player.Pos.x][R_Player.Pos.y] = ' ';
                    try {
                        R_Generate.GenerateRoom(R_Generate.GetRoom(R_Dungeon.Direction.BACK),
                                R_Dungeon.CurrentRoom);
                    }
                    catch (NullPointerException e) {
                        e.getMessage();
                    }
                    finally {
                        R_Generate.PutPlayerInDungeon(
                                R_Generate.GetCenterOfRoom(), 1,
                                R_Dungeon.CurrentRoom);
                    }
                }
            else
                {
                    R_Dungeon.CurrentRoom[R_Player.Pos.x][R_Player.Pos.y] = ' ';
                    R_Dungeon.CurrentRoom[x][y] = Player;
                    R_Player.Pos.x = x;
                    R_Player.Pos.y = y;
                }
            }
        }
//        if(R_Generate.CheckExit(R_Generate.CurrentRoom[x][y])) {
//            T_View.NextRoomWindow(T_View.terminal);
//        }
    }
