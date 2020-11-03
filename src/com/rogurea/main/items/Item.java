package com.rogurea.main.items;

public class Item {
    public String name;

    public final int id;

    protected static int counter = 0;

    public final int SellPrice;

    public char _model;

    public String Material;

    public String getMaterialColor(){
        return "";
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
