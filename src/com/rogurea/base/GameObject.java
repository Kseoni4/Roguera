/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.base;

import com.googlecode.lanterna.TextCharacter;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.Position;
import com.rogurea.resources.Model;

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
