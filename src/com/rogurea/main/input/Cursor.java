package com.rogurea.main.input;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.MenuContainer;
import com.rogurea.main.view.TerminalView;

public class Cursor {

    public static TerminalPosition CursorPos;

    private static MenuContainer menu;

    private static int Index;

    private static int offset;

    private static int Shift;

    private static int PosY;

    private static int Length = 0;

    private static int ShiftType = 0;

    private static final int Left = -1;

    private static final int Right = 1;

    public static int Moving(
            KeyType key, MenuContainer container, int shift, int size, int y, int shifttype, IMenuAction action){

        menu = container;

        if(menu.getOptionNames() != null)
            Index = menu.getIndexcontext();
        else
            Index = menu.getIndexoffset();

        offset = menu.getOffset();

        Shift = shift;

        PosY = y;

        Length = size;

        ShiftType = shifttype;

        switch (key) {
            case ArrowUp -> {}
            case ArrowLeft -> Move(Left);
            case ArrowRight -> Move(Right);
            case Enter -> action.Do(Index);
        }
        TerminalView.CurrentPointerPosition = CursorPos;

        if(menu.getOptionNames() == null){
            Index = menu.UpdateCursor(Index);
            menu.setIndexoffset(Index);
        }

        return Index;
    }

    private static void Move(int n){
        Index += n;

        int element = Length;

        if(Index >= 0 && Index <= Length -1){

            int b = (n == Left ? 0 : -1);

            if(menu.getOptionNames() != null){
               element = menu.getOptionNames()[Index+b].length();
            }

            offset += ((element * ShiftType) + Shift) * n;

            CursorSlide(offset, PosY);
        }
        else{
            ClampPos(Length);
        }
    }

    private static void ClampPos(int Length){

        if (Index <= 0){
            Index = 0;
            offset = 0;
        }
        else if (Index >= Length){
            Index = Length-1;
        }
    }

    private static void CursorSlide(int col, int row) {

        CursorPos = menu.getPosition().withRelative(col, row);

        System.out.println("CursorPos: " + CursorPos.getRow() + ";" + CursorPos.getColumn());

        TerminalView.SetPointerIntoPosition(menu.getTextGUI(), menu.getPointer(), CursorPos);

        menu.setOffset(offset);
    }
}
