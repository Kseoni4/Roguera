/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.creatures;

import com.rogurea.base.Debug;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.items.Item;
import com.rogurea.resources.Colors;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static com.rogurea.view.ViewObjects.mapView;


public class Mob extends Creature{

    @Override
    public int getDamageByEquipment() {
        return super.getDamageByEquipment();
    }

    @Override
    public void getHit(int incomingDamage) {
        int fullDef = getDefenceByEquipment();
        int deltaDmg = incomingDamage - fullDef;
        this.HP -= Math.max(0,deltaDmg);
    }

    public Mob(int HP, String name) {
        super(HP, name);
        this.tag += ".mob";
        this.baseATK = Dungeon.getCurrentFloor().getFloorNumber();
        this.baseDEF = Dungeon.getCurrentFloor().getFloorNumber();
        this.model.changeModel(name.charAt(0));
        this.model.changeColor(Colors.RED_BRIGHT);
        initialPutInventory();
        Debug.toLog(this.toString());
    }

    @Override
    public String getName(){
        return Colors.RED_BRIGHT+this.name+Colors.R;
    }

    private void initialPutInventory(){
        this.creatureInventory.add(ItemGenerator.getRandomWeaponEquipment());
        this.creatureInventory.add(ItemGenerator.getRandomArmorEquipment());
        this.creatureInventory.add(ItemGenerator.getRandomGold());
    }

    public void dropLoot(){
        Cell[] cells = Dungeon.getCurrentRoom().getCell(this.cellPosition).getCellsAround();
        for(Item item : creatureInventory) {
            int randomCellIndex = ThreadLocalRandom.current().nextInt(8);
            cells[randomCellIndex].putIntoCell(item);
        }
        mapView.drawAround(this);
    }

    @Override
    public String toString(){
      return "[INFO]Mob: ".concat(this.getName()).concat("\n\t")
              .concat("HP:"+String.valueOf(this.getHP()))
              .concat("\n\t")
              .concat("Base ATK: " + this.baseATK)
              .concat("\n\t")
              .concat("Base DEF: " + this.baseDEF)
              .concat("\n\t")
              .concat("DMG: "+String.valueOf(this.getDamageByEquipment()))
              .concat("\n\t")
              .concat("Items in inventory:")
              .concat("\n------------------------")
              .concat("\n")
              .concat(Arrays.deepToString(this.creatureInventory.toArray()))
              .concat("\n------------------------");
    };
}
