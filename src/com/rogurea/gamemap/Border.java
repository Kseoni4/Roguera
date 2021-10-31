/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.base.GameObject;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;

public class Border extends GameObject {

    public static int BorderCounter = 0;

    //public static String color = Colors.GREY;

    public Border(Model border_model) {
        this.tag = "structure";
        this.id = BorderCounter++;
        border_model.changeColor(Colors.GREY);
        setModel(border_model);
    }
}
