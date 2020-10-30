package com.rogurea.main.creatures;

import com.rogurea.main.map.Position;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameVariables;

public class Mob extends Creature {

    private int Armor;

    private final int Damage;

    public final int ScanZone = 5;

    public char MobSymbol;

    public Position Destination = new Position();

    public Position HisPosition = new Position();

    public int MobSpeed = GameVariables.Fast;

    public enum Behavior {
        IDLE {
            public void SetBehaviorAction(){
                currentState = "IDLE";
            }
        },
        CHASE {
            public void SetBehaviorAction(){
                System.out.println("CHASE");
                currentState = "CHASE";
            }
        },
        FIGHT {
            public void SetBehaviorAction(){
                System.out.println("FIGHT");
                currentState = "FIGHT";
            }
        },
        DEAD {
            public void SetBehaviorAction(){
                System.out.println("DEAD");
                currentState = "DEAD";
            }
        };
        public String currentState = "IDLE";

        public int id = 0;

        public abstract void SetBehaviorAction();
    }

    private Behavior mobBehavior;

    public Mob(String name, char mobSymbol, int HP) {
        super(name);
        this.MobSymbol = mobSymbol;
        setHP(HP);
        setCreatureType(CreatureType.MOB);
        this.Damage = MobFactory.GetDamage();
        this.Loot = MobFactory.GetLoot();
        this.mobBehavior = Behavior.IDLE;
        this.mobBehavior.id = super.id;
    }

    public void setMobPosition(int y, int x){
        this.HisPosition.setPosition(y,x);
    }

    public int getMobPosX() {
        return HisPosition.x;
    }

    public int getMobPosY() {
        return HisPosition.y;
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

    public void SetBehaviorId(int id){
        this.mobBehavior.id = id;
    }

    @Override
    public char getCreatureSymbol() {
        return this.MobSymbol;
    }

    @Override
    public int getDamage() {
        return this.Damage;
    }

    @Override
    public int getArmor() {
        return this.Armor;
    }
}

