/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.Roguera;
import com.rogurea.base.AIController;
import com.rogurea.base.Debug;
import com.rogurea.base.Entity;
import com.rogurea.base.GameObject;
import com.rogurea.creatures.Creature;
import com.rogurea.mapgenerate.RoomGenerate;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.Model;
import com.rogurea.workers.DrawLootWorker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Room implements Serializable {

    public int roomNumber;

    public int floorNumber;

    public int width;

    public int height;

    public boolean isEndRoom;

    public RoomGenerate.RoomSize roomSize;

    private FogController fogController;

    HashMap<Position,Cell> cells;

    public ArrayList<Position> positionsInsidePerimeter;

    ArrayList<GameObject> gameObjects;

    ArrayList<Cell> perimeter;

    Room nextRoom;

    Room prevRoom;

    transient ExecutorService mobThreads = Executors.newCachedThreadPool();

    public Cell getCell(int x, int y){
        return cells.get(new Position(x,y));
    }

    public Cell getCell(Position position){
        return getCell(position.x, position.y);
    }

    public ArrayList<Cell> getCells(){
        return (ArrayList<Cell>) cells.values().stream().collect(Collectors.toList());
    }

    private void makeCells(){
        cells = new HashMap<>();

        perimeter = new ArrayList<>();

        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position position = new Position(x, y);
                cells.put(position, new Cell(position,this));
            }
        }
    }

    public void ClearCells(){
        makeCells();
    }

    private void makeCells_test(){
        /*int i = 0;

        cells = new ArrayList<>();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                cells.add(new Cell(new Position(x,y)));
                cells.get(i).putIntoCell(new Border(new Model("wall", Colors.WHITE_BRIGHT, '*')));
                i++;
            }
        }*/
    }

    public Position getBottomCenterCellPosition(){
        return cells.get(new Position(this.width/2,this.height)).position;
    }

    public Position getTopCenterCellPosition(){
        return cells.get(new Position(this.width/2, 0)).position;

    }

    public void makePerimeter(){
        cells.values().forEach(cell -> {
            if(cell.isWall)
                perimeter.add(cell);
        });
    }

    public void setPositionsInsidePerimeter(){
        positionsInsidePerimeter = new ArrayList<>();

        cells.values().forEach(cell -> {
            if(!perimeter.contains(cell) && isIntersectionsOdd(cell)) {
                //cell.putIntoCell(new Entity(new Model("dot", '.')));
                positionsInsidePerimeter.add(cell.position);
            }
        });

        /*Cell maxCellOnX = perimeter.stream().max(Comparator.comparingInt(cell -> cell.position.x)).get();
        Cell maxCellOnY = perimeter.stream().max(Comparator.comparingInt(cell -> cell.position.y)).get();

        cells.forEach(cell -> perimeter.forEach(cellPerimeter ->{
            if((cell.position.x > cellPerimeter.position.x && cell.position.y < cellPerimeter.position.y)
                    && (cell.position.x < maxCellOnX.position.x && cell.position.y < maxCellOnY.position.y) && !cell.isWall()){
                    positionsInsidePerimeter.add(cell.position);
                }
            })
        );*/
    }

    private boolean isIntersectionsOdd(Cell cell){

        int y = 0;

        int x = 0;

        int intersections = 0;

        for(;x < width; x++){
            Optional<Cell> gettedCell = Optional.ofNullable(getCell(cell.position.getRelative(x,0)));
            if(gettedCell.isPresent()) {
                if (gettedCell.get().isWall()) {
                    intersections++;
                }
            }
        }
        if(intersections > 2){
            return false;
        }
        return intersections % 2 != 0;
    }

    public void setCells(ArrayList<Cell> cells){
        this.cells.clear();
        for(Cell cell : cells) {
            this.cells.put(cell.position, cell);
        }
    }

    public void replaceCell(Cell cell, Position position){
        this.cells.replace(position,cell);
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

    public GameObject getObjectByTag(String tag){
        return gameObjects.stream().filter(gameObject -> gameObject.tag.startsWith(tag)).findFirst().get();
    }

    public Room(int roomNumber, int width, int height, RoomGenerate.RoomSize roomSize) {
        this.roomNumber = roomNumber;
        this.width = width;
        this.height = height;
        this.roomSize = roomSize;
        makeCells();
        this.gameObjects = new ArrayList<>();
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
        if(isEndRoom){
            this.nextDoor = new Entity(
                    GameResources.getModel("FloorExit").changeColor(Colors.MAGENTA),
                    Dungeon::changeFloor
            );
        }
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
        if(!Roguera.isClearMap)
            fogController = new FogController(this.getCells());
    }

    public FogController getFogController(){
        return this.fogController;
    }

    public void startMobAIThreads(){
        try {
            mobThreads = Executors.newCachedThreadPool();
            for (GameObject gO : getObjectsByTag("creature.mob")) {
                Creature creature = (Creature) gO;
                if (creature.getHP() > 0) {
                    Debug.toLog("[Room " + roomNumber + "]" + creature.getName() + " to thread");
                    mobThreads.execute(new AIController(creature));
                }
            }
            if (getObjectsByTag("creature.mob").length > 0) {
                mobThreads.execute(new DrawLootWorker());
                //mobThreads.execute(new MapCleanWorker());
                Debug.toLog("[Room " + roomNumber + "] mob threads has started");
            }
        } catch (NullPointerException e){
            Debug.toLog("[ERROR][MOB]"+e.getMessage());
        } finally {
            mobThreads.shutdown();
        }
    }

    public void endMobAIThreads() {
        if(mobThreads != null) {
            mobThreads.shutdownNow();
            try {
                if (mobThreads.awaitTermination(2, TimeUnit.SECONDS)) {
                    boolean isTerm = mobThreads.isTerminated();
                    Debug.toLog("[Room] threads is " + (isTerm ? Colors.GREEN_BRIGHT : Colors.RED_BRIGHT + "not") + " terminated");
                    gameObjects.removeIf(go -> (go != null && go.tag.startsWith("creature.mob") && ((Creature) go).getHP() <= 0));
                    Debug.toLog("[Room " + roomNumber + "] mob threads has ended");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
