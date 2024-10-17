package kseoni.ch.roguera.rendering;

public interface Drawer<T> {

    void draw(T object);

    void refresh();
}
