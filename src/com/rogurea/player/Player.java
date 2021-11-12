/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.player;

import com.rogurea.GameLoop;
import com.rogurea.creatures.Creature;
import com.rogurea.base.Debug;
import com.rogurea.gamelogic.RogueraGameSystem;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.items.*;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.Model;
import com.rogurea.gamemap.Position;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.rogurea.view.ViewObjects.infoGrid;
import static com.rogurea.view.ViewObjects.logView;

public class Player extends Creature {

    transient private DiscordRichPresence playerRichPresence;

    private final String defaultName = "Player" + ThreadLocalRandom.current().nextInt(1,10000);

    public Position playerPosition = new Position();

    private PlayerData playerData;

    private byte currentRoom = 1;

    private String pname;

    @Override
    public Model setModel(Model model) {
        return super.setModel(model);
    }

    public HashMap<String, Equipment> Equipment = new HashMap<>();

    public ArrayList<Item> Inventory = new ArrayList<>();

    public ArrayList<Equipment> quickEquipment = new ArrayList<>(5);

    public void putUpItem(Item item){
        //Debug.toLog("[PLAYER]Picked up item: "+item.toString());
        if(item instanceof Equipment){
            autoEquip((Equipment) item);
        }
        else if (item instanceof Gold){
            getPlayerData().setMoney(((Gold) item).getAmount());
        }
        else {
            //Debug.toLog("\ninto inventory");
            putIntoInventory(item);
        }
        logView.playerActionPickUp(item.getName());
        Draw.call(infoGrid.getFirstBlock());
        Draw.call(ViewObjects.infoGrid.getThirdBlock());
    }

    private boolean isGreaterStats(Item item, String key){
       return ((Equipment) item).getStats() > getEquipmentFromSlot(key).orElse(com.rogurea.items.Equipment.BLANK).getStats();
    }

    private void autoEquip(Equipment equipment){
        //removeBlank();
        if(equipment instanceof Weapon){
            if(getEquipmentFromSlot("FirstWeapon").isPresent() && isGreaterStats(equipment, "FirstWeapon")) {
                switchEquipment(equipment, "FirstWeapon");
            }else if(getEquipmentFromSlot("FirstWeapon").isEmpty()) {
                putIntoEquipment(equipment, "FirstWeapon");
            } else {
                //Debug.toLog("\ninto inventory");
                putIntoInventory(equipment);
            }
        }
        else if(equipment instanceof Armor){
            if(getEquipmentFromSlot("Armor").isPresent() && isGreaterStats(equipment, "Armor")){
                switchEquipment(equipment, "Armor");
            } else if(getEquipmentFromSlot("Armor").isEmpty()) {
                putIntoEquipment(equipment, "Armor");
            } else {
                //Debug.toLog("\ninto inventory");
                putIntoInventory(equipment);
            }
        } else {
            //Debug.toLog("\ninto inventory");
            putIntoInventory(equipment);
        }
    }

    private void putIntoInventory(Item item){
        if(quickEquipment.toArray().length < 5){
            if(item instanceof Potion)
                quickEquipment.add((Equipment) item);
            else {
                Inventory.add(item);
            }
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
            Equipment.remove("FirstWeapon", com.rogurea.items.Equipment.BLANK);
    }

    @Override
    public void placeObject(Cell cell) {
        this.playerPosition = cell.position;
        this.cellPosition = this.playerPosition;
        cell.gameObjects.add(this);
    }

    {
        Equipment.put("FirstWeapon", null);
        Equipment.put("Armor", null);
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
            return com.rogurea.items.Equipment.BLANK;
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

        playerRichPresence = new DiscordRichPresence.Builder("Ready to crawling dungeon")
                .setStartTimestamps(Instant.now().getEpochSecond()).setBigImage("icon","Roguera").build();

        pname = ViewObjects.getTrimString(playerData.getPlayerName());
    }

    public Player(PlayerData playerData){
        super();

        this.tag += ".player";

        setModel(new Model("Player", Colors.GREEN_BRIGHT, GameResources.PLAYER_MODEL));

        this.setPlayerData(playerData);

        this.playerPosition = playerData.getPlayerPositionData();

        this.HP = playerData.getHP();

        this.currentRoom = (byte) playerData.getCurrentRoom().roomNumber;

        this.name = playerData.getPlayerName();

        this.cellPosition = playerData.getPlayerPositionData();

        this.playerPosition = playerData.getPlayerPositionData();

        this.Inventory = getPlayerData().getPlayerInventory();

        this.Equipment = getPlayerData().getPlayerEquipment();

        this.quickEquipment = getPlayerData().getPlayerQuickEquipment();

        playerRichPresence = new DiscordRichPresence.Builder("Ready to crawling dungeon")
                .setStartTimestamps(Instant.now().getEpochSecond()).setBigImage("icon","Roguera").build();

        this.pname = this.name;
    }

    @Override
    public String getName(){
        return this.playerData.getPlayerName();
    }

    @Override
    public int getHP(){
        return playerData.getHP();
    }

    public PlayerData getPlayerData(){
        return playerData;
    }

    public byte getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(byte currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void updateRichPresence(){
        pname = ViewObjects.getTrimString(playerData.getPlayerName());
        playerRichPresence.details = pname+"(LVL "+playerData.getLevel()+")"+" is crawling dungeon";
        playerRichPresence.state = "Floor "+Dungeon.getCurrentFloor().get().getFloorNumber() + "|" + " Room " +currentRoom;
        DiscordRPC.discordUpdatePresence(playerRichPresence);
    }

    @Override
    public void getHit(int incomingDamage){
        int fullDef = getDefenceByEquipment();
        int deltaDmg = incomingDamage - fullDef;
        this.playerData.setHP(this.playerData.getHP() - Math.max(0, deltaDmg));
    }

    @Override
    public int getDamageByEquipment(){
        return this.playerData.get_baseAtk() + this.getPlayerData().get_atk() + this.getPlayerData().get_atkPotionBonus(); //+ findEquipmentInInventoryByTag("item.equipment.weapon.").getStats();
    }

    @Override
    public int getDefenceByEquipment(){
        return this.playerData.get_baseDef() + this.getPlayerData().get_def() + this.getPlayerData().get_defPotionBonus(); //+ findEquipmentInInventoryByTag("item.equipment.armor.").getStats();
    }

    public void checkNewLevel(){
        if(this.playerData.getExp() >= this.playerData.getRequiredEXP()){
            this.playerData.setLevel(this.playerData.getLevel() + 1);
            this.playerData.updBaseATK();
            this.playerData.updRequiredEXP();
            this.playerData.updBaseDEF();
            this.playerData.setMaxHP(RogueraGameSystem.getNextHP());

            logView.playerAction("get the new level!");
            Draw.call(infoGrid.getFirstBlock());
        }
    }

    public void setPlayerData(PlayerData playerData){
        this.playerData = playerData;
    }

}
