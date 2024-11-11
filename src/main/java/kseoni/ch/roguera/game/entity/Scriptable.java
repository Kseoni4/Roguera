package kseoni.ch.roguera.game.entity;

import kseoni.ch.roguera.base.GameObject;

@FunctionalInterface
public interface Scriptable<T extends GameObject> {

    void doAction(T entity);

}
