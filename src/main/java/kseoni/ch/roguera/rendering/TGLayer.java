package kseoni.ch.roguera.rendering;

public enum TGLayer {

    BACKGROUND(0),
    FOREGROUND(1),
    UI(2);

    private final int layer;

    TGLayer(int layer){
        this.layer = layer;
    }
}
