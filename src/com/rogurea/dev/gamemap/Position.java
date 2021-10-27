/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.googlecode.lanterna.TerminalPosition;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 *      Класс, описывающий позицию в 2D-пространстве игрового поля.
 *      Так как реализация вывода представляет собой текстовые строки и столбцы,
 *      то класс содержит в себе два публичных поля <br>
 * </p>
 * <code>
 *     int x - столбец <br>
 *     int y - строка <br>
 * </code>
 * <p>
 *     Специальные методы<br>
 *     <code>
 *      getRelative(int x, int y) - функция, возвращающая новую позицию <u>относительно</u> заданной.
 *      На вход принимает сдвиг по двум осям.
 *     </code>
 * </p>
 *
 */
public class Position implements Serializable {
    public int y;
    public int x;

    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position NORTH = new Position(0,-1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position WEST = new Position(-1,0);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position EAST = new Position(1,0);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position SOUTH = new Position(0,1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position NORTH_WEST = new Position(-1,-1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position NORTH_EAST = new Position(1,-1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position SOUTH_WEST = new Position(-1,1);
    /**
     * Статическая константа, описывающая точку расположенную по "розе ветров" на одну относительно текущей.
     */
    private static final Position SOUTH_EAST = new Position(1,1);

    /**
     * Статический массив точек вокруг данной.
     */
    public static final Position[] AroundPositions = {
            NORTH,
            WEST,
            EAST,
            SOUTH,
            NORTH_WEST,
            NORTH_EAST,
            SOUTH_WEST,
            SOUTH_EAST
    };

    public static final Position FRONT = new Position(0,1);
    public static final Position BACK = new Position(0,-1);
    public static final Position LEFT = new Position(-1,0);
    public static final Position RIGHT = new Position(1,0);

    /**
     * Задаёт позицию для объекта по входным координатам
     * @param y - строка
     * @param x - столбец
     */
    public void setPosition(int y, int x){
        this.y = y;
        this.x = x;
    }

    /**
     * Обнулить позицию объекта
     */
    public void setToZero(){
        this.x = 0;
        this.y = 0;
    }

    /**
     * Принимает на вход смещение по столбцу и строке <u>относительно</u> текущей позиции.
     * @param x - столбец
     * @param y - строка
     * @return новая позиция <u>относительно</u> текущей позиции.
     */
    public Position getRelative(int x, int y){
        return new Position(this.x+x,this.y+y);
    }

    /**
     * Принимает на вход позицию, смещённую <u>относительно</u> текущей позиции.
     * @param direction точка в сторону которого смещается позиция
     * @return новая позиция <u>относительно</u> текущей позиции.
     */
    public Position getRelative(Position direction){
        return getRelative(direction.x, direction.y);
    }

    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * <p>
     *     Метод сравнения двух координат. Обычно применяется для проверки пересечения двух объектов на одной позиции.
     * </p>
     *
     * @param p вторая координата
     * @return true, если координаты равны
     */
    public boolean equals(Position p) {
        return y == p.y && x == p.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }

    @Override
    public String toString(){
        return "x: " + x + ";" + "y: " + y;
    }

    public Position(Position newPosition){
        this(newPosition.x, newPosition.y);
    }

    public TerminalPosition toTerminalPosition(){
        return new TerminalPosition(this.x, this.y);
    }

    public Position(){
        setToZero();
    }
}
