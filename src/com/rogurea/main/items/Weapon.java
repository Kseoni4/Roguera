package com.rogurea.main.items;

import com.rogurea.main.resources.GameResources;

public class Weapon extends Item {

    private int _damage;

    public int _minLevel = 1;

    public enum _weapontype {
        MELLE{
            final char _model = GameResources.LongSword;
            public char getModel(){
                return _model;
            }
        },
        RANGE{
            final char _model = GameResources.Bow;
            public char getModel(){
                return _model;
            }
        };
        public abstract char getModel();

    }

    public boolean IsWearable = true;

    public Weapon(String name, int SellPrice, _weapontype weapontype) {
        super(name, SellPrice, weapontype.getModel());
        ItemGenerate.WeaponForge(this._damage, this._minLevel);
    }

    public int getDamage(){
        return this._damage;
    }

    public boolean checkLevel(int plevel){
        return plevel >= this._minLevel;
    }
}
