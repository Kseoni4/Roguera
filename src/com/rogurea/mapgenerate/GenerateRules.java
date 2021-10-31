/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.mapgenerate;

import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.Scan;
import com.rogurea.gamemap.Position;

import java.util.ArrayList;
import java.util.Objects;

public class GenerateRules {

    public static boolean IsNotOutOfBoundsRule(Position point, ArrayList<Cell> CurrentRoomCell){
        return CurrentRoomCell.contains(
                CurrentRoomCell.stream()
                        .filter(
                                cell -> cell.position.equals(point))
                        .findFirst()
                        .orElse(new Cell(new Position(-1,-1)))
        );
    }

    public static boolean IsNotObstaclesRule(Position point, ArrayList<Cell> CurrentRoomCells){
        return !Scan.checkWall(Objects.requireNonNull(CurrentRoomCells.stream()
                                    .filter(
                                            cell -> cell.position.equals(point))
                                    .findFirst()
                                    .orElse(null)));
    }

    public static boolean IsNotSameCellRule(Position point, ArrayList<Cell> CurrentRoomCells){
        return Objects.requireNonNull(CurrentRoomCells.stream()
                .filter(
                        cell -> cell.position.equals(point))
                .findFirst()
                .orElse(null))
                .isEmpty();
    }

}
