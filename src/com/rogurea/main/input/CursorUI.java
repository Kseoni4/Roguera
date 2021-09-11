package com.rogurea.main.input;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.map.Position;
import com.rogurea.main.view.UI.Menu.Element;

import java.util.ArrayList;

public class CursorUI {
    public Position CursorPosition;

    public int IndexOfElement;

    public ArrayList<Element> Elements;

    public char CursorPonter;

    private int Left = -1;

    private final int Right = 1;

    public void SelectElement(KeyType Key){
        switch (Key){
            case ArrowLeft: {Move(Left); break;}
            case ArrowRight: {Move(Right); break;}
            case Enter: {Elements.get(IndexOfElement).ElementAction.run();break;}
        }
        try {
            setCursorPositionByIndex();
        } catch (IndexOutOfBoundsException e){
            Debug.log("UI ERROR: Index of element " + IndexOfElement + " is out of bounds." );
        }
    }

    private void setCursorPositionByIndex(){
        CursorPosition = Elements.get(IndexOfElement).ElementPointerPosition;
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
