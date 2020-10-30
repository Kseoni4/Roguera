package com.rogurea.main.creatures;

import com.rogurea.main.gamelogic.Fight;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameVariables;

import java.util.Objects;
import java.util.Random;

public class MobController extends Thread {

    public MobController(Mob mob){
        this.mob = mob;
    }
    public final Mob mob;

    private static final int DropRight = 1;

    private static final int DropLeft = -1;

    private static final int DropUp = -1;

    private static final int DropDown = 1;

    private static final int[] DropPos = {DropLeft, DropRight, DropUp, DropDown};

    public void run(){
        BehaviorLoop();
        System.out.println("Ending thread for " + mob.Name + "(" + mob.id + ")");
    }

    private void BehaviorLoop(){

        while(!mob.getMobBehavior().currentState.equals("DEAD") && !isInterrupted()){

            mob.getMobBehavior().SetBehaviorAction();

            mob.setHP(Dungeon.GetCurrentRoom().RoomCreatures.stream().filter(
                    mob1 -> mob1.id == mob.id
            ).findAny().orElse(null).getHP());

            try {
                sleep(mob.MobSpeed);
            } catch (InterruptedException e) {
                break;
            }

            if(isInterrupted())
                break;

            if(mob.getHP() <= 0) {

                mob.SetMobBehavior(Mob.Behavior.DEAD);

                mob.getMobBehavior().SetBehaviorAction();
            }

            if(Player.HP <= 0) {
                break;
            }

            CheckBehaviorState();
        }
    }

    private void CheckBehaviorState() {
        switch (mob.getMobBehavior().currentState){
            case "IDLE" -> {
                if (MobsScan())
                    mob.SetMobBehavior(Mob.Behavior.CHASE);
            }
            case "CHASE" -> {
                if (Move())
                    mob.SetMobBehavior(Mob.Behavior.FIGHT);
                else
                    mob.SetMobBehavior(Mob.Behavior.IDLE);
            }
            case "FIGHT" -> {
                if(DoFight())
                    mob.SetMobBehavior(Mob.Behavior.IDLE);
            }
            case "DEAD" -> {
                DropLoot();
                RemoveMob();
            }
        }
    }

    boolean MobsScan() {
        if(!isInterrupted())
            return ScanZone();
        else{
            System.out.println(Colors.RED_BRIGHT + "Interrupted");
            interrupt();
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
                return false;
            }
            if(currentThread().isInterrupted()){
                System.out.println(Colors.RED_BRIGHT + "Interrupted");
                break;
            }
        }
        return false;
    }

    boolean Move(){

        Position NewPos = mob.Destination;

        char cell;

        int y_s;

        int x_s;

        y_s = mob.getMobPosY()+SearchPathShift(mob.getMobPosY(), NewPos.y);

        x_s = mob.getMobPosX()+SearchPathShift(mob.getMobPosX(), NewPos.x);

        cell = MapEditor.getFromCell(y_s, x_s);

        if(isInterrupted()){
            System.out.println(Colors.RED_BRIGHT + "Interrupted");
            return false;
        }

        if(cell == Player.PlayerModel)
            return true;

        if(Scans.CheckWall(cell) && !Scans.CheckCreature(cell)){
            MoveMobInPosition(mob.HisPosition, y_s, x_s);

            mob.setMobPosition(y_s, x_s);

            Objects.requireNonNull(Dungeon.GetCurrentRoom().RoomCreatures.stream().filter(
                    mob1 -> mob1.id == mob.id
            ).findAny().orElse(null)).setMobPosition(y_s, x_s);
        }
        return false;
    }

    private int SearchPathShift(int mobpos, int pos){
        return Integer.compare(pos, mobpos);
    }

    private void MoveMobInPosition(Position mobpos, int y_s, int x_s){
        MapEditor.setIntoCell('.', mobpos.y, mobpos.x);

        MapEditor.setIntoCell(mob.MobSymbol, y_s, x_s);
    }

    boolean DoFight(){
        if (mob.ScanForPlayer(MapEditor.getFromCell(mob.Destination))){

            mob.MobSpeed = GameVariables.FightSpeed;

            Fight.HitPlayer(mob);

            return false;
        }
        mob.MobSpeed = GameVariables.Fast;

        return true;
    }

    void RemoveMob(){

        MapEditor.clearCell(mob.HisPosition);

        Dungeon.CurrentRoomCreatures.removeIf(mob1 -> mob1.getMobBehavior().currentState.equals("DEAD"));

        Objects.requireNonNull(Dungeon.Rooms
                .stream().filter(
                        room -> room.NumberOfRoom == Player.CurrentRoom
                )
                .findAny()
                .orElse(null))
                .RoomCreatures.removeIf(mob1 -> mob1.getMobBehavior().currentState.equals("DEAD"));
    }

    void DropLoot(){

        Random rnd = new Random();

        for(Item item : mob.Loot){
            Position lootdrop = new Position();
            Dungeon.GetCurrentRoom().RoomItems.add(item);

            lootdrop.setPosition(
                    mob.HisPosition.y+(DropPos[rnd.nextInt(4)]),
                    mob.HisPosition.x+(DropPos[rnd.nextInt(4)])
            );

            int b = 0;

            while(MapEditor.getFromCell(lootdrop) != ' '){
                lootdrop.setPosition(
                        Math.max(mob.HisPosition.y+(DropPos[rnd.nextInt(4)]),0),
                        Math.max(mob.HisPosition.x+(DropPos[rnd.nextInt(4)]),0)
                );
                if(b > 10){
                    break;
                }
                b++;
            }
            MapEditor.setIntoCell(item._model, lootdrop);

        }
    }
}
