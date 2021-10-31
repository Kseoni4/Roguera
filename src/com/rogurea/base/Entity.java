/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.base;

import com.rogurea.gamelogic.EntityAction;
import com.rogurea.resources.Model;

public class Entity extends GameObject {

    private static int EntityCounter = 0;
    public EntityAction action;

    public Entity(Model model, EntityAction action){
        this.tag = "entity";
        this.action = action;
        this.id = EntityCounter++;
        setModel(model);
    }

    public Entity(Model model){
        this(model, ()->{});
    }
}
