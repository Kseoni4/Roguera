package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.gamemap.Position;

import java.io.Serializable;
import java.util.ArrayList;

public class Cell implements Serializable {

    public Position position;

    public ArrayList<GameObject> gameObjects;

    boolean isWall = false;

    private int lastItem;

    public boolean isEmpty(){
        return this.gameObjects.isEmpty() && !isWall;
    }

    public GameObject getFromCell(){
        if(!isEmpty())
            return this.gameObjects.get(lastItem);
        else{
            return EditorEntity.emptyCell;
        }
    }

    public void removeFromCell(){
            gameObjects.remove(lastItem);
            UpdateLastItemCounter();
    }

    public GameObject getAndRemoveFromCell(){
        GameObject gatedObject = gameObjects.get(lastItem);
        removeFromCell();
        return gatedObject;
    }

    public void clear(){
        gameObjects.clear();
        UpdateLastItemCounter();
    }

    public boolean isWall(){
        return this.isWall;
    }

    public void setWall(){
        this.isWall = true;
    }

    public void unsetWall(){this.isWall = false;}

    public void putIntoCell(GameObject gameObject){
        if(gameObject instanceof Border)
            setWall();
        gameObject.placeObject(this);
        UpdateLastItemCounter();
    }

    private void UpdateLastItemCounter(){
        int size = gameObjects.size();

        if(size > 0)
            lastItem = gameObjects.size()-1;
        else
            lastItem = 0;
    }

    public Cell(Position position){
        this.position = position;
        gameObjects = new ArrayList<>();
    }

    public Cell[] getCellsAround(){
        Cell[] cells = new Cell[8];
        int i = 0;
        for(Position direction : Position.AroundPositions){
            cells[i] = Dungeon.GetCurrentRoom().getCell(this.position.getRelative(direction));
            i++;
        }
        return cells;
    }
}
