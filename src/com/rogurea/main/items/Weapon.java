/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.items;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

public class Weapon extends Equipment {

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

    public int SpawnRoomNumber;

    public Weapon(String name, int SellPrice, _weapontype weapontype, int RoomNumber) {
        super(name, SellPrice, weapontype.getModel(), RoomNumber);
        super.name = PutName();
        super.name = PutMaterialInName(this.Material);
        this.SpawnRoomNumber = RoomNumber;
        GameResources.AllWeapons.add(this);
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
}
