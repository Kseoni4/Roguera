package com.rogurea.prototype_old;

public class Weapon extends Item {

    private int Damage;

    public enum WeaponType{
        SWORD{
            public int bonus = 15;
        },
        AXE {
            public int bonus = 10;
        },
        KNIFE{
            public int bonus = 5;
        };
       // BOW;

        public int bonus;
    }

    private WeaponType weaponType;

    public void SetDamage(int dmg, WeaponType weaponType){
        this.Damage = dmg + weaponType.bonus;
    }

    public int getDamage(){
        return this.Damage;
    }

    public Weapon(String name, int sellprice, WeaponType weaponType, int dmg){
        super(name, sellprice);
        this.weaponType = weaponType;
        SetDamage(dmg, this.weaponType);
    }
}
