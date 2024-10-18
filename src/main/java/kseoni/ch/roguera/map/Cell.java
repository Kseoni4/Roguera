package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.utils.ObjectPool;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class Cell {

    @Getter
    private Position position;

    private final Deque<GameObject> gameObjectStack;

    private boolean isWall = false;

    public boolean isWall(){
        return this.isWall;
    }

    public void setWall(boolean value){
        this.isWall = value;
    }

    public boolean isEmpty(){
        return gameObjectStack.peek().equals(GameObject.getEmpty()) && !isWall;
    }

    public <T extends GameObject> T getObject(){
        return (T) gameObjectStack.peek();
    }

    public <T extends GameObject> T removeObject(){
        return (T) gameObjectStack.pop();
    }

    public void placeObject(GameObject gameObject) {
        gameObject.setPosition(this.position);
        this.gameObjectStack.push(gameObject);
    }

    public void replaceObject(GameObject gameObject){
        GameObject go = removeObject();
        if(!(go == GameObject.getEmpty())){
            ObjectPool.get().removeObjectFromPool(go);
        }
        placeObject(gameObject);
    }

    public void clearCell(){
        this.gameObjectStack.clear();
    }

    public Cell(Position position){
        this.position = position;
        this.gameObjectStack = new ArrayDeque<>();
        placeObject(GameObject.getEmpty());
    }

    public Cell(Position position, GameObject object){
        this.position = position;
        this.gameObjectStack = new ArrayDeque<>();
        placeObject(object);
    }

    @Override
    public String toString() {
        return "Cell"+position.toString() + "\n";
    }
}