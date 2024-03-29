/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.base;

import com.rogurea.creatures.Boss;
import com.rogurea.creatures.Creature;
import com.rogurea.creatures.Mob;
import com.rogurea.gamelogic.Events;
import com.rogurea.gamelogic.PathFinder;
import com.rogurea.gamemap.*;
import com.rogurea.player.Player;
import com.rogurea.resources.Colors;
import com.rogurea.view.Animation;
import com.rogurea.view.GameSound;
import com.rogurea.view.Window;
import org.openjdk.jmh.annotations.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.rogurea.view.ViewObjects.logView;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
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
        if(creature instanceof Boss){
            new GameSound("papich_laught.wav").play(2);
        }

        while (!isDead() && !Thread.currentThread().isInterrupted()){
            try{
                if(fogUncover() && !Window.isOpen()) {
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
            mobDead();
        }
    }

    private void mobDead() {
        logView.playerAction("kill a "+this.creature.model.getModelColorName()+"!");

        int scoreForMob = ThreadLocalRandom.current().nextInt(3, 10) * this.creature.getDamageByEquipment();

        Dungeon.player.getPlayerData().setScore(scoreForMob);

        Dungeon.player.getPlayerData().setKill();

        Dungeon.player.getPlayerData().setExp(((Mob) this.creature).getExperiencePoints());

        Debug.toLog(Colors.VIOLET+"[SCORE] Score for mob: "+scoreForMob + " (equipment dmg "+this.creature.getDamageByEquipment()+")");

        new GameSound("papich_sosat.wav").play();

        new Animation().deadAnimation(this.creature);

        ((Mob) this.creature).dropLoot();

        //Thread.currentThread().interrupt();
    }

    private boolean fogUncover() {
        Cell[] cellsAround = Dungeon.getCurrentRoom().getCell(this.creature.cellPosition).getCellsAround();
        return !Arrays.stream(cellsAround).allMatch(cell -> cell.getFromCell() instanceof FogPart);
    }

    private boolean isTargetDead(){
        if(target != null)
            return this.target.getHP() <= 0;
        else {
            return true;
        }
    }

    private boolean checkPlayer(){
        ArrayList<Cell> cells = Dungeon.getCurrentRoom().getCells();

        if(cells.stream().anyMatch(cell -> cell.getFromCell() instanceof Player)){
            target = (Creature) cells.stream().filter(cell -> cell.getFromCell() instanceof Player).findAny().get().getFromCell();
            targetPosition = target.cellPosition;
            return true;
        }
        return false;
    }

    private void moveToTarget() throws InterruptedException {
        long startSearchPath = Instant.now().getNano();
        ArrayList<Position> pathToTarget = updatePath();
        long endSearchPath = Instant.now().getNano();

        System.out.println("Start time ns: "+startSearchPath);
        System.out.println("End time ns: "+endSearchPath);
        System.out.println("Delta time ns: "+(endSearchPath - startSearchPath));


        int steps = 0;
        while(!Window.isOpen() && !isTargetNear() && !isDead() && !Thread.currentThread().isInterrupted()) {
            try {
                Position nextPosition = pathToTarget.get(steps);

                if(!(Dungeon.getCurrentRoom().getCell(nextPosition).getFromCell() instanceof Creature)) {
                    creature.moveTo(nextPosition);
                }

                TimeUnit.MILLISECONDS.sleep(350);

                steps++;
                if (!isTargetNotMove() && steps >= pathToTarget.size()/2) {
                    break;
                }

            } catch (IndexOutOfBoundsException e) {
                Debug.toLog(Colors.RED_BRIGHT+"[AI][Mob] "+creature.getName() + " something goes wrong with index ");
                break;
            }
        }
        try {
            if(isTargetNear()){
                Events.encounter(this.creature, this.target);
            }
        }catch (NullPointerException e) {
            Debug.toLog(Colors.RED_BRIGHT + "[AI][Mob] Exception in target finder (null)");
        }
    }

    @Benchmark
    public ArrayList<Position> updatePath(){
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
        //Debug.toLog("[AI][Mob] "+creature.getName() + " waiting...");
        TimeUnit.MILLISECONDS.sleep(150);
    }
}
