package com.rogurea.dev.view;

import com.rogurea.dev.creatures.Creature;

import java.util.concurrent.TimeUnit;

import static com.rogurea.dev.view.ViewObjects.mapView;

public class Animation {

    private static final int ANIMATION_SPEED_MILLISECONDS = 200;

    private static final String[] deadAnimationSequence = {"|",";","‧","."};

    public static void deadAnimation(Creature creature){
        for(String frame : deadAnimationSequence){
            creature.model.changeModel(frame.charAt(0));
            Draw.call(mapView);
            try{
                TimeUnit.MILLISECONDS.sleep(ANIMATION_SPEED_MILLISECONDS);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
