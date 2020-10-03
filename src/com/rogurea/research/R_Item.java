package com.rogurea.research;

public class R_Item {
    public String name;

    public int id;

    public static int counter = 0;

    boolean Wearable;

    public int SellPrice;

    public R_Item(String name, int SellPrice){
        this.name = name;
        this.SellPrice = SellPrice;
        id = counter++;
    }
}
