package kseoni.ch.roguera.graphics.ui;

import kseoni.ch.roguera.base.Position;

public interface Drawer<T> {

    void draw(T object, Position relativePosition);

    void draw(T object);

    void refresh();

    void clear();
}
