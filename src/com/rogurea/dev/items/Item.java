/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.items;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameVariables;
import com.rogurea.dev.resources.Model;

public class Item extends GameObject {

    public static final Materials BLANK_MATERIAL = Materials.IRON;

    public enum Materials{
        WOOD (2){
            @Override
            public String getColor() {
                return Colors.BROWN;
            }

            @Override
            public float getStrenght() {
                return GameVariables.MATERIAL_POWER.get(WOOD.name().toLowerCase());
            }
        },
        STONE (3) {
            @Override
            public String getColor() {
                return Colors.GREY_241;
            }

            @Override
            public float getStrenght() {
                return GameVariables.MATERIAL_POWER.get(STONE.name().toLowerCase());
            }
        },
        IRON (4) {
            @Override
            public String getColor() {
                return Colors.IRON;
            }

            @Override
            public float getStrenght() {
                return GameVariables.MATERIAL_POWER.get(IRON.name().toLowerCase());
            }
        },
        GOLD (6) {
            @Override
            public String getColor() {
                return Colors.GOLDEN;
            }

            @Override
            public float getStrenght() {
                return GameVariables.MATERIAL_POWER.get(GOLD.name().toLowerCase());
            }
        },
        DIAMOND (10) {
            @Override
            public String getColor() {
                return Colors.DIAMOND;
            }

            @Override
            public float getStrenght() {
                return GameVariables.MATERIAL_POWER.get(DIAMOND.name().toLowerCase());
            }
        },
        NONE (1){
            @Override
            public String getColor() {
                return Colors.GREY;
            }
            @Override
            public float getStrenght() {
                return 0f;
            }
        };

        public abstract String getColor();
        public abstract float getStrenght();
        public int costEmpower;

        Materials(int costEmpower){
            this.costEmpower = costEmpower;
        }
    }

    private static int itemCounter = 0;

    private final String name;

    private int sellPrice = 1;

    private Materials itemMaterial;

    public Item(String name, Model model, Materials itemMaterial){
        this.tag = "item";
        this.id = ++itemCounter;
        this.name = name;
        setItemMaterial(itemMaterial);
        this.setModel(model).changeColor(itemMaterial.getColor());
    }

    public Item(String name, Model model){
        this(name, model, Materials.NONE);
    }

    public Materials getItemMaterial(){
        return this.itemMaterial;
    }

    public void setItemMaterial(Materials material){
        this.itemMaterial = material;
    }

    public String getName(){
        return name;
    }

    public int getSellPrice(){
        return sellPrice;
    }

    public void setSellPrice(int amount){
        this.sellPrice = amount;
    }

    public String getFullInfo(){
        return "Item ".concat(getName()).concat("\n")
                .concat("\t")
                .concat("ID: ")
                .concat(String.valueOf(this.id))
                .concat("\n\t")
                .concat("Model: ")
                .concat(model.toString())
                .concat("\n\t")
                .concat("Tag: ")
                .concat(this.tag)
                .concat("\n\t")
                .concat("Material: ")
                .concat(getItemMaterial().name())
                .concat("\n\t")
                .concat("Is extend: ")
                .concat(getClass().getName())
                .concat("\n\t")
                .concat("@ID: ")
                .concat(String.valueOf(this.hashCode()).concat(" "+this.getClass().hashCode()));
    }
}
