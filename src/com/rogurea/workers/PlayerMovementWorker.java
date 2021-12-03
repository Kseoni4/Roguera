package com.rogurea.workers;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.input.Input;
import com.rogurea.player.KeyController;
import com.rogurea.player.MoveController;
import com.rogurea.view.TerminalView;

import java.io.IOException;
import java.util.Optional;

public class PlayerMovementWorker implements Runnable {

    @Override
    public void run() {
        Debug.toLog("[PLAYER_MOVEMENT_WORKER] Start of worker");
        while(Dungeon.player.getHP() > 0) {
            Input.waitForInput().ifPresent(keyStroke -> TerminalView.keyStroke = keyStroke);

            if(!isNotEscapePressed() || Thread.currentThread().isInterrupted()){
                break;
            }

            KeyController.getKey(TerminalView.keyStroke.getCharacter());

            MoveController.movePlayer(Optional.ofNullable(TerminalView.keyStroke));
        }
        Debug.toLog("[PLAYER_MOVEMENT_WORKER] End of worker");
    }

    private boolean isNotEscapePressed(){
        KeyType keyType = TerminalView.keyStroke.getKeyType();
        return keyType != KeyType.Escape;
    }
}
