package com.rogurea.dev.player;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.items.Weapon;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.items.Equipment;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.main.player.PlayerStatistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Player extends GameObject {

    private final String defaultName = "Player" + Calendar.getInstance().getTimeInMillis();

    public Position playerPosition = new Position();

    private PlayerData playerData;

    private byte CurrentRoom = 1;
    @Override
    public Model setModel(Model model) {
        return super.setModel(model);
    }



/*    public static short XP = 0; //Experience Points
    public static short ATK = GameVariables.BasePlayerATK; //Attack
    public static short DEF = GameVariables.BasePlayerDEF; //Defence
    public static byte DEX = GameVariables.BasePlayerDEX; //Dexterity
    public static short ReqXPForNextLevel = (short) GameVariables.BaseReqXP;
    public static ArrayList<Item> Inventory = new ArrayList<>();*/

    public HashMap<String, Equipment> Equipment = new HashMap<>();

    public ArrayList<Item> Inventory = new ArrayList<>();

    public void putUpItem(Item item){
        if(item instanceof Equipment){
            autoEquip((Equipment) item);
        }
        else {
            Inventory.add(item);
        }
    }

    private void autoEquip(Equipment equipment){
        if(equipment instanceof Weapon){
            Equipment.put("FirstWeapon", equipment);
        }
        else {
            Inventory.add(equipment);
        }
    }

    @Override
    public void placeObject(Cell cell) {
        this.playerPosition = cell.position;
        cell.gameObjects.add(this);
    }

    public PlayerStatistics playerStatistics = new PlayerStatistics();
    {
        Equipment.put("FirstWeapon", null);
        Equipment.put("SecondWeapon", null);
        Equipment.put("Armor", null);
        Equipment.put("Amulet", null);
    }

    public Cell getFrontCell(){
        return Dungeon.getCurrentRoom().getCell(playerPosition.getRelative(0,1));
    }

    public Cell[] lookAround(){
        Cell[] cells = new Cell[8];
        int i = 0;
        for(Position direction : Position.AroundPositions){
            cells[i] = Dungeon.getCurrentRoom().getCell(playerPosition.getRelative(direction));
            i++;
        }
        return cells;
    }

    public Player(){
        this.tag = "player";
        playerData = new PlayerData();
        playerData.setPlayerName(defaultName);
        setModel(new Model("Player", Colors.GREEN_BRIGHT, GameResources.PLAYER_MODEL));
    }

    public PlayerData getPlayerData(){
        return playerData;
    }

    public byte getCurrentRoom() {
        return CurrentRoom;
    }

    public void setCurrentRoom(byte currentRoom) {
        CurrentRoom = currentRoom;
    }

}
