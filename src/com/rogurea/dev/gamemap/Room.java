/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.Entity;
import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.mapgenerate.RoomGenerate;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.Model;
import com.rogurea.devMain;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {

    public int RoomNumber;

    int Width;

    int Height;

    public boolean isEndRoom;

    public transient RoomGenerate.RoomSize roomSize;

    private FogController fogController;

    ArrayList<Cell> Cells;

    ArrayList<GameObject> gameObjects;

    ArrayList<Cell> Perimeter;

    Room nextRoom;

    Room prevRoom;

    public Cell getCell(int x, int y){
        return Cells.stream().filter(cell -> cell.position.equals(new Position(x,y))).findFirst().orElse(null);
    }

    public Cell getCell(Position position){
        return getCell(position.x, position.y);
    }

    public ArrayList<Cell> getCells(){
        return Cells;
    }

    private void makeCells(){
        Cells = new ArrayList<>();

        Perimeter = new ArrayList<>();

        for(int x = 0; x < Width; x++) {
            for (int y = 0; y < Height; y++) {
                Cells.add(new Cell(new Position(x, y)));
            }
        }
    }

    public void ClearCells(){
        makeCells();
    }

    private void makeCells_test(){
        int i = 0;

        Cells = new ArrayList<>();

        for(int x = 0; x < Width; x++){
            for(int y = 0; y < Height; y++){
                Cells.add(new Cell(new Position(x,y)));
                Cells.get(i).putIntoCell(new Border(new Model("wall", Colors.WHITE_BRIGHT, '*')));
                i++;
            }
        }
    }

    public Position getBottomCenterCellPosition(){
        return Cells.stream()
                .filter(
                        cell -> cell.position.y == this.Height
                                & cell.position.x == this.Width /2
                ).findFirst().get().position;
    }

    public Position getTopCenterCellPosition(){
        return Cells.stream()
                .filter(
                        cell -> cell.position.y == 0
                                & cell.position.x == this.Width /2
                ).findFirst().get().position;
    }

    public void makePerimeter(){
        Cells.forEach(cell -> {
            if(cell.isWall)
                Perimeter.add(cell);
        });
    }

    public void setCells(ArrayList<Cell> cells){
        this.Cells = cells;
    }

    public void replaceCell(Cell cell, Position position){
        this.Cells.removeIf(cell1 -> cell1.position.equals(position));
        this.Cells.add(cell);
    }

    public GameObject[] getObjectsByTag(String tag){
        return gameObjects.stream().filter(gameObject -> gameObject.tag.equals(tag)).toArray(GameObject[]::new);
    }

    public GameObject getObjectByModel(Model model){
        return gameObjects.stream().filter(gameObject -> gameObject.model.equals(model)).findFirst().orElse(null);
    }

    public GameObject getObjectById(int id){
        return gameObjects.stream().filter(gameObject -> gameObject.id == id).findAny().orElse(null);
    }

    public Room(int roomNumber, int width, int height, RoomGenerate.RoomSize roomSize) {
        this.RoomNumber = roomNumber;
        this.Width = width;
        this.Height = height;
        this.roomSize = roomSize;
        makeCells();
        /*makeCells_test();*/
    }

    public Room(int roomNumber, int width, int height, RoomGenerate.RoomSize roomSize, boolean isEndRoom){
        this(roomNumber, width, height, roomSize);
        this.isEndRoom = isEndRoom;
    }

    public void LinkRooms(Room nextRoom){
        this.nextRoom = nextRoom;
        nextRoom.prevRoom = this;
        LinkDoors();
    }

    public void LinkDoors(){
        buildDoors();
    }

    private void buildDoors(){
        this.nextDoor = new Entity(
                GameResources.getModel("NextDoor").changeColor(Colors.CYAN),
                () ->   Dungeon.ChangeRoom(this.nextRoom));

        this.backDoor = new Entity(
                GameResources.getModel("BackDoor").changeColor(Colors.CYAN),
                () -> Dungeon.ChangeRoom(this.prevRoom));
    }

    public void initFogController(){
        if(!devMain.isClearMap)
            fogController = new FogController(Cells);
    }

    public FogController getFogController(){
        return this.fogController;
    }

    private Entity nextDoor;

    private Entity backDoor;

    public Entity getNextDoor(){
        return nextDoor;
    }

    public Entity getBackDoor(){
        return backDoor;
    }
}
