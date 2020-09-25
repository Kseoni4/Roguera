package com.rogurea.main;

import java.util.ArrayList; //Не забываем импортировать!

public class Player {
    public String nickName; //Поле (член) класса
    public int HP;
    public int MP;
    public int Level;
    public int CurrentRoom = 1;
    public static int attempt = 0;

    public ArrayList<Item> Inventory = new ArrayList<Item>(); //Инициализация коллекции

    public void ShowName(){ //Метод класса
        System.out.printf("%s", this.nickName);
    }

    public void GetItem(Item item){
        Inventory.add(item);
    }

    public int CountArmor(){
        int Armor = 0;
        for(Item item : Inventory){
            if(item.Wearable){
                Armor += (((Armor) item).Defence);
            }
        }
        if(GameLoop.IsDef){
            return Armor + 10;
        }
        return Armor;
    }

    public void changeHP(int dmg){
        this.HP -= Math.max((dmg - CountArmor()), 0);
    }

    public Player(String nickName){ //Конструктор класса
        this.nickName = nickName;
        HP = 100;
        MP = 20;
        Level = 1;
        GetItem(new Weapon("Small sword", 15, Weapon.WeaponType.SWORD, 15));
        GetItem(new Armor("LeatherArmor", 10, 15, Armor.ArmorType.CHEST));
        attempt++;
    }

}
