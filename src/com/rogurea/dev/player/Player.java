package com.rogurea.dev.player;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.Model;
import com.rogurea.main.items.Equipment;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.main.player.PlayerStatistics;

import java.util.Calendar;
import java.util.HashMap;

public class Player extends GameObject {

    private String defaultName = "Player" + Calendar.getInstance().getTimeInMillis();

    public Position PlayerPosition = new Position();

    public String PlayerName = defaultName;

    @Override
    public void setModel(Model model) {
        super.setModel(model);
    }

    private short MP = 30;
    private short HP = 100;
    private byte Level = 1;
    private byte CurrentRoom = 1;
    private short Money = 0;
    private byte attempt = 0;
    private byte[] RandomSeed;

/*    public static short XP = 0; //Experience Points
    public static short ATK = GameVariables.BasePlayerATK; //Attack
    public static short DEF = GameVariables.BasePlayerDEF; //Defence
    public static byte DEX = GameVariables.BasePlayerDEX; //Dexterity
    public static short ReqXPForNextLevel = (short) GameVariables.BaseReqXP;
    public static ArrayList<Item> Inventory = new ArrayList<>();*/

    public HashMap<String, Equipment> Equip;

    @Override
    public void placeObject(Cell cell) {
        this.PlayerPosition = cell.position;
        cell.gameObjects.add(this);
    }

    public PlayerStatistics playerStatistics = new PlayerStatistics();
    {
        Equip = new HashMap<>();
        Equip.put("FirstWeapon", null);
        Equip.put("SecondWeapon", null);
        Equip.put("Armor", null);
        Equip.put("Amulet", null);
    }

    public Cell[] lookAround(){
        Cell[] cells = new Cell[8];
        int i = 0;
        for(Position direction : Position.AroundPositions){
            cells[i] = Dungeon.GetCurrentRoom().getCell(PlayerPosition.getRelative(direction));
            i++;
        }
        return cells;
    }

    public Player(){
        this.tag = "player";
        setModel(new Model("Player", Colors.GREEN_BRIGHT, GameResources.PlayerModel));
    }

    public int getHP(){
        return HP;
    }

    public int getMP(){
        return MP;
    }

    public int getLevel(){
        return Level;
    }

    public void setMP(int MP) {
        this.MP = (short) MP;
    }

    public void setHP(int HP) {
        this.HP = (short) HP;
    }

    public void setLevel(int level) {
        Level = (byte) level;
    }

    public byte getCurrentRoom() {
        return CurrentRoom;
    }

    public void setCurrentRoom(byte currentRoom) {
        CurrentRoom = currentRoom;
    }

    public int getMoney() {
        return Money;
    }

    public void setMoney(int money) {
        Money = (short) money;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = (byte) attempt;
    }

    public byte[] getRandomSeed() {
        return RandomSeed;
    }

    public void setRandomSeed(byte[] randomSeed) {
        RandomSeed = randomSeed;
    }
}
