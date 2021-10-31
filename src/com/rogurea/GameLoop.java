/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.base.Debug;
import com.rogurea.exceptions.NickNameAlreadyUsed;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamelogic.SaveLoadSystem;
import com.rogurea.net.UpdaterWorker;
import com.rogurea.gamemap.*;
import com.rogurea.input.Input;
import com.rogurea.player.KeyController;
import com.rogurea.player.MoveController;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.net.JDBСQueries;
import com.rogurea.view.Draw;
import com.rogurea.view.TerminalView;
import com.rogurea.view.ViewObjects;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.rogurea.view.ViewObjects.logView;

public class GameLoop {

    public static LocalDateTime startPlayTime;
    public static LocalDateTime endPlayTime;

    public static int playTime;

    public GameLoop(){

    }
    public static ExecutorService updWrk;

    public void start() throws InterruptedException, NickNameAlreadyUsed {

        updWrk = Executors.newSingleThreadExecutor();

        startPlayTime = LocalDateTime.now();

        Debug.toLog(startPlayTime.toLocalTime().toString());
        if(Main.isNewGame()) {
            logView.playerAction("entered the dungeon... Good luck!");

            JDBСQueries.createNewUser();

            Dungeon.player.getPlayerData().setPlayerID(JDBСQueries.getNewUserId());

            Dungeon.player.putUpItem(ItemGenerator.getRandomWeaponEquipment());
        }

        JDBСQueries.createGameSession();

        updWrk.execute(new UpdaterWorker());

        updWrk.shutdown();

        Draw.call(ViewObjects.mapView);

        while (isNotEscapePressed() && isNotClosed() && !Thread.currentThread().isInterrupted()) {

            Input.waitForInput().ifPresent(keyStroke -> TerminalView.keyStroke = keyStroke);

            KeyController.getKey(TerminalView.keyStroke.getCharacter());

            MoveController.movePlayer(TerminalView.keyStroke);

            if (Dungeon.player.getPlayerData().getHP() <= 0) {

                logView.playerAction(Colors.RED_BRIGHT + "are dead. Game over!");

                logView.action(Colors.WHITE_BRIGHT + "Press enter to quit.");

                while(!Input.waitForInput().get().getKeyType().equals(KeyType.Enter)){}

                break;
            }
        }
        endPlayTime = LocalDateTime.now();

        Debug.toLog(endPlayTime.toString());

        Debug.toLog(String.valueOf(endPlayTime.minusSeconds(startPlayTime.getSecond()).getSecond()));

        calculatePlayTime();

        JDBСQueries.updateGameSession();

        System.out.println(Colors.VIOLET+"[SYSTEM]End of the game session");

        JDBСQueries.endGameSession();

        endGameSequence();
    }

    public static void endGameSequence(){
        if(Dungeon.player.getHP()  > 0) {
            try {
                SaveLoadSystem.saveGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updWrk.shutdownNow();
        Dungeon.rooms.forEach(Room::endMobAIThreads);
        Dungeon.floors.clear();
        Dungeon.rooms.clear();
        Floor.resetCounter();
        GameResources.clearResources();
        TerminalView.dispose();
        System.gc();
    }

    private void calculatePlayTime(){

        int minutes = endPlayTime.minusMinutes(startPlayTime.getMinute()).getMinute();

        if(minutes > 0){
            playTime = minutes;
            return;
        } else {
            int seconds = endPlayTime.minusSeconds(startPlayTime.getSecond()).getSecond();
            playTime = seconds;
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
