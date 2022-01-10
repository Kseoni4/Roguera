/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.base.Debug;
import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamelogic.SaveLoadSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Floor;
import com.rogurea.gamemap.Room;
import com.rogurea.input.Input;
import com.rogurea.items.Potion;
import com.rogurea.net.RogueraSpring;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.view.Animation;
import com.rogurea.view.Draw;
import com.rogurea.view.TerminalView;
import com.rogurea.view.ViewObjects;
import com.rogurea.workers.AutoSaveLogWorker;
import com.rogurea.workers.PlayerMovementWorker;
import com.rogurea.workers.UpdaterWorker;
import net.arikia.dev.drpc.DiscordRPC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.rogurea.Main.autoLogWorker;
import static com.rogurea.view.ViewObjects.getTrimString;
import static com.rogurea.view.ViewObjects.logView;

public class GameLoop {

    public static LocalDateTime startPlayTime;

    public static LocalDateTime endPlayTime;

    public static int gameSessionId;

    public static int playTime;

    public static ExecutorService updWrk;

    public static ExecutorService movingWrk;

    public GameLoop(){

    }

    public void start() throws InterruptedException, URISyntaxException, IOException {

        Dungeon.player.getPlayerData().updBaseATK();

        Dungeon.player.getPlayerData().updBaseDEF();

        updWrk = Executors.newSingleThreadExecutor();

        movingWrk = Executors.newSingleThreadExecutor();

        Potion.effectWrks = Executors.newCachedThreadPool();

        startPlayTime = LocalDateTime.now();

        if(Main.isNewGame()) {
            logView.playerAction("entered the dungeon... Good luck!");

            try {
                if (Roguera.isOnline()) {
                    String map = RogueraSpring.createNewUser(getTrimString(Dungeon.player.getPlayerData().getPlayerName()));

                    String[] s = map.split(",");

                    Dungeon.player.getPlayerData().setPlayerID(Integer.parseInt(s[0]));

                    Dungeon.player.getPlayerData().setToken(s[1]);
                } else {
                    Dungeon.player.getPlayerData().setPlayerID(Math.abs(Dungeon.player.getPlayerData().getPlayerName().hashCode()));

                    Dungeon.player.getPlayerData().setToken(Dungeon.player.getPlayerData().getPlayerID() + "_offlinetoken");
                }

                Debug.toLog("[HTTP_POST][GAME_LOOP] User created and got token = "+Dungeon.player.getPlayerData().getToken());
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }

            Dungeon.player.putUpItem(ItemGenerator.getRandomWeaponEquipment());

            Dungeon.player.getPlayerData().updRequiredEXP();

            //Events.putTestItemIntoPos.action(new Position(3,3));
        }

        if(Roguera.isOnline()) {
            gameSessionId = RogueraSpring.createGameSession();
        } else {
            gameSessionId = Dungeon.player.getPlayerData().getToken().hashCode() + ThreadLocalRandom.current().nextInt(100000, 999999);
        }

        Debug.toLog("[GAME_LOOP] Created game session with id: "+gameSessionId);

        autoLogWorker.execute(new AutoSaveLogWorker());

        autoLogWorker.shutdown();

        if(Roguera.isOnline()) {
            updWrk.execute(new UpdaterWorker());

            updWrk.shutdown();
        }

        Draw.call(ViewObjects.mapView);

        Draw.call(ViewObjects.infoGrid);

        Debug.toLog("[GAME_LOOP] Starting gameloop on "+startPlayTime);

        movingWrk.execute(new PlayerMovementWorker());

        movingWrk.shutdown();

        while (isNotEscapePressed() && isNotClosed() && !Thread.currentThread().isInterrupted()) {

            Dungeon.player.updateRichPresence();

            if(TerminalView.keyStroke.getKeyType().equals(KeyType.EOF)){
                break;
            }

            if (Dungeon.player.getPlayerData().getHP() <= 0) {

                new Animation().deadAnimation(Dungeon.player);

                logView.playerAction(Colors.RED_BRIGHT + "are dead. Game over!");

                logView.action(Colors.WHITE_BRIGHT + "Press enter to quit.");

                movingWrk.shutdownNow();

                Input.waitForInput().get().getKeyType().equals(KeyType.Enter);

                break;
            }

            Dungeon.player.checkNewLevel();

            DiscordRPC.discordRunCallbacks();

            TimeUnit.MILLISECONDS.sleep(100);
        }
        endPlayTime = LocalDateTime.now();

        calculatePlayTime();

        if(Roguera.isOnline() || Roguera.tryToConnect()) {
            if(!Roguera.isOnline()) {
                Debug.toLog("[NETWORK][GAME_SESSION] Try to create new user and GS");

                String data = RogueraSpring.createNewUser(getTrimString(Dungeon.player.getPlayerData().getPlayerName()));

                Debug.toLog("[NETWORK][USER]TokenID: "+data);

                Dungeon.player.getPlayerData().setPlayerID(Integer.parseInt(data.split(",")[0]));

                RogueraSpring.createGameSession();
            }
            RogueraSpring.updateGameSession();

            RogueraSpring.finalizeGameSession();
        }

        Debug.toLog(Colors.VIOLET+"[SYSTEM]End of the game session");

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

        if(Potion.effectWrks != null){
            Debug.toLog("[SHUTDOWN] potion effect workers");
            Potion.effectWrks.shutdownNow();
        }

        Debug.toLog("[SHUTDOWN] Player moving worker");
        movingWrk.shutdownNow();

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

    public static void calculatePlayTime(){

        int minutes = LocalDateTime.now().minusMinutes(startPlayTime.getMinute()).getMinute();

        if(minutes > 0){
            playTime = minutes;
        } else {
            playTime = LocalDateTime.now().minusSeconds(startPlayTime.getSecond()).getSecond();
        }
    }

    private boolean isNotClosed(){
       return TerminalView.terminal != null;
    }

    private boolean isNotEscapePressed(){
        KeyType keyType = TerminalView.keyStroke.getKeyType();
        return keyType != KeyType.Escape;
    }
}
