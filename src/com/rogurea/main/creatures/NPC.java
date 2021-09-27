/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.creatures;

import com.rogurea.main.items.Gold;
import com.rogurea.main.map.Position;

public class NPC extends Creature {

    private Position NPCPosition;

    public NPC(String name, int DefaultGoldAmount) {
        super(name);
        NPCGold = new Gold(DefaultGoldAmount);
    }

    public String firstName;

    public String lastName;

    public Gold NPCGold;

    public INPCAction NPCAction;

    public void setNPCAction(INPCAction action){
        this.NPCAction = action;
    }

    @Override
    public char getCreatureSymbol() {
        return Name.charAt(0);
    }

    public Position getNPCPosition(){
        return this.NPCPosition;
    }

    @Override
    public void setMobPosition(int x, int y) {
        NPCPosition = new Position(x, y);
    }

    @Override
    public byte getMobPosX() {
        return (byte) NPCPosition.x;
    }

    @Override
    public byte getMobPosY() {
        return (byte) NPCPosition.y;
    }

    @Override
    public short getATKm() {
        return 0;
    }

    @Override
    public short getDEFm() {
        return 0;
    }
}
