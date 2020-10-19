package com.rogurea.main.items;

public class Item {
    public String name;

    public int id;

    protected static int counter = 0;

    public int SellPrice;

    public char _model;

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
