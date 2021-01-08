package com.rogurea.main;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobController;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.SavingSystem;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.gamelogic.rgs.Events;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.KeyController;
import com.rogurea.main.player.Player;
import com.rogurea.main.player.PlayerMoveController;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.TerminalView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.rogurea.main.view.ViewObjects.*;
import static java.lang.Thread.sleep;

public class GameLoop {

    private ReentrantLock lock = new ReentrantLock();

    public final ArrayList<Thread> ActiveThreads = new ArrayList<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private boolean isGameOver = false;

    public void RestartThread(){

        if(ActiveThreads.size() > 0) {
            executorService.shutdown();
            try{
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }catch (InterruptedException e){
                Debug.log(e.getMessage());
                e.getStackTrace();
            }

            try {
                sleep(300);
            } catch (InterruptedException e) {
                Debug.log(e.getMessage());
                e.printStackTrace();
            }

            ActiveThreads.removeIf(
                    thread -> !thread.isAlive()
            );
        }
        StartThreads();
    }

    private void StartThreads(){
        executorService = Executors.newCachedThreadPool();
        int i = 0;
        for (Mob mob : Dungeon.GetCurrentRoom().RoomCreatures) {
            ActiveThreads.add(new Thread(new MobController(mob), "mobcontroller for mob " + mob.Name));
            executorService.submit(ActiveThreads.get(i));
            i++;
        }
    }

    public void Start(){
        try{
            InLoop();
        } catch (IOException e) {
            Debug.log(e.getMessage());
            e.printStackTrace();

        } finally {
            if (TerminalView.terminal != null) {
                try {
                    executorService.shutdownNow();
                    for(Thread t : ActiveThreads){
                        t.join();
                    }
                    TerminalView.terminal.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void InLoop() throws IOException {

        Debug.log("SYSTEM: Starting game loop");

        Debug.log("RANDOM SEED: " + new BigInteger(Player.RandomSeed));

        while (TerminalView.keyStroke == null || TerminalView.keyStroke.getKeyType() != KeyType.Escape) {

            if(Player.XP >= Player.XPForNextLevel)
                Events.getNewLevel();

            TerminalView.keyStroke = TerminalView.terminal.readInput();

            if(Player.HP <= 0) {
                break;
            }

            if (TerminalView.keyStroke != null) {

                if (TerminalView.keyStroke.getKeyType() == KeyType.Character) {
                    KeyController.GetKey(TerminalView.keyStroke.getCharacter());
                }

                PlayerMoveController.MovePlayer(TerminalView.keyStroke.getKeyType());

                Scans.CheckSee((Player.GetPlayerPosition().getRelative(0,1)));
            }
        }

        SavingSystem.saveGame();

        Debug.log("Ending game loop");
    }

    public void GameEndByDead(){
        if(!lock.isLocked() && !isGameOver){
            lock.lock();

            logBlock.Action(Colors.RED_BRIGHT + "are dead. GameOver.");

            logBlock.Event("Press any key to exit");

            isGameOver = !isGameOver;

            Player.playerStatistics.PlayerDead += 1;

            lock.unlock();
        }
    }

    private void RestartGame() throws IOException {
        TerminalView.terminal.flush();

        Dungeon.Rooms = new ArrayList<>();

        Player.PlayerReset();

        RestartThread();

        Dungeon.Generate();
    }
}
