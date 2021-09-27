/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.base;

import com.googlecode.lanterna.TextCharacter;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.resources.Model;

import java.io.Serializable;

public abstract class GameObject implements Serializable {
    public int id;

    public String tag;

    public Position cellPosition;

    public Model model;

    public TextCharacter getModel(){
        return model.get();
    }

    public Model setModel(Model model){
        return this.model = model;
    }

    public void placeObject(Cell cell){
        this.cellPosition = cell.position;
        cell.gameObjects.add(this);
    }
}
