/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev;

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
import com.rogurea.dev.items.Item;
import com.rogurea.dev.player.KeyController;
import com.rogurea.dev.player.MoveController;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.Draw;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.dev.view.ViewObjects;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameLoop {
    public GameLoop(){

    }

    public void start() throws InterruptedException {

        Dungeon.player.getPlayerData().setScore(100);

        /* Message win = new Message(Colors.RED_BRIGHT,"teststtssstst\nfsddsds");

        win.show(); */

        Item item = new Item("", Model.BLANK, Item.Materials.IRON);

        
        Events.putTestItemIntoPos.action(new Position(1,2));

        Events.putChestIntoPos.action(new Position(4,3));

        ViewObjects.logView.putLog("log init_2");

        ViewObjects.logView.putLog("Very very long super string for testing");

        Random random = new Random();

        Draw.call(ViewObjects.mapView);

        try {
            while (isNotEscapePressed() && isNotClosed()) {

                if (TerminalView.keyStroke != null) {
                    TerminalView.keyStroke = TerminalView.terminal.readInput();

                    if (TerminalView.keyStroke.getKeyType() == KeyType.Character) {
                        KeyController.getKey(TerminalView.keyStroke.getCharacter());
                    }

                    MoveController.movePlayer(TerminalView.keyStroke.getKeyType());
                    Scan.checkPlayerSee(Dungeon.player.getFrontCell());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            Dungeon.rooms.forEach(Room::endMobAIThreads);
            TerminalView.dispose();
        }
    }

    private boolean isNotEscapePressed(){
        KeyType keyType = TerminalView.keyStroke.getKeyType();
        return keyType != KeyType.Escape;
    }

    private boolean isNotClosed(){
       return TerminalView.terminal != null;
    }
}
