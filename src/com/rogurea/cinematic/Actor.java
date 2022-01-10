package com.rogurea.cinematic;

import com.rogurea.base.GameObject;
import com.rogurea.creatures.Movable;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.gamemap.Room;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;

import java.util.ArrayList;

public class Actor extends GameObject implements Movable {

    private static int actorCounter = 0;

    private final ArrayList<Runnable> actionSequences;

    private final Room sceneField;

    public Actor(String name, Model actorModel, Room sceneField){
        this.id = ++actorCounter;
        this.model = actorModel;
        this.sceneField = sceneField;
        this.actionSequences = new ArrayList<>();
    }

    public void addAction(Runnable action){
        actionSequences.add(action);
    }

    public void doSequence(int index){
        actionSequences.get(index).run();
    }

    public ArrayList<Runnable> getSequences(){
        return actionSequences;
    }

    @Override
    public void moveTo(Position position) {
        sceneField.getCell(this.cellPosition).removeFromCell(this);
        sceneField.getCell(position).putIntoCell(this);
        this.cellPosition = position;
        Draw.call(ViewObjects.mapView);
    }
}
