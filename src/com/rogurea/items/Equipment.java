/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.items;

import com.rogurea.resources.Model;

import java.util.concurrent.ThreadLocalRandom;

public class Equipment extends Item implements Wearable<Integer> {

    public static final Equipment BLANK = new Equipment("blank",Model.BLANK,BLANK_MATERIAL, "none");

    public String equipmentStat;

    public Equipment(String name, Model model, Materials itemMaterial, String stat) {
        super(name, new Model(model), itemMaterial);
        tag = tag.concat(".equipment");
        this.equipmentStat = stat;
        int sellPrice = ThreadLocalRandom.current().nextInt(1, 25) * itemMaterial.costEmpower;
        this.setSellPrice(sellPrice);
    }

    public Equipment(String name, Model model, String stat){
        this(name, model, Materials.NONE, stat);
    }

    @Override
    public Integer getStats() {
        return 0;
    }
}
