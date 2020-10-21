package com.rogurea.main;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Room;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.KeyController;
import com.rogurea.main.player.Player;
import com.rogurea.main.player.PlayerMoveController;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.util.Objects;

import static com.rogurea.main.player.Player.CurrentRoom;
import static com.rogurea.main.player.Player.PlayerModel;

public class GameLoop {

    public static Thread drawcall = new Thread(new TerminalView(), "drawcall");

    public static void Start(){
        try{
            TerminalView.InitTerminal();
            InLoop();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            if (TerminalView.terminal != null) {
                try {
                    TerminalView.terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void InLoop() throws IOException {

        drawcall.start();

        while (TerminalView.keyStroke.getKeyType() != KeyType.Escape) {

            Player.AutoEquip();

            TerminalView.keyStroke = TerminalView.terminal.readInput();

            if (TerminalView.keyStroke.getKeyType() == KeyType.Character) {
                KeyController.GetKey(TerminalView.keyStroke.getCharacter());
            }
            PlayerMoveController.MovePlayer(TerminalView.keyStroke.getKeyType());

            Scans.CheckSee(Dungeon.CurrentRoom[Player.Pos.y+1][Player.Pos.x]);
        }
    }

    public static void RegenRoom() throws IOException {
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
            catch (NullPointerException | IOException e){
                e.getMessage();
                Dungeon.CurrentRoom[1][1] = PlayerModel;
            }
            finally {
                BaseGenerate.PutPlayerInDungeon(
                        BaseGenerate.GetCenterOfRoom(room), 1,
                        Dungeon.CurrentRoom);
            }
        }
        else{
            com.rogurea.main.player.Player.CurrentRoom = room.NumberOfRoom;
            Dungeon.CurrentRoom = room.RoomStructure;
            BaseGenerate.PutPlayerInDungeon(BaseGenerate.GetCenterOfRoom(room), 1,
                    room.RoomStructure);
        }
    }

}
