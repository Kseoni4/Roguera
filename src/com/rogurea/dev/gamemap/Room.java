/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.AIController;
import com.rogurea.dev.base.Debug;
import com.rogurea.dev.base.Entity;
import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.creatures.Creature;
import com.rogurea.dev.creatures.NPC;
import com.rogurea.dev.gamelogic.NPCLogicFactory;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.mapgenerate.RoomGenerate;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.Model;
import com.rogurea.devMain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Room implements Serializable {

    public int RoomNumber;

    public int Width;

    public int Height;

    public boolean isEndRoom;

    public transient RoomGenerate.RoomSize roomSize;

    private FogController fogController;

    ArrayList<Cell> Cells;

    public ArrayList<Position> positionsInsidePerimeter;

    ArrayList<GameObject> gameObjects;

    ArrayList<Cell> Perimeter;

    Room nextRoom;

    Room prevRoom;

    ExecutorService mobThreads = Executors.newCachedThreadPool();

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
                Cells.add(new Cell(new Position(x, y),this));
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

    public void setPositionsInsidePerimeter(){
        positionsInsidePerimeter = new ArrayList<>();
        Cell maxCellOnX = Perimeter.stream().max(Comparator.comparingInt(cell -> cell.position.x)).get();
        Cell maxCellOnY = Perimeter.stream().max(Comparator.comparingInt(cell -> cell.position.y)).get();
        Cells.forEach(cell -> Perimeter.forEach(cellPerimeter ->{
            if((cell.position.x > cellPerimeter.position.x && cell.position.y < cellPerimeter.position.y)
                    && (cell.position.x < maxCellOnX.position.x && cell.position.y < maxCellOnY.position.y) && !cell.isWall()){
                    positionsInsidePerimeter.add(cell.position);
                }
            })
        );
    }

    public void setCells(ArrayList<Cell> cells){
        this.Cells = cells;
    }

    public void replaceCell(Cell cell, Position position){
        this.Cells.removeIf(cell1 -> cell1.position.equals(position));
        this.Cells.add(cell);
    }

    public ArrayList<GameObject> getObjectsSet(){
        return gameObjects;
    }

    public GameObject[] getObjectsByTag(String tag){
        return gameObjects.stream().filter(gameObject -> gameObject.tag.startsWith(tag)).toArray(GameObject[]::new);
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
        this.gameObjects = new ArrayList<>();
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

    public void startMobAIThreads(){
        mobThreads = Executors.newCachedThreadPool();
        for(GameObject gO : getObjectsByTag("creature.mob")){
            Creature creature = (Creature) gO;
            Debug.toLog("[Room "+RoomNumber+"]" + creature.getName() + " to thread");
            mobThreads.execute(new AIController(creature));
        }
        Debug.toLog("[Room "+RoomNumber+"] mob threads has started");
    }

    public void endMobAIThreads() {
        mobThreads.shutdownNow();
        try {
            if(mobThreads.awaitTermination(2, TimeUnit.SECONDS))
                gameObjects.removeIf(go -> go.tag.startsWith("creature.mob"));
                Debug.toLog("[Room "+RoomNumber+"] mob threads has ended");
        } catch (InterruptedException e){
            e.printStackTrace();
        }

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
