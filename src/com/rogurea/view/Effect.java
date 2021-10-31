package com.rogurea.view;

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
