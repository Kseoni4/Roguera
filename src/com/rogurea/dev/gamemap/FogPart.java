package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;

public class FogPart extends GameObject {

    public FogPart(){
        this.tag = "fog";
        setModel(new Model("fog", Colors.GREY, '#'));
    }

}
