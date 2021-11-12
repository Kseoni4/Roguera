/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.base.Debug;
import com.rogurea.base.GameObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class Cell implements Serializable {

    private Room linkedRoom;

    public Position position;

    public ArrayList<GameObject> gameObjects;

    boolean isWall = false;

    private int lastObjectIndex;

    public static final Cell EMPTY_CELL = new Cell(new Position(0,0));

    public boolean isEmpty(){
        return this.gameObjects.isEmpty() && !isWall;
    }

    public GameObject getFromCell(){
        try {
            if (!isEmpty()) {
                Optional<GameObject> gameObject;
                gameObject = Optional.ofNullable(this.gameObjects.get(lastObjectIndex));
                return gameObject.orElse(EditorEntity.EMPTY_CELL);
            }
            else {
                return EditorEntity.EMPTY_CELL;
            }
        } catch (IndexOutOfBoundsException e){
            Debug.toLog("[CELL|"+position.toString()+"|] Error out of bounds, return empty");
            return EditorEntity.EMPTY_CELL;
        }
    }

    public void removeFromCell() {
        if(lastObjectIndex < 0)
            lastObjectIndex = 0;
        removeFromCell(gameObjects.get(lastObjectIndex));
    }

    public void removeFromCell(GameObject gameObject){
        this.gameObjects.remove(gameObject);
        this.linkedRoom.gameObjects.remove(gameObject);
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

    public Cell(Room room){
        this(new Position(), room);
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
