package com.rogurea.research;

import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.rogurea.research.R_Player.Player;

public class R_GameLoop {

    public static void Start(){
        try{

            T_View.InitTerminal();
            InLoop();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            if (T_View.terminal != null) {
                try {
                    T_View.terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void InLoop() throws IOException {

        while (T_View.keyStroke.getKeyType() != KeyType.Escape) {

            T_View.Drawcall();

            T_View.keyStroke = T_View.terminal.readInput();

/*            if(R_MobController.MobsScan()) {
                T_View.DrawFightMenu();
                T_View.MenuPrompt(R_Dungeon.CurrentRoomCreatures.keySet());
            }*/

            R_MoveController.MovePlayer(T_View.keyStroke.getKeyType());
        }
    }

    public static void ChangeRoom(R_Room room){
        if(!room.IsRoomStructureGenerate){
            try {
                R_Generate.GenerateRoom(
                        Objects.requireNonNull(R_Generate.GetRoom(R_Dungeon.Direction.NEXT)).nextRoom);
            }
            catch (NullPointerException e){
                e.getMessage();
                R_Dungeon.CurrentRoom[1][1] = Player;
            }
            finally {
                R_Generate.PutPlayerInDungeon(
                        R_Generate.GetCenterOfRoom(room), 1,
                        R_Dungeon.CurrentRoom);
            }
        }
        else{
            R_Player.CurrentRoom = room.NumberOfRoom;
            R_Dungeon.CurrentRoom = room.RoomStructure;
            R_Generate.PutPlayerInDungeon(R_Generate.GetCenterOfRoom(room), 1,
                    room.RoomStructure);
        }
    }

}
