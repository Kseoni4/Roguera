package com.rogurea.main.creatures;

import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.map.Position;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameVariables;

public class Mob extends Creature {

    private short Armor;

    private final short Damage;

    public short MobLevel = Level;

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

    public Mob(String name, char mobSymbol, short HP, String _name, int roomnum) {
        super(name);
        this.MobSymbol = mobSymbol;
        this.MobLevel = Formula.GetLvlForMob(roomnum);
        this.GainXP = Formula.GetXPForMob(this.MobLevel);
        this.Damage = MobFactory.GetDamage(_name, this.MobLevel);
        this.Loot = MobFactory.GetLoot();
        this.mobBehavior = Behavior.IDLE;
        this.mobBehavior.id = super.id;
        setHP(HP);
        setCreatureType(CreatureType.MOB);
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
    public short getDamage() {
        return this.Damage;
    }

    @Override
    public short getArmor() {
        return this.Armor;
    }
}

