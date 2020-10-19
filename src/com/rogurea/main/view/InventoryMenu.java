package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.input.Input;
import com.rogurea.main.items.Item;
import com.rogurea.main.player.Player;

import java.util.ArrayList;

public class InventoryMenu {

    static ArrayList<Item> BufferItems = new ArrayList<>();

    public static boolean Selected = false;

    static int IndexOffset = 0;

    static int PosY = 3;

    static int PosX = 1;

    static TerminalPosition CursorPos = TerminalView.topInventoryLeft.withRelative(PosX,PosY);

    public static void show(){
        TerminalView.HideInv = !TerminalView.HideInv;
        BufferItems = Player.Inventory;
        GetControlContext();
    }

    private static void GetControlContext(){
        while(!TerminalView.HideInv){
            KeyStroke key = Input.GetKey();
            ControlContext(key);
        }
    }

    private static void ControlContext(KeyStroke key){
        if(key.getCharacter() != null && key.getCharacter() == 'i') {
            TerminalView.HideInv = true;
            return;
        }
        MoveCursor(key.getKeyType());
    }

    private static void MoveCursor(KeyType key){
        int ofs = 1;
        switch (key){
            case ArrowLeft:
                ofs = -1;
                if(CursorPos.getColumn() > TerminalView.topInventoryLeft.getColumn() && IndexOffset > 0) {
                    CursorPos = TerminalView.topInventoryLeft.withRelative(PosX+=ofs,3);
                    TerminalView.PutCursonOnPos(CursorPos);
                    IndexOffset--;
                }
                break;
            case ArrowRight:
                ofs = 1;
                if(PosX < Player.Inventory.size() && IndexOffset < BufferItems.size()-1){
                    CursorPos = TerminalView.topInventoryLeft.withRelative(PosX+=ofs,3);
                    TerminalView.PutCursonOnPos(CursorPos);
                    IndexOffset++;
                }
                break;
            case Enter:
                //ContextItemMenu();
                break;
        }
    }

    public static Item GetItem(){
        if(BufferItems.size() > 0){
           return BufferItems.get(IndexOffset);
        }
        return null;
    }
}
