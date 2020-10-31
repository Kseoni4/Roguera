package com.rogurea.main.gamelogic;

import com.rogurea.main.player.Player;
import com.rogurea.main.view.TerminalView;

public class GlobalWatcher extends Thread {

   public GlobalWatcher(){

   }

   public void run(){
       while (TerminalView.terminal != null && !isInterrupted()){

        CheckPlayerHP();

           try {
               sleep(100);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

       }
   }

   private void CheckPlayerHP(){
       if(Player.HP <= 0){

       }
   }

}
