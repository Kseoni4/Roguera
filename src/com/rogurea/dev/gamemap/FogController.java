package com.rogurea.dev.gamemap;

import com.rogurea.dev.resources.Colors;

import java.util.ArrayList;

public class FogController {

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
                if(Scan.CheckFogPart(cell.getFromCell())){
                    cell.getFromCell().model.changeColor(Colors.GREY_243);
                    //cell.getFromCell().model.changeModel('.');
                }
            }
    }

    public void RemoveFogParts(Cell[] cells, int depth){
        for(Cell cell : cells){
            int __depth = depth;
            if(Scan.CheckFogPart(cell.getFromCell())) {
                if(__depth > 0)
                    RemoveFogParts(cell.getCellsAround(), --__depth);
                cell.removeFromCell();
            }
        }
    }

}
