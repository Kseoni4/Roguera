/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.GameObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Cell implements Serializable {

    private Room linkedRoom;

    public Position position;

    public ArrayList<GameObject> gameObjects;

    boolean isWall = false;

    private int lastObjectIndex;

    public boolean isEmpty(){
        return this.gameObjects.isEmpty() && !isWall;
    }

    public GameObject getFromCell(){
        if(!isEmpty())
            return this.gameObjects.get(lastObjectIndex);
        else{
            return EditorEntity.EMPTY_CELL;
        }
    }

    public void removeFromCell(){
            gameObjects.remove(lastObjectIndex);
            UpdateLastItemCounter();
    }

    public GameObject getAndRemoveFromCell(){
        GameObject gatedObject = gameObjects.get(lastObjectIndex);
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
        linkedRoom.gameObjects.add(gameObject);
        UpdateLastItemCounter();
    }

    private void UpdateLastItemCounter(){
        int size = gameObjects.size();

        if(size > 0)
            lastObjectIndex = gameObjects.size()-1;
        else
            lastObjectIndex = 0;
    }

    public Cell(Position position){
        this.position = position;
        gameObjects = new ArrayList<>();
    }

    public Cell(Position position, Room room){
        this(position);
        this.linkedRoom = room;
    }

    public Cell[] getCellsAround(){
        Cell[] cells = new Cell[8];
        int i = 0;
        for(Position direction : Position.AroundPositions){
            cells[i] = Dungeon.getCurrentRoom().getCell(this.position.getRelative(direction));
            i++;
        }
        return cells;
    }
}
