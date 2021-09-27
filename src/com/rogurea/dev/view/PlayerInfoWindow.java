/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.player.Player;

public class PlayerInfoWindow extends Window{

    private final Player _player;

    public PlayerInfoWindow(Player player) {
        super(new TerminalPosition(10, 10), new TerminalSize(40,20));
        _player = player;
    }

    @Override
    protected void content() {
        /*putStringIntoWindow("Player name: " + _player.PlayerName, new Position(0,1));
        putStringIntoWindow("Player pos" + _player.playerPosition.toString(), new Position(0,5));
        putStringIntoWindow("First weapon: " + _player.Equipment.get("FirstWeapon").getName(), new Position(0,2));
        putStringIntoWindow("Second weapon: " + _player.Equipment.get("SecondWeapon"), new Position(0,3));
        putStringIntoWindow("Armor weapon: " + _player.Equipment.get("Armor"), new Position(0,4));*/
    }

    @Override
    protected void input() {
        super.input();
    }
}
