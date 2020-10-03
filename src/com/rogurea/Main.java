package com.rogurea;

import com.rogurea.research.R_Dungeon;
import com.rogurea.research.R_GameLoop;
import com.rogurea.research.T_View;

import java.io.IOException;

public class Main{

    public static void main(String[] args) throws IOException {

        R_Dungeon.Generate();
        R_GameLoop.Start();

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


