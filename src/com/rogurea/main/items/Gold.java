package com.rogurea.main.items;

import com.rogurea.main.resources.GameResources;

public class Gold extends Item {

    public final int Amount;

    public Gold(int Amount) {
        super("gold", 0, GameResources.Gold);
        this.Amount = Amount;
    }
}
