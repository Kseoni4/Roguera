/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.input;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.view.Element;

import java.util.ArrayList;

public class CursorUI {
    public Position CursorPosition;

    public int IndexOfElement;

    public ArrayList<Element> Elements;

    public char CursorPointer = '>';

    private final int Left = -1;
    private final int Up = -1;

    private final int Down = 1;
    private final int Right = 1;

    public void SelectElementH(KeyType Key){
        switch (Key){
            case ArrowLeft: {Move(Left); break;}
            case ArrowRight: {Move(Right); break;}
            case Enter: {Elements.get(IndexOfElement).ElementAction.run();break;}
        }
        setCursorPositionByIndex();
    }

    public void SelectElementV(KeyType key){
        switch (key){
            case ArrowDown: {Move(Down); break;}
            case ArrowUp: {Move(Up); break;}
            case Enter: {Elements.get(IndexOfElement).ElementAction.run(); break;}
        }
        setCursorPositionByIndex();
    }

    private void setCursorPositionByIndex(){
        try {
            CursorPosition = Elements.get(IndexOfElement).ElementPointerPosition;
        }catch (IndexOutOfBoundsException e){
            Debug.toLog("UI ERROR: Index of element " + IndexOfElement + " is out of bounds." );
        }
    }

    public void setElements(ArrayList<Element> elements){
        this.Elements = elements;
        if(Elements.isEmpty()){
            setDefaultCursorPosition();
        }else if (Elements.size() == 1){
            setFirstElementCursorPosition();
        } else if(IndexOfElement < Elements.size()){
            setCursorPositionByIndex();
        } else{
            IndexOfElement = 0;
        }
    }

    private void Move(int Direction){
        if((IndexOfElement+Direction) < 0 || (IndexOfElement+Direction) >= Elements.size()){
            return;
        }
        IndexOfElement += Direction;
    }

    public void setFirstElementCursorPosition(){
        CursorPosition = Elements.get(0).ElementPointerPosition;
    }

    public void setDefaultCursorPosition(){
        CursorPosition = new Position(0, 0);
    }

    public CursorUI(ArrayList<Element> MenuElements){
        setElements(MenuElements);
        this.IndexOfElement = 0;
    }
}
