package com.rogurea.dev.items;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;

public class Item extends GameObject {
    public enum Materials{
        WOOD{
            @Override
            public String getColor() {
                return Colors.BROWN;
            }

            @Override
            public float getDurability() {
                return 0;
            }
        },
        STONE {
            @Override
            public String getColor() {
                return Colors.GREY_241;
            }

            @Override
            public float getDurability() {
                return 0;
            }
        },
        IRON {
            @Override
            public String getColor() {
                return Colors.IRON;
            }

            @Override
            public float getDurability() {
                return 0;
            }
        },
        GOLD {
            @Override
            public String getColor() {
                return Colors.GOLDEN;
            }

            @Override
            public float getDurability() {
                return 0;
            }
        },
        DIAMOND {
            @Override
            public String getColor() {
                return Colors.DIAMOND;
            }

            @Override
            public float getDurability() {
                return 0;
            }
        };
        public abstract String getColor();
        public abstract float getDurability();

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
}
