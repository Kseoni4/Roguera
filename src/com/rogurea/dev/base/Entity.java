package com.rogurea.dev.base;

import com.rogurea.dev.resources.Model;

public class Entity extends GameObject {

    private static int EntityCounter = 0;
    public Runnable action;

    public Entity(Model model, Runnable action){
        this.tag = "entity";
        this.action = action;
        this.id = EntityCounter++;
        setModel(model);
    }
}
