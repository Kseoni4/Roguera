package com.rogurea.main.input;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.resources.UIContainer;
import com.rogurea.main.view.IMenuAction;
import com.rogurea.main.view.TerminalView;

public class Cursor_UI {
    private static UIContainer UI;

    private static TerminalPosition CursorPosition;

    private static int Index;

    private static int Length = 0;

    private static int ShiftGap = 0;

    private static char Pointer;

    private static int OffsetX;

    private static int OffsetY;

    private static final String OffsetX_ = "OffsetX";

    private static String OffsetY_ = "OffsetY";

    private static String ListSize = "ListSize";
/*


    private static int Shift;

    private static int PosY;

    private static int ShiftType = 0;*/

    private static final int Left = -1;

    private static final int Right = 1;

    private static final int Up = -1;

    private static final int Down = -1;

    public static int Moving(KeyType SendKey, int IndexElement, char pointer,
                             int shiftGap, UIContainer uiContainer, IMenuAction action){

        UI = uiContainer;

        Index = IndexElement;

        ShiftGap = shiftGap;

        Pointer = pointer;

        switch (SendKey){
            /*case ArrowUp -> Move(Up);
            case ArrowDown -> Move(Down);*/
            case ArrowLeft -> MoveLR(Left);
            case ArrowRight -> MoveLR(Right);
            case Enter -> action.Do(Index);
        }
        TerminalView.CurrentPointerPosition = CursorPosition;

        if(UI.getMenuOptions() == null){
            Index = UpdateCursor(Index);
        }
        return Index;
    }

    private static void MoveLR(int Direction) {
        Index += Direction;

        Length = UI.getMenuOptions().length;

        if(UI.getExtraDataFromKey(OffsetX_) != -1)
            OffsetX = UI.getExtraDataFromKey(OffsetX_);

        if(Index >= 0 && Index <= Length-1){
            int b = ((Direction == Left) ? 0 : -1);

            if(UI.getMenuOptions() != null){
                Length = UI.getMenuOptions()[Index+b].length();
            }

            int Shift = ShiftGap > 1 ? 2 : 1;

            OffsetX += ((Length * ShiftGap) + Shift) * Direction;

            CursorSlide(OffsetX, 0);
        }
        else{
            ClampPos(Length);
        }
    }

    private static void MoveUD(int Direction){
        Index += Direction;

        if(Index >= 0 && Index <= Length-1){
            /*int b = ((Direction == Down) ? 0 : -1);*/

            int Shift = ShiftGap > 1 ? 2 : 1;

            OffsetY += (ShiftGap + Shift) * Direction;

            CursorSlide(0, OffsetY);
        }
        else{
            ClampPos(0);
        }
    }

    private static void CursorSlide(int offsetX, int offsetY){
        CursorPosition = new TerminalPosition(UI.getCursorPosition().x, UI.getCursorPosition().y)
                .withRelative(offsetX, offsetY);

        TerminalView.SetPointerIntoPosition(UI.getUIGraphics(), Pointer, CursorPosition);

        if(offsetX > 0)
            UI.replaceExtraData(OffsetX_, offsetX);
        else
            UI.replaceExtraData(OffsetY_, offsetY);
    }

    private static void ClampPos(int Length){

        if (Index <= 0){
            Index = 0;
            OffsetX = 0;
            OffsetY = 0;
        }
        else if (Index >= Length){
            Index = Length-1;
        }
    }

    private static int UpdateCursor(int index){
        while(index >= UI.getExtraDataFromKey(ListSize) && index > 0){
            index--;
        }
        return index;
    }
}
