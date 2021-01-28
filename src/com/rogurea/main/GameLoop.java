package com.rogurea.main;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobController;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.SavingSystem;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.gamelogic.rgs.Events;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.KeyController;
import com.rogurea.main.player.Player;
import com.rogurea.main.player.PlayerMoveController;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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

    public boolean isGameOver = false;

    public void RestartThread(){

        if(ActiveThreads.size() > 0) {
            Debug.log("THREADS: " + ActiveThreads.size() + " Active threads, shutting down");
            executorService.shutdownNow();
            try{
                Debug.log("THREADS: Awaiting Termination...");
                executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
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
            if(!ActiveThreads.isEmpty()) {
                ActiveThreads.removeAll(ActiveThreads);
            }
        }
        StartThreads();
    }

    private void StartThreads(){
        Debug.log("THREADS: Starting mob threads");
        executorService = Executors.newCachedThreadPool();
        int i = 0;
        for (Mob mob : Dungeon.GetCurrentRoom().RoomCreatures) {
            Debug.log("THREADS: Starting mob controller thread for mob " + mob.Name);
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

        while (TerminalView.keyStroke == null || (TerminalView.keyStroke.getKeyType() != KeyType.Escape && !isGameOver)) {

            if(Player.XP >= Player.ReqXPForNextLevel)
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

        MapEditor.clearCell(Player.GetPlayerPosition());

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
