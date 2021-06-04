package com.rogurea.dev.gamemap;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    public int y;
    public int x;

    private static final Position NORTH = new Position(0,-1);
    private static final Position WEST = new Position(-1,0);
    private static final Position EAST = new Position(1,0);
    private static final Position SOUTH = new Position(0,1);
    private static final Position NORTH_WEST = new Position(-1,-1);
    private static final Position NORTH_EAST = new Position(1,-1);
    private static final Position SOUTH_WEST = new Position(-1,1);
    private static final Position SOUTH_EAST = new Position(1,1);

    public static final Position[] AroundPositions = {
            NORTH,
            WEST,
            EAST,
            SOUTH,
            NORTH_WEST,
            NORTH_EAST,
            SOUTH_WEST,
            SOUTH_EAST
    };

    public void setPosition(int y, int x){
        this.y = y;
        this.x = x;
    }

    public void setToZero(){
        this.x = 0;
        this.y = 0;
    }

    public Position getRelative(int x, int y){
        return new Position(this.x+x,this.y+y);
    }

    public Position getRelative(Position direction){
        return getRelative(direction.x, direction.y);
    }

    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Position p) {
        return y == p.y && x == p.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }

    @Override
    public String toString(){
        return "x: " + x + ";" + "y: " + y;
    }

    public Position(Position newPosition){
        this(newPosition.x, newPosition.y);
    }

    public Position(){
        setToZero();
    }
}
