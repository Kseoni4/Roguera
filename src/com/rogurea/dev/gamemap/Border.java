package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;

public class Border extends GameObject {

    public static int BorderCounter = 0;

    public static String color = Colors.GREY;

    public Border(Model border_model) {
        this.tag = "structure";
        this.id = BorderCounter++;
        setModel(border_model);
    }
}
