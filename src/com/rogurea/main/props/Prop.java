package com.rogurea.main.props;

public abstract class Prop {

    public final String name;

    protected final boolean isUsable;

    protected final char model;

    public Prop(String name, boolean isUsable, char model){
        this.name = name;
        this.isUsable = isUsable;
        this.model = model;
    }

    public abstract char getModel();
}
