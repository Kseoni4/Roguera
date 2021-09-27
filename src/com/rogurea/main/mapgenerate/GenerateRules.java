/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.mapgenerate;

import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Position;

public class GenerateRules {
    public static boolean IsNotOutOfBoundsRule(Position point, char[][] CurrentRoom){
        int x = point.x, y = point.y;
        return (y < CurrentRoom.length && x < CurrentRoom[0].length) && (y >= 0 && x >= 0);
    }

    public static boolean IsNotObstaclesRule(Position point, char[][] CurrentRoom){
        int x = point.x, y = point.y;
        for(int i = 0; i < CurrentRoom.length-y; i++){
            if(!Scans.CheckWall(CurrentRoom[y+i][x]))
                return false;
        }
        for(int i = 0; i < CurrentRoom[0].length-x; i++){
            if(!Scans.CheckWall(CurrentRoom[y][x+i]))
                return false;
        }
        for(int i = 0; i > (CurrentRoom.length-y)*-1 && y+i > 0; i--){
            if(!Scans.CheckWall(CurrentRoom[y+i][x]))
                return false;
        }
        for(int i = 0 ; i > (CurrentRoom[0].length-x)*-1 && x+i > 0; i--){
            if(!Scans.CheckWall(CurrentRoom[y][x+i]))
                return false;
        }
        return true;
    }

    public static boolean IsNotSameCellRule(Position point, char[][] CurrentRoom){
        int x = point.x, y = point.y;
        return CurrentRoom[y][x] != '.';
    }

}
