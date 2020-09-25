package com.rogurea.research;

import com.rogurea.main.Item;

import java.util.ArrayList;

public class R_Player {
    public final static char Player = '@';

    public static class Pos{
        public static int x = 1;
        public static int y = 1;
    }
    public static String nickName = "Player 1"; //Поле (член) класса
    public static int HP = 100;
    public static int MP = 30;
    public static int Level = 1;
    public static int CurrentRoom = 1;
    public static int attempt = 0;

    public static ArrayList<Item> Inventory = new ArrayList<Item>(); //Инициализация коллекции

}
