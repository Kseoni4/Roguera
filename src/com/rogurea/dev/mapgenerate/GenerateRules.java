package com.rogurea.dev.mapgenerate;

import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Scan;
import com.rogurea.dev.gamemap.Position;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

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
        return !Scan.CheckWall(Objects.requireNonNull(CurrentRoomCells.stream()
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
