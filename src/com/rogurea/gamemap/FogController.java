/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.resources.Colors;

import java.io.Serializable;
import java.util.ArrayList;

public class FogController implements Serializable {

    public FogController(ArrayList<Cell> roomCells){
        setFogIntoRoom(roomCells);
    }

    private void setFogIntoRoom(ArrayList<Cell> roomCells){
        roomCells.forEach(cell -> cell.putIntoCell(new FogPart()));
    }

    private void changeNearParts(Cell[] nearFogParts){
            for(Cell cell : nearFogParts){
                if(cell == null)
                    continue;
                if(Scan.checkFogPart(cell.getFromCell())){
                    cell.getFromCell().model.changeColor(Colors.GREY_243);
                }
            }
    }

    public void removeFogParts(Cell[] cells, int depth){
        for(Cell cell : cells){
            int __depth = depth;
            try {
                if (Scan.checkFogPart(cell.getFromCell())) {
                    cell.removeFromCell();
                }
                if (__depth > 0)
                    removeFogParts(cell.getCellsAround(), --__depth);
            } catch (NullPointerException ignored){
            }
        }
    }

}
