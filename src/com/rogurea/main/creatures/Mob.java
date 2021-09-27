/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.creatures;

import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.items.Gold;
import com.rogurea.main.map.Position;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameVariables;

public class Mob extends Creature {

    private final short DEFm;

    private final short ATKm;

    public short MobLevel;

    public final byte ScanZone = 5;

    public final char MobSymbol;

    public final short GainXP;

    public final Position Destination = new Position();

    public final Position HisPosition = new Position();

    public short MobSpeed = GameVariables.Fast;

    public enum Behavior {
        IDLE {
            public void SetBehaviorAction(){
                currentState = "IDLE";
            }
        },
        CHASE {
            public void SetBehaviorAction(){
                currentState = "CHASE";
            }
        },
        FIGHT {
            public void SetBehaviorAction(){
                currentState = "FIGHT";
            }
        },
        DEAD {
            public void SetBehaviorAction(){
                currentState = "DEAD";
            }
        };
        public String currentState = "IDLE";

        public short id = 0;

        public abstract void SetBehaviorAction();
    }

    private Behavior mobBehavior;

    public Mob(String name, char mobSymbol, String _name, int roomnum) {
        super(name);
        Debug.log("CREATING MOB: " + name);
        this.MobSymbol = mobSymbol;
        Debug.log("CREATING MOB: Mob Symbol is good");
        this.MobLevel = (short) Formula.GetLvlForMob(roomnum);
        Debug.log("CREATING MOB: MobLevel is good");
        this.GainXP = (short) Formula.GetXPForMob(_name, this.MobLevel);
        Debug.log("CREATING MOB: GainXP is good");
        this.ATKm = (short) Formula.GetATKForMob(_name, this.MobLevel, roomnum);
        Debug.log("CREATING MOB: ATKm is good");
        this.DEFm = (short) Formula.GetDEFForMob(_name, this.MobLevel, roomnum);
        Debug.log("CREATING MOB: DEFm is good");
        this.Loot = MobFactory.GetLoot();
        Debug.log("CREATING MOB: Loot is good");
        this.mobBehavior = Behavior.IDLE;
        this.mobBehavior.id = super.id;
        setHP((short) Formula.GetHPForMob(MobLevel));
        Debug.log("CREATING MOB: HP is good");
        setCreatureType(CreatureType.MOB);
        Debug.log("CREATING MOB: " + name + " IS CREATED");
    }

    public void setMobPosition(int y, int x){
        this.HisPosition.setPosition(y,x);
    }

    public byte getMobPosX() {
        return (byte) HisPosition.x;
    }

    public byte getMobPosY() {
        return (byte) HisPosition.y;
    }

    public boolean ScanForPlayer(char c){
        return c == Player.PlayerModel;
    }

    public Behavior getMobBehavior(){
        return mobBehavior;
    }

    public void SetMobBehavior(Behavior mb){
       this.mobBehavior = mb;
    }

    @Override
    public char getCreatureSymbol() {
        return this.MobSymbol;
    }

    @Override
    public short getATKm() {
        return this.ATKm;
    }

    @Override
    public short getDEFm() {
        return this.DEFm;
    }

    public void GetAllInfo(){

        StringBuilder MobInfo = new StringBuilder();
        MobInfo.append("Mob name: ").append(this.Name).append('\n')
                .append("HP: ").append(getHP()).append('\n')
                .append("ATK: ").append(this.ATKm).append('\n')
                .append("DEF: ").append(this.DEFm).append('\n')
                .append("Mob level: ").append(this.MobLevel).append('\n')
                .append("Have gold: ").append(
                        ((Gold) this.Loot.stream()
                                .filter(item -> item instanceof Gold)
                                .findAny().get())
                                .Amount)
                .append('\n');

        Debug.log(MobInfo.toString());
    }
}

