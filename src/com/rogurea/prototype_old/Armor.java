package com.rogurea.prototype_old;

public class Armor extends Item {

    public int Defence;

    public enum ArmorType{
        HELMET {
                public int getArmorBonus() {return 5;}
            },
        CHEST {
                public int getArmorBonus() {return 15;}
            },
        BOOTS{
            public int getArmorBonus() {return 5;}
        },
        LEGS {
            public int getArmorBonus() {return 10;}
        };
        public abstract int getArmorBonus();
    }

    public ArmorType armorType;

    public Armor(String name, int SellPrice, int Defence, ArmorType ArmorType){
        super(name, SellPrice);
        this.armorType = ArmorType;
        this.Defence = Defence + armorType.getArmorBonus();
        Wearable = true;
    }
}
