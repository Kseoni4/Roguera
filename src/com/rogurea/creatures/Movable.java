package com.rogurea.creatures;

import com.rogurea.gamemap.Position;


/**
 * <p>
 *     Интерфейса "движимого" объекта. Описывает что должен реализовывать класс, объекты которого будут перемещаться на игровой карте.
 * </p>
 */
public interface Movable {
    void moveTo(Position position);
}
