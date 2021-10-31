package com.rogurea.items;

import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.Model;

import java.util.concurrent.ThreadLocalRandom;

public class Gold extends Item{

    private int amount;

    public Gold(){
        this("Gold", GameResources.getModel("Gold").changeColor(Colors.GOLDEN));
    }

    private Gold(String name, Model model) {
        super(name, model, Materials.GOLD);
        this.amount = ThreadLocalRandom.current().nextInt(1, 100);
    }

    public int getAmount(){
       return this.amount;
    }
}
