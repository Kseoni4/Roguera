/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.dev.base;

import com.rogurea.dev.creatures.Creature;
import com.rogurea.dev.gamelogic.Events;
import com.rogurea.dev.gamelogic.PathFinder;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.gamemap.Room;
import com.rogurea.dev.player.Player;
import com.rogurea.dev.resources.Colors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AIController implements Runnable {

    private Creature creature;

    private Position targetPosition;

    private Creature target;

    private PathFinder pathFinder = new PathFinder();

    private Room currentRoom;

    public AIController(Creature creature){
        this.creature = creature;
        this.currentRoom = Dungeon.getCurrentRoom();
    }

    @Override
    public void run() {
        behaivorLoop();
    }

    private boolean isDead(){
        return creature.getHP() <= 0;
    }

    private void behaivorLoop(){
        while (!isDead() || !Thread.currentThread().isInterrupted()){
            try{
                if(checkPlayer()){
                    moveToTarget();
                    if(isTargetDead()){
                        break;
                    }
                }
                idle();
            } catch (InterruptedException e){
                Debug.toLog("Creature thread ".concat(creature.getName()).concat(" has been interrupted"));
                break;
            }
        }
    }

    private boolean isTargetDead(){
       return this.target.getHP() <= 0;
    }

    private boolean checkPlayer(){
        ArrayList<Cell> cells = Dungeon.getCurrentRoom().getCells();

        Debug.toLog("[Mob] "+creature.getName() + " finding player...");
        if(cells.stream().anyMatch(cell -> cell.getFromCell() instanceof Player)){
            target = (Creature) cells.stream().filter(cell -> cell.getFromCell() instanceof Player).findAny().get().getFromCell();
            targetPosition = target.cellPosition;
            Debug.toLog("[Mob] "+creature.getName() + " Find "+target.getName()+" on position "+targetPosition.toString());
            return true;
        }
        return false;
    }

    private void moveToTarget() throws InterruptedException {
        ArrayList<Position> pathToTarget = updatePath();

        int pos = 0;
        Debug.toLog("[Mob] "+creature.getName() + " moving to target...");
        while(!isTargetNear()){
            try {
                Position nextPosition = pathToTarget.get(pos);

                Debug.toLog("[POS] "+pos);
                Debug.toLog("[Mob] "+creature.getName() + " move to " + nextPosition);

                creature.moveTo(nextPosition);

                TimeUnit.MILLISECONDS.sleep(350);

                pos++;
                if (!isTargetNotMove() && pos >= pathToTarget.size()/2) {
                    Debug.toLog("[Mob] "+creature.getName() + " target is moved, break");
                    break;
                }

            } catch (IndexOutOfBoundsException e) {
                Debug.toLog(Colors.RED_BRIGHT+"[Mob] "+creature.getName() + " something goes wrong with index");
                break;
            }
        }
        try {
            if(isTargetNear()){
                Debug.toLog("[Mob] "+creature.getName() + " target is near, fight!");
                Events.encounter(this.creature, this.target);
            }
        }catch (NullPointerException e) {
            Debug.toLog(Colors.RED_BRIGHT + "[Mob] Exception in target finder (null)");
        }
    }

    private ArrayList<Position> updatePath(){
        return pathFinder.getPathToTarget(currentRoom, targetPosition, creature.cellPosition);
    }

    private boolean isTargetNear(){
        try {
            return Arrays.stream(currentRoom.getCell(creature.cellPosition).getCellsAround()).anyMatch(cell -> cell.getFromCell() instanceof Player);
        } catch (NullPointerException e){
            Debug.toLog(Colors.RED_BRIGHT+"[Mob] Exception in target finder (null)");
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
        Debug.toLog("[Mob] "+creature.getName() + " waiting...");
        TimeUnit.MILLISECONDS.sleep(400);
    }
}
