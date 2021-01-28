package com.rogurea.main.items;

import com.rogurea.main.map.Position;

import java.io.Serializable;

public class Item implements Serializable {

    public String name;

    public final int id;

    protected static int counter = 0;

    public int SellPrice;

    public char _model;

    public Position ItemPosition = new Position();

    public String getMaterialColor(){
        return "GREY";
    }

    public Integer getID(){
        return this.id;
    }

    public Item(){
        id = -1;
    }

    public Item(String name, int SellPrice){
        this.name = name;
        this.SellPrice = SellPrice;
        id = counter++;
    }

    public Item(String name, int SellPrice, char model){
        this.name = name;
        this.SellPrice = SellPrice;
        this._model = model;
        id = counter++;
    }
}
