/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.base.GameObject;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;

public class FogPart extends GameObject {

    public FogPart(){
        this.tag = "fog";
        setModel(new Model("fog", Colors.GREY, '#'));
    }

}
