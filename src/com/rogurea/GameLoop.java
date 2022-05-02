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
import com.rogurea.net.PlayerDTO;
import com.rogurea.net.ServerRequests;
import com.rogurea.net.SessionData;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.utils.HashToString;
import com.rogurea.utils.SavePlayerDTO;
import com.rogurea.view.*;
import com.rogurea.workers.AutoSaveLogWorker;
import com.rogurea.workers.PlayerMovementWorker;
import com.rogurea.workers.UpdaterWorker;
import net.arikia.dev.drpc.DiscordRPC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.rogurea.Main.autoLogWorker;
import static com.rogurea.view.ViewObjects.getTrimString;
import static com.rogurea.view.ViewObjects.logView;

public class GameLoop {

    public static int playTime;

    public static ExecutorService updWrk;

    public static ExecutorService movingWrk;

    public static PlayerDTO playerDTO = new PlayerDTO();

    public static SessionData sessionData = new SessionData();

    public static UpdaterWorker updaterWorker;

    public GameLoop(){

    }

    public void start() throws InterruptedException, URISyntaxException, IOException {

        Dungeon.player.getPlayerData().updBaseATK();

        Dungeon.player.getPlayerData().updBaseDEF();

        updWrk = Executors.newSingleThreadExecutor();

        movingWrk = Executors.newSingleThreadExecutor();

        Potion.effectWrks = Executors.newCachedThreadPool();

        if(Main.isNewGame()) {
            logView.playerAction("entered the dungeon... Good luck!");

            try {
                if (Roguera.isOnline()) {
                    Long playerId = Long.parseLong(ServerRequests.createNewUser(getTrimString(Dungeon.player.getPlayerData().getPlayerName())));

                    Dungeon.player.getPlayerData().setPlayerID(playerId.intValue());

                    playerDTO.setNickName(ViewObjects.getTrimString(Dungeon.player.getName()));

                    playerDTO.setId(playerId);

                    MessageDigest hashGen = MessageDigest.getInstance("SHA-256");

                    hashGen.update(ServerRequests.getSecretKey());

                    hashGen.update(playerId.toString().getBytes(StandardCharsets.UTF_8));

                    hashGen.update(playerDTO.getNickName().getBytes(StandardCharsets.UTF_8));

                    playerDTO.setPlayerHash(HashToString.convert(hashGen.digest()));

                    Debug.toLog(playerDTO.getPlayerHash());

                    SavePlayerDTO.save(playerDTO);
                } else {
                    Dungeon.player.getPlayerData().setPlayerID(0);
                }

                Debug.toLog("[HTTP_POST][GAME_LOOP] User created and got id = "+playerDTO.getId());
            } catch (URISyntaxException | IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            Dungeon.player.putUpItem(ItemGenerator.getRandomWeaponEquipment());

            Dungeon.player.getPlayerData().updRequiredEXP();

            //Events.putTestItemIntoPos.action(new Position(3,3));
        }

        if(Main.isNewGameWithExistCharacter()){
            Dungeon.player.putUpItem(ItemGenerator.getRandomWeaponEquipment());

            Dungeon.player.getPlayerData().updRequiredEXP();
        }

        if(Roguera.isOnline()) {

            String playerName = ViewObjects.getTrimString(Dungeon.player.getName());

            int pId = Dungeon.player.getPlayerData().getPlayerID();

            Debug.toLog("[GAME_LOOP] Creating a new game session for player "+playerName);

            sessionData.setPlayerName(playerName);

            sessionData.setPlayerID(Integer.toUnsignedLong(pId));

            byte[] sessionKey = ServerRequests.createGameSession(sessionData);

            Debug.toLog("SK = "+new String(sessionKey));

            String sessionToken = HashToString.convert(sessionKey);

            sessionData.setSessionKey(sessionKey);

            sessionData.setSessionToken(sessionToken);

        } else {
            //gameSessionId = ThreadLocalRandom.current().nextInt(100000, 999999);
        }

        autoLogWorker.execute(new AutoSaveLogWorker());

        autoLogWorker.shutdown();

        if(Roguera.isOnline()) {
            updaterWorker = new UpdaterWorker(sessionData);

            updWrk.execute(updaterWorker);

            updWrk.shutdown();
        }

        Draw.call(ViewObjects.mapView);

        Draw.call(ViewObjects.infoGrid);

        movingWrk.execute(new PlayerMovementWorker());

        movingWrk.shutdown();

        while (/*isNotEscapePressed() && */isNotClosed() && !Thread.currentThread().isInterrupted()) {

            Dungeon.player.updateRichPresence();

            if(TerminalView.keyStroke.getKeyType().equals(KeyType.EOF)){
                break;
            }

            if(!isNotEscapePressed()){
                if(Roguera.isOnline()) {
                    new Message("Score of this game session will be saved on server.").show();
                } else {
                    new Message("Score of this game session will not be saved: no connection").show();
                }
                break;
            }

            if (Dungeon.player.getPlayerData().getHP() <= 0) {

                new Animation().deadAnimation(Dungeon.player);

                logView.playerAction(Colors.RED_BRIGHT + "are dead. Game over!");

                logView.action(Colors.WHITE_BRIGHT + "Press enter to quit.");

                movingWrk.shutdownNow();

                Input.waitForInput().get().getKeyType().equals(KeyType.Enter);

                if(SaveLoadSystem.deleteSaveFile()){
                    Debug.toLog("[GAME_LOOP] Save file deleted");
                }

                break;
            }

            Dungeon.player.checkNewLevel();

            DiscordRPC.discordRunCallbacks();

            TimeUnit.MILLISECONDS.sleep(100);
        }

        if(Roguera.isOnline() || Roguera.tryToConnect()) {
            if(!Roguera.isOnline()) {
                Debug.toLog("[NETWORK][GAME_SESSION] Try to create new user and GS");

                String data = ServerRequests.createNewUser(getTrimString(Dungeon.player.getPlayerData().getPlayerName()));

                Dungeon.player.getPlayerData().setPlayerID(Integer.parseInt(data.split(",")[0]));

                //ServerRequests.createGameSession();
            }

            updaterWorker.updateSessionAndInterrupt();

            ServerRequests.finalizeGameSession(sessionData);

            sessionData = new SessionData();
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

    private boolean isNotClosed(){
       return TerminalView.terminal != null;
    }

    private boolean isNotEscapePressed(){
        KeyType keyType = TerminalView.keyStroke.getKeyType();
        return keyType != KeyType.Escape;
    }
}
