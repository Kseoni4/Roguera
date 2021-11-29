/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.input;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Position;
import com.rogurea.resources.Colors;
import com.rogurea.view.Element;

import java.util.ArrayList;

public class CursorUI {
    public Position cursorPosition;

    public Position previousCursorPosition;

    public int indexOfElement;

    public ArrayList<Element> elements;

    public char cursorPointer = '>';

    private final int LEFT = -1;
    private final int UP = -1;

    private final int DOWN = 1;
    private final int RIGHT = 1;

    public void SelectElementH(KeyType Key){
        previousCursorPosition = new Position(cursorPosition);
        switch (Key){
            case ArrowLeft: {Move(LEFT); break;}
            case ArrowRight: {Move(RIGHT); break;}
            case Enter: {
                elements.get(indexOfElement).ElementAction.run();break;}
        }
        setCursorPositionByIndex();
    }

    public void SelectElementV(KeyType key){
        previousCursorPosition = new Position(cursorPosition);
        switch (key){
            case ArrowDown: {Move(DOWN); break;}
            case ArrowUp: {Move(UP); break;}
            case Enter: {
                try {
                    elements.get(indexOfElement).ElementAction.run();
                } catch (IndexOutOfBoundsException e) {
                    Debug.toLog(Colors.RED_BRIGHT + "[ERROR][CURSOR_UI] No such element on index");
                }
                break;
            }
        }
        setCursorPositionByIndex();
    }

    private void setCursorPositionByIndex(){
        try {
            cursorPosition = elements.get(indexOfElement).ElementPointerPosition;
        }catch (IndexOutOfBoundsException e){
            Debug.toLog("UI ERROR: Index of element " + indexOfElement + " is out of bounds." );
        }
    }

    public void setElements(ArrayList<Element> elements){
        this.elements = elements;
        if(this.elements.isEmpty()){
            setDefaultCursorPosition();
        }else if (this.elements.size() == 1){
            setFirstElementCursorPosition();
        } else if(indexOfElement < this.elements.size()){
            setCursorPositionByIndex();
        } else{
            indexOfElement = 0;
        }
    }

    private void Move(int Direction){
        if((indexOfElement +Direction) < 0 || (indexOfElement +Direction) >= elements.size()){
            return;
        }
        indexOfElement += Direction;
    }

    public void setFirstElementCursorPosition(){
        cursorPosition = elements.get(0).ElementPointerPosition;
        previousCursorPosition = new Position(cursorPosition);
    }

    public void setDefaultCursorPosition(){
        cursorPosition = new Position(0, 0);
    }

    public CursorUI(ArrayList<Element> MenuElements){
        setElements(MenuElements);
        this.indexOfElement = 0;
    }
}
