/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.base;

import com.rogurea.creatures.Creature;
import com.rogurea.creatures.Mob;
import com.rogurea.gamelogic.Events;
import com.rogurea.gamelogic.PathFinder;
import com.rogurea.gamemap.*;
import com.rogurea.player.Player;
import com.rogurea.resources.Colors;
import com.rogurea.view.Animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.rogurea.view.ViewObjects.logView;

public class AIController implements Runnable {

    private final Creature creature;

    private Position targetPosition;

    private Creature target;

    private final PathFinder pathFinder = new PathFinder();

    private final Room currentRoom;

    public AIController(Creature creature){
        this.creature = creature;
        this.currentRoom = Dungeon.getCurrentRoom();
    }

    @Override
    public void run() {
        behaviorLoop();
        Debug.toLog("[AI] Mob "+this.creature.getName()+" behavior loop is over. Cause by " + (isDead() ? "Mob dead" : (isTargetDead() ? " target dead" : " interrupted")));
    }

    private boolean isDead(){
        return this.creature.getHP() <= 0;
    }

    private void behaviorLoop(){
        while (!isDead() && !Thread.currentThread().isInterrupted()){
            try{
                if(fogUncover()) {
                    if (checkPlayer()) {
                        moveToTarget();
                        if (isTargetDead()) {
                            break;
                        }
                    }
                }
                idle();
            } catch (InterruptedException e){
                Debug.toLog("[AI]Creature thread ".concat(creature.getName()).concat(" has been interrupted"));
                break;
            }
        }
        if(isDead()){
            logView.playerAction("kill a "+this.creature.model.getModelColorName()+"!");

            int scoreForMob = ThreadLocalRandom.current().nextInt(3, 10) * this.creature.getDamageByEquipment();

            Dungeon.player.getPlayerData().setScore(scoreForMob);
            Dungeon.player.getPlayerData().setKill();

            Debug.toLog(Colors.VIOLET+"[SCORE] Score for mob: "+scoreForMob + " (equipment dmg "+this.creature.getDamageByEquipment()+")");

            Animation.deadAnimation(this.creature);

            ((Mob) this.creature).dropLoot();

            //Thread.currentThread().interrupt();
        }
    }

    private boolean fogUncover() {
        Cell[] cellsAround = Dungeon.getCurrentRoom().getCell(this.creature.cellPosition).getCellsAround();
        return !Arrays.stream(cellsAround).allMatch(cell -> cell.getFromCell() instanceof FogPart);
    }

    private boolean isTargetDead(){
       return this.target.getHP() <= 0;
    }

    private boolean checkPlayer(){
        ArrayList<Cell> cells = Dungeon.getCurrentRoom().getCells();

        Debug.toLog("[AI][Mob] "+creature.getName() + " finding player...");
        if(cells.stream().anyMatch(cell -> cell.getFromCell() instanceof Player)){
            target = (Creature) cells.stream().filter(cell -> cell.getFromCell() instanceof Player).findAny().get().getFromCell();
            targetPosition = target.cellPosition;
            Debug.toLog("[AI][Mob] "+creature.getName() + " Find "+target.getName()+" on position "+targetPosition.toString());
            return true;
        }
        return false;
    }

    private void moveToTarget() throws InterruptedException {
        ArrayList<Position> pathToTarget = updatePath();

        int steps = 0;
        Debug.toLog("[AI][Mob] "+creature.getName() + " moving to target...");
        while(!isTargetNear() && !isDead() && !Thread.currentThread().isInterrupted()){
            try {
                Position nextPosition = pathToTarget.get(steps);

                Debug.toLog("[AI][POS] "+steps);
                Debug.toLog("[AI][Mob] "+creature.getName() + " move to " + nextPosition);

                creature.moveTo(nextPosition);

                TimeUnit.MILLISECONDS.sleep(350);

                steps++;
                if (!isTargetNotMove() && steps >= pathToTarget.size()/2) {
                    Debug.toLog("[AI][Mob] "+creature.getName() + " target is moved, break");
                    break;
                }

            } catch (IndexOutOfBoundsException e) {
                Debug.toLog(Colors.RED_BRIGHT+"[AI][Mob] "+creature.getName() + " something goes wrong with index");
                break;
            }
        }
        try {
            if(isTargetNear()){
                Debug.toLog("[AI][Mob] "+creature.getName() + " target is near, fight!");
                Events.encounter(this.creature, this.target);
            }
        }catch (NullPointerException e) {
            Debug.toLog(Colors.RED_BRIGHT + "[AI][Mob] Exception in target finder (null)");
        }
    }

    private ArrayList<Position> updatePath(){
        return pathFinder.getPathToTarget(currentRoom, targetPosition, creature.cellPosition);
    }

    private boolean isTargetNear(){
        try {
            return Arrays.stream(currentRoom.getCell(creature.cellPosition).getCellsAround()).anyMatch(cell -> cell.getFromCell() instanceof Player);
        } catch (NullPointerException e){
            Debug.toLog(Colors.RED_BRIGHT+"[AI][Mob] Exception in target finder (null)");
            return false;
        }
    }

    private boolean isTargetNotMove(){
        return target.cellPosition.equals(targetPosition);
    }

    private boolean isTargetOnFront(Position targetPosition){
        return creature.cellPosition.getRelative(Position.FRONT).equals(targetPosition);
    }

    private void idle() throws InterruptedException {
        Debug.toLog("[AI][Mob] "+creature.getName() + " waiting...");
        TimeUnit.MILLISECONDS.sleep(400);
    }
}
