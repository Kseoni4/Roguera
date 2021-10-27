/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.base.AIController;
import com.rogurea.dev.base.Entity;
import com.rogurea.dev.creatures.Creature;
import com.rogurea.dev.creatures.Mob;
import com.rogurea.dev.gamelogic.Events;
import com.rogurea.dev.gamelogic.PathFinder;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.gamemap.Room;
import com.rogurea.dev.gamemap.Scan;
import com.rogurea.dev.input.Input;
import com.rogurea.dev.items.Item;
import com.rogurea.dev.player.KeyController;
import com.rogurea.dev.player.MoveController;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.dev.view.ViewObjects;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.rogurea.dev.view.ViewObjects.logView;

public class GameLoop {
    public GameLoop(){

    }

    public void start() throws InterruptedException {

        Events.putTestItemIntoPos.action(new Position(1, 2));

        Events.putChestIntoPos.action(new Position(4, 3));

        logView.playerAction("entered the dungeon... Good luck!");

        Draw.call(ViewObjects.mapView);

        while (isNotEscapePressed() && isNotClosed()) {

            Input.waitForInput().ifPresent(keyStroke -> TerminalView.keyStroke = keyStroke);

            KeyController.getKey(TerminalView.keyStroke.getCharacter());

            MoveController.movePlayer(TerminalView.keyStroke);

            if (Dungeon.player.getPlayerData().getHP() <= 0) {

                logView.playerAction(Colors.RED_BRIGHT + "are dead. Game over!");

                logView.playerAction(Colors.WHITE_BRIGHT + "Press enter to quit.");

                while(!Input.waitForInput().get().getKeyType().equals(KeyType.Enter)){}

                break;
            }
        }
        Dungeon.rooms.forEach(Room::endMobAIThreads);
        TerminalView.dispose();
    }

    private boolean isNotEscapePressed(){
        KeyType keyType = TerminalView.keyStroke.getKeyType();
        return keyType != KeyType.Escape;
    }

    private boolean isNotClosed(){
       return TerminalView.terminal != null;
    }
}
