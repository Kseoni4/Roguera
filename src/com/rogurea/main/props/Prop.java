package com.rogurea.main.props;

import com.rogurea.dev.base.GameObject;

public abstract class Prop extends GameObject {

    public final String name;

    protected final boolean isUsable;

    protected final char model;

    public Prop(String name, boolean isUsable, char model){
        this.name = name;
        this.isUsable = isUsable;
        this.model = model;
    }
}
