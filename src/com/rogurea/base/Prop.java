/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.base;

import com.rogurea.resources.Model;

public abstract class Prop extends GameObject {

    public static int PropCounter = 0;

    public Prop(Model model) {
        this.id = PropCounter++;
        this.tag = "prop";
        setModel(model);
    }
}
