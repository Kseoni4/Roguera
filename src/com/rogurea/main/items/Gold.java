/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.items;

import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

public class Gold extends Item {

    public final int Amount;

    @Override
    public String getMaterialColor() {
        return Colors.GOLDEN;
    }

    public Gold(int Amount) {
        super("gold", 0, GameResources.Gold);
        this.Amount = Amount;
    }
}
