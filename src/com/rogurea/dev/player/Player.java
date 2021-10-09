/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;

import com.rogurea.dev.base.Debug;
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
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.ViewObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.rogurea.dev.view.ViewObjects.logView;

public class Player extends GameObject {

    private final String defaultName = "Player" + Calendar.getInstance().getTimeInMillis();

    public Position playerPosition = new Position();

    private PlayerData playerData;

    private byte CurrentRoom = 1;
    @Override
    public Model setModel(Model model) {
        return super.setModel(model);
    }

    public HashMap<String, Equipment> Equipment = new HashMap<>();

    public ArrayList<Item> Inventory = new ArrayList<>();

    public void putUpItem(Item item){
        Debug.toLog("Picked up item: "+item.getFullInfo());
        if(item instanceof Equipment && isGreaterStats(item)){
            autoEquip((Equipment) item);
        }
        else {
            Debug.toLog("\ninto inventory");
            Inventory.add(item);
        }
        logView.playerActionPickUp(item.getName());
        Draw.call(ViewObjects.infoGrid.getThirdBlock());
    }

    private boolean isGreaterStats(Item item){
       return ((Equipment) item).getStats().intValue() > Equipment.get("FirstWeapon").getStats().intValue();
    }

    private void autoEquip(Equipment equipment){
        removeBlank();
        if(equipment instanceof Weapon){
            if(Equipment.get("FirstWeapon") != null) {
                switchEquipment(equipment, "FirstWeapon");
            }else{
                Equipment.put("FirstWeapon", equipment);
            }
            playerData.set_atk(playerData.get_baseAtk()+Equipment.get("FirstWeapon").getStats().intValue());
        }
        else {
            Inventory.add(equipment);
        }
    }

    private void switchEquipment(Equipment toEquip, String place){
        Inventory.add(Equipment.remove(place));
        Equipment.put(place,toEquip);
    }

    private void removeBlank(){
        if(Equipment.get("FirstWeapon").getName().equals("blank"))
            Equipment.remove("FirstWeapon");
    }

    @Override
    public void placeObject(Cell cell) {
        this.playerPosition = cell.position;
        cell.gameObjects.add(this);
    }

    {
        Equipment.put("FirstWeapon", com.rogurea.dev.items.Equipment.BLANK);
        Equipment.put("SecondWeapon", com.rogurea.dev.items.Equipment.BLANK);
        Equipment.put("Armor", com.rogurea.dev.items.Equipment.BLANK);
        Equipment.put("Amulet", com.rogurea.dev.items.Equipment.BLANK);
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
