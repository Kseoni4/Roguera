package com.rogurea.main.player;

import com.rogurea.main.items.Equipment;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Position;
import com.rogurea.main.map.Room_old;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerContainer implements Serializable {

    public  String nickName;
    public Position PlayerPosition;
    public  short HP;
    public  short MP;
    public  byte Level;
    public  byte CurrentRoom;
    public  short Money;
    public  byte attempt;
    public  byte[] RandomSeed;
    public  short XP; //Experience Points
    public  short ATK;
    public  short DEF;
    public  byte DEX;
    public  short XPForNextLevel;
    public  ArrayList<Item> Inventory;
    public  HashMap<String, Equipment> Equip;
    public  PlayerStatistics playerStatistics;
    public  String SaveFileVer;
    public  byte CurrentDungeonLenght;
    public Room_old currentRoomOldObject;

    public PlayerContainer(String nickName, Position playerPosition, short HP,
                           short MP, byte level, byte currentRoom,
                           short money, byte attempt, byte[] randomSeed,
                           short XP, short ATK, short DEF, byte DEX,
                           short XPForNextLevel, ArrayList<Item> inventory, HashMap<String, Equipment> equip,
                           PlayerStatistics playerStatistics) {
        this.nickName = nickName;
        PlayerPosition = playerPosition;
        this.HP = HP;
        this.MP = MP;
        Level = level;
        CurrentRoom = currentRoom;
        Money = money;
        this.attempt = attempt;
        RandomSeed = randomSeed;
        this.XP = XP;
        this.ATK = ATK;
        this.DEF = DEF;
        this.DEX = DEX;
        this.XPForNextLevel = XPForNextLevel;
        Inventory = inventory;
        Equip = equip;
        this.playerStatistics = playerStatistics;
    }
}
