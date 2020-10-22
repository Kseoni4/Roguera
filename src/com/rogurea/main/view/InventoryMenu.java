package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.input.*;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.MenuContainer;

import java.io.IOException;

public class InventoryMenu {

    private static boolean HideInv = true;

    private static boolean ContextMenuHide = true;

    private static KeyStroke key;

    private static TerminalPosition topInventoryLeft;

    private static TextGraphics InventoryGraphics = null;

    private static final String[] Options = {
            "Equip",
            "Drop",
            "Back",
    };

    private static final InvAction invAction = new InvAction();

    private static final InvContextActions invContextActions = new InvContextActions();

    public static String Selected = " ";

    private static char pointer = GameResources.PointerUp;

    private static int IndexOffset = 0;

    private static int PosY = 3;

    private static final int PosX = 1;

    private static final int ShiftPlusOne = 0;

    private static final int ShiftPlusN = 1;

    private static MenuContainer InventoryContainer;

    static int Offset = PosX;

    public static void Init(){

        try {
            InventoryGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+35, 12);

        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
    }

    public static void show(){
        SwitchVisibility();
        TerminalView.CurrentPointerPosition = Cursor.CursorPos;
        ControlContext();
    }

    private static void SwitchVisibility(){
        HideInv = !HideInv;
    }

    private static void SwitchContextVisibility(){
        ContextMenuHide = !ContextMenuHide;
    }

    private static void ControlContext(){

        InventoryContainer = new MenuContainer(
                topInventoryLeft,
                InventoryGraphics,
                pointer,
                IndexOffset,
                Offset
        );

        while(!HideInv){
            pointer = GameResources.PointerUp;

            key = Input.GetKey();

            try{
                if(key.getCharacter() == 'i') {
                    SwitchVisibility();
                    break;
                }
            }
            catch (NullPointerException ignored){ }

            IndexOffset = Cursor.Moving(key.getKeyType(),
                    InventoryContainer,
                    1,
                    Player.Inventory.size(),
                    PosY,
                    ShiftPlusOne,
                    invAction);

            /*InventoryMenu.UpdateCursor();*/

            System.out.println("IndexOffset: " + IndexOffset);

            InventoryContainer.setIndexoffset(IndexOffset);
        }

        ResetIndex();

        InventoryContainer.setOffset(Offset);

        InventoryContainer.setIndexoffset(IndexOffset);

        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
    }

    public static void OpenContextItemMenu(){

        SwitchContextVisibility();

        PosY = 5;

        pointer = GameResources.PointerRight;

        int contextOffset = 0;

        Offset = contextOffset;

        InventoryContainer.setIndexcontext(contextOffset);

        InventoryContainer.setOffset(Offset);

        InventoryContainer.changePointer(pointer);

        InventoryContainer.setOptionNames(Options);

        Cursor.CursorPos = topInventoryLeft.withRelative(Offset,PosY);

        TerminalView.CurrentPointerPosition = Cursor.CursorPos;

        while(!Selected.equals("Back")){
            key = Input.GetKey();

            try{
                if(key.getCharacter().equals('i'))
                    break;
            }
            catch (NullPointerException ignored){
            }
            contextOffset = Cursor.Moving(key.getKeyType(), InventoryContainer, 2, Options.length, PosY, ShiftPlusN, invContextActions);

            /*InventoryMenu.UpdateCursor();*/

            System.out.println("CO: " + contextOffset);

            InventoryContainer.setIndexcontext(contextOffset);
        }

/*
        InventoryMenu.UpdateCursor();
*/

        Selected = "";

        PosY = 3;

        Offset = IndexOffset+1;

        Cursor.CursorPos = topInventoryLeft.withRelative(Offset,PosY);

        InventoryContainer.setOptionNames(null);

        InventoryContainer.setIndexoffset(IndexOffset);

        InventoryContainer.setOffset(Offset);

        SwitchContextVisibility();
    }

    public static void DrawInventory(){
        if(!HideInv){
            DrawInventoryBorders();
            int offset = 1;
            for(Item item : Player.Inventory){
                TerminalView.DrawBlockInTerminal(InventoryGraphics,
                        "\u001b[38;5;202m" + item._model,
                        topInventoryLeft.withRelative(offset,1));
                offset++;
            }
            PutCursorOnPos(TerminalView.CurrentPointerPosition);
            ShowItemInfo();
            ShowContextMenu();
        }
    }

    private static void DrawInventoryBorders(){
        InventoryGraphics.putCSIStyledString(topInventoryLeft.getColumn(), topInventoryLeft.getRow()-1,
                "Inventory");
        InventoryGraphics.drawLine(
                topInventoryLeft,
                new TerminalPosition(topInventoryLeft.getColumn()+10, topInventoryLeft.getRow()),
                GameResources.InvHWallDown
        );
        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+2),
                new TerminalPosition(topInventoryLeft.getColumn()+10, topInventoryLeft.getRow()+2),
                GameResources.InvHWallUp
        );

        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+1),
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+1),
                GameResources.InvVWallRight
        );
        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn()+11, topInventoryLeft.getRow()+1),
                new TerminalPosition(topInventoryLeft.getColumn()+11, topInventoryLeft.getRow()+1),
                GameResources.InvVWallLeft
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn(),
                topInventoryLeft.getRow(),
                GameResources.InvLPCorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn()+11,
                topInventoryLeft.getRow(),
                GameResources.InvRPorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn(),
                topInventoryLeft.getRow()+2,
                GameResources.InvLDorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn()+11,
                topInventoryLeft.getRow()+2,
                GameResources.InvRDorner
        );

    }

    private static void PutCursorOnPos(TerminalPosition position){
        TerminalView.SetPointerIntoPosition(InventoryGraphics, pointer, position);
    }

    private static void ShowItemInfo(){
        Weapon weapon = (Weapon) GetItem();
        if(weapon != null)
            InventoryGraphics.putCSIStyledString(
                    topInventoryLeft.getColumn(),
                    topInventoryLeft.getRow()+4,
                    weapon._model + " "
                            + weapon.name + " " + Colors.ORANGE + "$"
                            + weapon.SellPrice + Colors.RED_BRIGHT + " dmg"
                            + weapon.getDamage()
            );
    }

    private static void ShowContextMenu() {
        int offset = 1;
        if (!ContextMenuHide) {
            for (String opt : InventoryMenu.Options) {
                InventoryGraphics.putCSIStyledString(topInventoryLeft.withRelative(offset, PosY),
                        opt);
                offset += opt.length() + 2;
            }
        }
    }

    /*public static void UpdateCursor(){
        if(IndexOffset >= Player.Inventory.size()){
            IndexOffset--;
        }
    }*/

    public static Item GetItem(){
        if(Player.Inventory.size() > 0){
           return Player.Inventory.get(IndexOffset);
        }
        return null;
    }

    public static void ResetIndex(){
        IndexOffset = 0;
        Offset = PosX;
    }

    public static void Reset(){
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+35, 12);
        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
        ResetIndex();
    }
}
