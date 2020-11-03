package com.rogurea.main.items;

import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

public class Weapon extends Item {

    private final int _damage;

    public int _minLevel = 1;

    public enum _weapontype {
        MELLE{
            public char getModel(){
                return GetRandom.WeaponModel("MELEE");
            }
        },
        RANGE{
            public char getModel(){
                return GetRandom.WeaponModel("RANGE");
            }
        };
        public abstract char getModel();
    }

    public boolean IsWearable = true;

    public final String Material;

    public Weapon(String name, int SellPrice, _weapontype weapontype) {
        super(name, SellPrice, weapontype.getModel());
        this._minLevel += Player.Level;
        this.Material = GetRandom.WeaponMaterial();
        this._damage = ItemGenerate.WeaponForge(this._minLevel, this.Material);
        super.name = PutMaterialInName(this.Material);
        super.name = PutName();
    }

    public String getMaterialColor(){
        return GameResources.MaterialColor.get(this.Material);
    }

    private String PutName(){
       return super.name.replace("%name%", ItemGenerate.SetWeaponName(this._model));
    }

    private String PutMaterialInName(String material){
        return super.name.replace("%mat%", material);
    }

    public int getDamage(){
        return this._damage;
    }

    public boolean checkLevel(int plevel){
        return plevel >= this._minLevel;
    }
}
