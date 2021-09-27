/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.creatures;

import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.Fight;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameVariables;
import com.rogurea.main.view.Draw;

import java.util.Objects;
import java.util.Random;

import static com.rogurea.Main.gameLoop;
import static com.rogurea.main.view.ViewObjects.gameMapBlock;
import static com.rogurea.main.view.ViewObjects.logBlock;

public class MobController extends Thread {

    public MobController(Mob mob){
        this.mob = mob;
    }

    public final Mob mob;

    private final byte DropRight = 1;

    private final byte DropLeft = -1;

    private final byte DropUp = -1;

    private final byte DropDown = 1;

    private final byte[] DropPos = {DropLeft, DropRight, DropUp, DropDown};

    public void run(){
        BehaviorLoop();
        Debug.log("THREAD: Ending thread for " + mob.Name + "(" + mob.id + ")");
        System.out.println("Ending thread for " + mob.Name + "(" + mob.id + ")");
    }

    private void BehaviorLoop(){

        while(!mob.getMobBehavior().currentState.equals("DEAD") && !isInterrupted()){

            mob.getMobBehavior().SetBehaviorAction();

            if(isInterrupted()) {
                Debug.log("MOB: " + mob.Name + " controller is interrupted");
                break;
            }
            try {
                sleep(mob.MobSpeed);
            } catch (InterruptedException e) {
                Debug.log("MOB: " + mob.Name + " controller is interrupted from sleep");
                break;
            }

            if(mob.getHP() <= 0) {
                mob.SetMobBehavior(Mob.Behavior.DEAD);

                mob.getMobBehavior().SetBehaviorAction();
                Debug.log("MOB: " + mob.Name + " is dead");
            }

            if(Player.HP <= 0) {
                gameLoop.GameEndByDead();
                break;
            }

            CheckBehaviorState();
        }
    }

    private Mob GetCurrentMobFromRoom(){
        return Objects.requireNonNull(Dungeon.GetCurrentRoom().RoomCreatures.stream().filter(
                mob1 -> mob1.id == mob.id
        ).findAny().orElse(null));
    }

    private void CheckBehaviorState() {
        switch (mob.getMobBehavior().currentState){
            case "IDLE" : {
                if (MobsScan())
                    mob.SetMobBehavior(Mob.Behavior.CHASE);
                break;
            }
            case "CHASE" : {
                if (Move())
                    mob.SetMobBehavior(Mob.Behavior.FIGHT);
                else
                    mob.SetMobBehavior(Mob.Behavior.IDLE);
                break;
            }
            case "FIGHT" : {
                if(DoFight())
                    mob.SetMobBehavior(Mob.Behavior.IDLE);
                break;
            }
            case "DEAD" : {
                DropLoot();
                GiveXPToPlayer();
                RemoveMob();
                break;
            }
        }
    }

    boolean MobsScan() {
        if(!isInterrupted())
            return ScanZone();
        else{
            Debug.log("MOB: " + mob.Name + " controller is interrupted from scan");
            System.out.println(Colors.RED_BRIGHT + "Interrupted");
            return false;
        }
    }

    boolean ScanZone(){

        int y = mob.getMobPosY();

        int x = mob.getMobPosX();

        for(int Zone = 1; Zone <= mob.ScanZone; Zone++) {
            try{

                if (mob.ScanForPlayer(MapEditor.getFromCell(y+Zone, x))){
                    mob.Destination.setPosition(y+Zone, x);
                    return true;
                }
                if (mob.ScanForPlayer(MapEditor.getFromCell(y-Zone, x))){
                    mob.Destination.setPosition(y-Zone, x);
                    return true;
                }
                if (mob.ScanForPlayer(MapEditor.getFromCell(y, x+Zone))){
                    mob.Destination.setPosition(y, x+Zone);
                    return true;
                }
                if (mob.ScanForPlayer(MapEditor.getFromCell(y, x-Zone))){
                    mob.Destination.setPosition(y, x-Zone);
                    return true;
                }
            }
            catch (ArrayIndexOutOfBoundsException e){
                continue;
            }
            if(isInterrupted()){
                Debug.log("MOB: " + mob.Name + " controller is interrupted from scan zone");
                System.out.println(Colors.RED_BRIGHT + "Interrupted");
                break;
            }
        }
        return false;
    }

    boolean Move(){

        Position NewPos = mob.Destination;

        int y_s = mob.getMobPosY()+SearchPathShift(mob.getMobPosY(), NewPos.y);

        int x_s = mob.getMobPosX()+SearchPathShift(mob.getMobPosX(), NewPos.x);

        char cell = MapEditor.getFromCell(y_s, x_s);

        if(isInterrupted()){
            System.out.println(Colors.RED_BRIGHT + getName() + " " + "Interrupted");
            Debug.log("MOB: " + mob.Name + " controller is interrupted from move");
            return false;
        }

        if(cell == Player.PlayerModel)
            return true;

        if(Scans.CheckWall(cell) && !Scans.CheckCreature(cell)){
            MoveMobInPosition(mob.HisPosition, y_s, x_s);

            mob.setMobPosition(y_s, x_s);

            GetCurrentMobFromRoom().setMobPosition(y_s, x_s);

            Draw.call(gameMapBlock);
        }
        return false;
    }

    private int SearchPathShift(int mobpos, int pos){
        return Integer.compare(pos, mobpos);
    }

    private void MoveMobInPosition(Position mobpos, int y_s, int x_s){
        MapEditor.clearCell(mobpos);

        MapEditor.setIntoCell(mob.MobSymbol, y_s, x_s);
    }

    boolean DoFight(){
        if (mob.ScanForPlayer(MapEditor.getFromCell(mob.Destination))){

            mob.MobSpeed = GameVariables.FightSpeed;

            Fight.HitPlayerByMob(mob);

            mob.setHP(GetCurrentMobFromRoom().getHP());

            return false;
        }
        mob.MobSpeed = GameVariables.Fast;

        return true;
    }

    void GiveXPToPlayer(){
        Player.XP += mob.GainXP;
        logBlock.Action("get " + Colors.ORANGE + mob.GainXP + Colors.R + " XP!");
    }

    void RemoveMob(){

        Player.playerStatistics.MobKilled += 1;

        MapEditor.clearCell(mob.HisPosition);

        logBlock.Action("kill the " + mob.Name);

        Dungeon.GetCurrentRoom().RoomCreatures.removeIf(mob1 -> mob1.getMobBehavior().currentState.equals("DEAD"));

        Draw.call(gameMapBlock);
    }

    void DropLoot(){

        Random rnd = new Random();

        Position LootDropPosition = new Position();

        for(Item item : mob.Loot){

            LootDropPosition.setPosition(
                    mob.HisPosition.y+(DropPos[rnd.nextInt(4)]),
                    mob.HisPosition.x+(DropPos[rnd.nextInt(4)])
            );

            int b = 0;

            while(MapEditor.getFromCell(LootDropPosition) != ' '){
                LootDropPosition.setPosition(
                        Math.max(mob.HisPosition.y+(DropPos[rnd.nextInt(4)]),0),
                        Math.max(mob.HisPosition.x+(DropPos[rnd.nextInt(4)]),0)
                );
                if(b > 15){
                    break;
                }
                b++;
            }

            item.ItemPosition = new Position(LootDropPosition);

            Dungeon.GetCurrentRoom().RoomItems.add(item);

            Debug.log("FIGHT: Mob" + mob.Name + " drop " + item.name + " Pos: " + LootDropPosition.toString());

            MapEditor.setIntoCell(item._model, LootDropPosition);
        }
    }
}
