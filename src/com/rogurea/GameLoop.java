/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.net.RogueraSpring;
import com.rogurea.workers.AutoSaveLogWorker;
import com.rogurea.base.Debug;
import com.rogurea.exceptions.NickNameAlreadyUsed;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamelogic.SaveLoadSystem;
import com.rogurea.workers.UpdaterWorker;
import com.rogurea.gamemap.*;
import com.rogurea.input.Input;
import com.rogurea.player.KeyController;
import com.rogurea.player.MoveController;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.view.Draw;
import com.rogurea.view.TerminalView;
import com.rogurea.view.ViewObjects;
import net.arikia.dev.drpc.DiscordRPC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.rogurea.Main.autoLogWorker;
import static com.rogurea.view.ViewObjects.getTrimString;
import static com.rogurea.view.ViewObjects.logView;

public class GameLoop {

    public static LocalDateTime startPlayTime;

    public static LocalDateTime endPlayTime;

    public static int gameSessionId;

    public static int playTime;

    public GameLoop(){

    }
    public static ExecutorService updWrk;

    public void start() throws InterruptedException, URISyntaxException, IOException {

        Dungeon.player.getPlayerData().updBaseATK();

        updWrk = Executors.newSingleThreadExecutor();

        startPlayTime = LocalDateTime.now();

        Debug.toLog(startPlayTime.toLocalTime().toString());

        if(Main.isNewGame()) {
            logView.playerAction("entered the dungeon... Good luck!");

            try {
               String map = RogueraSpring.createNewUser(getTrimString(Dungeon.player.getPlayerData().getPlayerName()));

               String[] s = map.split(",");

               Dungeon.player.getPlayerData().setPlayerID(Integer.parseInt(s[0]));

               Dungeon.player.getPlayerData().setToken(s[1]);

               //Debug.toLog("[HTTP_POST][GAME_LOOP] User created and got token = "+s[1]);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }

            Dungeon.player.putUpItem(ItemGenerator.getRandomWeaponEquipment());

            Dungeon.player.getPlayerData().updRequiredEXP();

            //Events.putTestItemIntoPos.action(new Position(3,3));
        }

        gameSessionId = RogueraSpring.createGameSession();

        Debug.toLog("[GAME_LOOP] Created game session with id: "+gameSessionId);

        autoLogWorker.execute(new AutoSaveLogWorker());

        autoLogWorker.shutdown();

        updWrk.execute(new UpdaterWorker());

        updWrk.shutdown();

        Draw.call(ViewObjects.mapView);

        Debug.toLog("[GAME_LOOP] Starting gameloop on "+startPlayTime);

        while (isNotEscapePressed() && isNotClosed() && !Thread.currentThread().isInterrupted()) {

            Dungeon.player.updateRichPresence();

            Input.waitForInput().ifPresent(keyStroke -> TerminalView.keyStroke = keyStroke);

            if(TerminalView.keyStroke.getKeyType().equals(KeyType.EOF)){
                break;
            }

            KeyController.getKey(TerminalView.keyStroke.getCharacter());

            MoveController.movePlayer(TerminalView.keyStroke);

            if (Dungeon.player.getPlayerData().getHP() <= 0) {

                logView.playerAction(Colors.RED_BRIGHT + "are dead. Game over!");

                logView.action(Colors.WHITE_BRIGHT + "Press enter to quit.");

                Input.waitForInput().get().getKeyType().equals(KeyType.Enter);

                break;
            }

            Dungeon.player.checkNewLevel();

            DiscordRPC.discordRunCallbacks();
        }
        endPlayTime = LocalDateTime.now();

        Debug.toLog(endPlayTime.toString());

        Debug.toLog(String.valueOf(endPlayTime.minusSeconds(startPlayTime.getSecond()).getSecond()));

        calculatePlayTime();

        RogueraSpring.updateGameSession();

        Debug.toLog(Colors.VIOLET+"[SYSTEM]End of the game session");

        RogueraSpring.finalizeGameSession();

        endGameSequence();
    }

    public static void endGameSequence(){
        Debug.toLog("[SYSTEM] Starting shutdown sequence");

        if(Dungeon.player != null && Dungeon.player.getHP()  > 0) {
            try {
                Debug.toLog("[SHUTDOWN] Saving game...");
                SaveLoadSystem.saveGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(updWrk != null) {
            Debug.toLog("[SHUTDOWN] Update game session worker");
            updWrk.shutdownNow();
        }

        if(autoLogWorker != null) {
            Debug.toLog("[SHUTDOWN] Auto log worker");
            autoLogWorker.shutdownNow();
            try {
                autoLogWorker.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(Dungeon.rooms != null && Dungeon.floors != null) {
            Debug.toLog("[SHUTDOWN] End mob threads");
            Dungeon.rooms.forEach(Room::endMobAIThreads);

            Debug.toLog("[SHUTDOWN] Clear rooms and floors");

            Dungeon.rooms.forEach(room -> {
                room.getObjectsSet().clear();
                room.getCells().clear();
            });

            Dungeon.floors.clear();

            Dungeon.rooms.clear();
        }

        Debug.toLog("[SHUTDOWN] Reset dungeon variables");
        Dungeon.resetVariables();

        Debug.toLog("[SHUTDOWN] Reset floor counter");
        Floor.resetCounter();

        Debug.toLog("[SHUTDOWN] Clear game resources");
        GameResources.clearResources();

        Debug.toLog("[SHUTDOWN] Dispose terminal");
        TerminalView.dispose();

        Debug.toLog("[SHUTDOWN] Calling for garbage collector");
        System.gc();
    }

    private void calculatePlayTime(){

        int minutes = endPlayTime.minusMinutes(startPlayTime.getMinute()).getMinute();

        if(minutes > 0){
            playTime = minutes;
        } else {
            playTime = endPlayTime.minusSeconds(startPlayTime.getSecond()).getSecond();
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
