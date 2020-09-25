package com.rogurea.research;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class R_Event implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    void Action(KeyEvent e){
        if('D' == e.getKeyChar()){
            System.out.print(e.getKeyChar());
        }
    }
}
