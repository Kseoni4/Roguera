package com.rogurea.dev.view;

import com.rogurea.dev.creatures.Creature;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Effect<T> implements Runnable{

    private final T target;

    private final Consumer<T> effectSequence;

    public Effect(T target, Consumer<T> effectSequence){
        this.target = target;
        this.effectSequence = effectSequence;
    }

    @Override
    public void run() {
        effectSequence.accept(target);
    }
}
