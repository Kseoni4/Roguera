package com.rogurea.main;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.creatures.MobController;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Room;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.KeyController;
import com.rogurea.main.player.Player;
import com.rogurea.main.player.PlayerMoveController;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.LogBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.rogurea.main.player.Player.CurrentRoom;
import static com.rogurea.main.player.Player.PlayerModel;
import static java.lang.Thread.sleep;

public class GameLoop {

    public static final Thread drawcall = new Thread(new TerminalView(), "drawcall");

    public static final ArrayList<Thread> ActiveThreads = new ArrayList<>();

    private static void RestartThread(){

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
            StartThreads();
        }
        else{
            StartThreads();
        }
    }

    private static void StartThreads(){
        for (Mob mob : Dungeon.CurrentRoomCreatures) {
            ActiveThreads.add(new Thread(new MobController(mob), "mobcontroller for mob " + mob.Name));
        }

        ActiveThreads.forEach(Thread::start);
    }

    public static void Start(){
        try{
            TerminalView.InitTerminal();
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
                    /*drawcall.interrupt();
*/                    TerminalView.terminal.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void InLoop() throws IOException {

        drawcall.start();

        while (TerminalView.keyStroke == null || TerminalView.keyStroke.getKeyType() != KeyType.Escape) {

            TerminalView.keyStroke = TerminalView.terminal.readInput();

            if(Player.HP <= 0) {
                GameEndByDead();
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

    private static void GameEndByDead(){
        LogBlock.Action(Colors.RED_BRIGHT + "are dead. GameOver.");

        LogBlock.Event("Press any key to exit");

        try {
            TerminalView.terminal.readInput();
            /*RestartGame();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void RestartGame() throws IOException {
        TerminalView.terminal.flush();

        Dungeon.Rooms = new ArrayList<>();

        Dungeon.CurrentRoomCreatures = new ArrayList<>();

        Player.PlayerReset();

        RestartThread();

        Dungeon.Generate();

    }

    public static void RegenRoom() {

        RestartThread();

        System.out.flush();
        BaseGenerate.GenerateRoom(Objects.requireNonNull(Dungeon.Rooms.stream().filter(
                room -> room.NumberOfRoom == CurrentRoom
        ).findAny().orElse(null)));
        BaseGenerate.PutPlayerInDungeon(Dungeon.CurrentRoom[0].length/2,1, Dungeon.CurrentRoom);

    }

    public static void ChangeRoom(Room room){

        if(!room.IsRoomStructureGenerate){
            try {
                BaseGenerate.GenerateRoom(
                        Objects.requireNonNull(BaseGenerate.GetRoom(Dungeon.Direction.NEXT)).nextRoom);
            }
            catch (NullPointerException e){
                e.getStackTrace();
                MapEditor.setIntoCell(PlayerModel, 1,1);
            }
            finally {
                BaseGenerate.PutPlayerInDungeon(
                        BaseGenerate.GetCenterOfRoom(room), 1,
                        Dungeon.CurrentRoom);
            }
        }
        else{

            Player.CurrentRoom = room.NumberOfRoom;
            Dungeon.CurrentRoom = room.RoomStructure;
            Dungeon.CurrentRoomCreatures = room.RoomCreatures;
            BaseGenerate.PutPlayerInDungeon(BaseGenerate.GetCenterOfRoom(room), 1,
                    room.RoomStructure);
        }

        RestartThread();

        TerminalView.ResetPositions();
    }
}
