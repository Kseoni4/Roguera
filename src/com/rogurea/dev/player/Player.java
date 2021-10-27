/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.player;

import com.rogurea.dev.creatures.Creature;
import com.rogurea.dev.base.Debug;
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

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.rogurea.dev.view.ViewObjects.infoGrid;
import static com.rogurea.dev.view.ViewObjects.logView;

public class Player extends Creature {

    private final String defaultName = "Player" + ThreadLocalRandom.current().nextInt(1,10000);

    public Position playerPosition = new Position();

    private PlayerData playerData;

    private byte CurrentRoom = 1;

    @Override
    public Model setModel(Model model) {
        return super.setModel(model);
    }

    public HashMap<String, Equipment> Equipment = new HashMap<>();

    public ArrayList<Item> Inventory = new ArrayList<>();

    public final ArrayList<Equipment> quickEquipment = new ArrayList<>(5);

    public void putUpItem(Item item){
        Debug.toLog("Picked up item: "+item.getFullInfo());
        if(item instanceof Equipment && isGreaterStats(item)){
            autoEquip((Equipment) item);
        }
        else {
            Debug.toLog("\ninto inventory");
            putIntoInventory(item);
        }
        logView.playerActionPickUp(item.getName());
        Draw.call(ViewObjects.infoGrid.getThirdBlock());
    }

    private boolean isGreaterStats(Item item){
       return ((Equipment) item).getStats().intValue() > getEquipmentFromSlot("FirstWeapon").orElse(com.rogurea.dev.items.Equipment.BLANK).getStats().intValue();
    }

    private void autoEquip(Equipment equipment){
        //removeBlank();
        if(equipment instanceof Weapon){
            if(getEquipmentFromSlot("FirstWeapon").isPresent()) {
                switchEquipment(equipment, "FirstWeapon");
            }else{
                putIntoEquipment(equipment, "FirstWeapon");
            }
        }
        else {
            Debug.toLog("\ninto inventory");
            putIntoInventory(equipment);
        }
    }

    private void putIntoInventory(Item item){
        if(quickEquipment.toArray().length < 5){
            quickEquipment.add((Equipment) item);
        } else {
            Inventory.add(item);
        }
    }

    public Optional<Equipment> getEquipmentFromSlot(String key){
          return Optional.ofNullable(Equipment.get(key));
    }

    public void equipItemIntoFirstSlot(Equipment eq){
        switchEquipment(eq, "FirstWeapon");
    }

    public void equipItemFromQuickSlot(Equipment eq){
        int index = quickEquipment.indexOf(eq);
        quickEquipment.remove(eq);
        quickEquipment.add(index, Equipment.remove("FirstWeapon"));
        putIntoEquipment(eq, "FirstWeapon");
    }

    private void switchEquipment(Equipment toEquip, String place){
        Inventory.add(Equipment.remove(place));
        putIntoEquipment(toEquip, place);
    }

    private void putIntoEquipment(Equipment equipment, String place){
        Equipment.put(place, equipment);
        playerData.recountStats(equipment.equipmentStat, equipment.getStats());
        Draw.call(infoGrid.getFirstBlock());
    }

    private void removeBlank(){
        if(!Equipment.get("FirstWeapon").getName().equals("blank"))
            Equipment.remove("FirstWeapon", com.rogurea.dev.items.Equipment.BLANK);
    }

    @Override
    public void placeObject(Cell cell) {
        this.playerPosition = cell.position;
        this.cellPosition = this.playerPosition;
        cell.gameObjects.add(this);
    }

    {
        Equipment.put("FirstWeapon", null);
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

    @Override
    protected Equipment findEquipmentInInventoryByTag(String tag){
        try {
            return this.Equipment.values().stream().filter(eq -> eq.tag.startsWith(tag)).findFirst().get();
        } catch (NoSuchElementException | NullPointerException e){
            return com.rogurea.dev.items.Equipment.BLANK;
        }
    }
    public Player(){
        super();
        this.tag += ".player";
        try {
            playerData = new PlayerData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        playerData.setPlayerName(defaultName);
        setModel(new Model("Player", Colors.GREEN_BRIGHT, GameResources.PLAYER_MODEL));
        playerData.setHP(100);
        this.name = playerData.getPlayerName();
        this.cellPosition = this.playerPosition;
    }

    @Override
    public int getHP(){
        return playerData.getHP();
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

    @Override
    public void getHit(int incomingDamage){
        int fullDef = this.playerData.get_baseDef() + this.playerData.get_def();
        int deltaDmg = incomingDamage - fullDef;
        this.playerData.setHP(this.playerData.getHP() - Math.max(0, deltaDmg));
    }

}
