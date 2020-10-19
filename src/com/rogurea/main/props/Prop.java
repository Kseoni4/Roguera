package com.rogurea.main.props;

public abstract class Prop {

    public String name;

    protected boolean isUsable;

    protected char model;

    public Prop(String name, boolean isUsable, char model){
        this.name = name;
        this.isUsable = isUsable;
        this.model = model;
    }

    public abstract char getModel();
}
