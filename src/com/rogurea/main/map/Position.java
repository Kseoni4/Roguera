package com.rogurea.main.map;

public class Position {
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

    public Position(){

    }
}
