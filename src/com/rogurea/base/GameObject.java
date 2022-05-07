/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.base;

import com.googlecode.lanterna.TextCharacter;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.Position;
import com.rogurea.resources.Model;

import java.io.Serializable;

/**
 * <p>
 *     Базовый абстрактный класс игрового объекта.
 *     Описывает основные свойства всех объектов:
 *     <ul>
 *         <li/>{@code id} - идентифкатор - номер созданного объекта;
 *         <li/>{@code tag} - текстовый "ярлык", по которому данный объект можно найти;
 *         <li/>{@code cellPostion} - позиция объекта на игровой сцене;
 *         <li/>{@code model} - моделька (символ) объекта.
 *     </ul>
 *     Так же описывает метод {@link #placeObject(Cell)}, который помещает объект в клетку ({@link Cell}) на игровом поле.
 * </p>
 */
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

    /**
     * Помещает объект в соотевствующую клетку на игровом поле
     * @param cell клетка на игровом поле (класс {@link Cell})
     */
    public void placeObject(Cell cell){
        this.cellPosition = cell.position;
        cell.gameObjects.add(this);
    }

    @Override
    public String toString(){
        return this.model.toString();
    }
}
