/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamelogic;

@FunctionalInterface
public interface Event<T> {
    void action(T value);

}
