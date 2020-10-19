package com.rogurea.prototype_old;

public class Item {

    public String name;

    public int id;

    public static int counter = 0;

    boolean Wearable;

    public int SellPrice;

    public Item(String name, int SellPrice){
        this.name = name;
        this.SellPrice = SellPrice;
        id = counter++;
    }
}
