package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

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