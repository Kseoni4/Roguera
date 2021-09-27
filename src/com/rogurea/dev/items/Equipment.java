/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.resources.Model;

public class Equipment extends Item implements Wearable<Number> {

    public static final Equipment BLANK = new Equipment("blank",Model.BLANK,BLANK_MATERIAL);

    public Equipment(String name, Model model, Materials itemMaterial) {
        super(name, model, itemMaterial);
        tag = tag.concat(".equipment");
    }

    @Override
    public Number getStats() {
        return 0.0d;
    }
}
