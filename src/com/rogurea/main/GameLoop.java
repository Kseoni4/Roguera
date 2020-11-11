package com.rogurea.main;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobController;
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
import java.util.ArrayList;
import static com.rogurea.main.resources.ViewObject.*;
import static java.lang.Thread.sleep;

public class GameLoop {

    public final ArrayList<Thread> ActiveThreads = new ArrayList<>();

    private boolean isGameOver = false;

    public void RestartThread(){

        if(ActiveThreads.size() > 0) {
            for (Thread t : ActiveThreads) {
                t.interrupt();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ActiveThreads.removeIf(
                    thread -> !thread.isAlive()
            );
        }
        StartThreads();
    }

    private void StartThreads(){
        for (Mob mob : Dungeon.CurrentRoomCreatures) {
            ActiveThreads.add(new Thread(new MobController(mob), "mobcontroller for mob " + mob.Name));
        }

        ActiveThreads.forEach(Thread::start);
    }

    public void Start(){
        try{
            InLoop();
        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            if (TerminalView.terminal != null) {
                try {
                    for(Thread t : ActiveThreads){
                        t.interrupt();
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

                Scans.CheckSee(MapEditor.getFromCell(Player.Pos.y + 1, Player.Pos.x));
            }
        }
    }

    public void GameEndByDead(){
        if(!isGameOver){
            logBlock.Action(Colors.RED_BRIGHT + "are dead. GameOver.");

            logBlock.Event("Press any key to exit");
            isGameOver = !isGameOver;
        }
    }

    private void RestartGame() throws IOException {
        TerminalView.terminal.flush();

        Dungeon.Rooms = new ArrayList<>();

        Dungeon.CurrentRoomCreatures = new ArrayList<>();

        Player.PlayerReset();

        RestartThread();

        Dungeon.Generate();
    }
}
