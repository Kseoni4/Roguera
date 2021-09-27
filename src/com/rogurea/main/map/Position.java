/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.map;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    public int y;
    public int x;

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
