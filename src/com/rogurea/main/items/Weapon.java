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

    public Weapon(String name, int SellPrice, _weapontype weapontype, int level) {
        super(name, SellPrice, weapontype.getModel(), level);
        super.name = PutName();
        super.name = PutMaterialInName(this.Material);
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

    public boolean checkLevel(int plevel){
        return plevel >= this.level;
    }
}
