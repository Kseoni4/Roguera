package com.rogurea;

import com.rogurea.research.R_Dungeon;
import com.rogurea.research.R_Room;
import com.rogurea.research.T_GUI;
import com.rogurea.research.T_View;

public class Main{

    public static void main(String[] args){

        R_Dungeon.GenerateDungeon();
        R_Dungeon.GenerateRoom(R_Dungeon.Rooms.keySet()
                                .stream()
                                .filter(
                                        r_room -> r_room.NumberOfRoom == 1
                                ).findAny().orElse(null)
        );
        //T_GUI.InitGUI();
        T_View.InitTerminal();

//         if(Dungeon.Generate()){
//             for(Room r : Dungeon.Rooms){
//                 PrintDebugInfo.Room(r);
//             }
//
//             Player player = new Player("Player1");
//
//             GameLoop.Start(player);
//
//
//         }
//         else{
//
//         }
    }
}


