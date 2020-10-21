package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.input.Input;
import com.rogurea.main.items.InventoryController;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

import java.io.IOException;
import java.util.ArrayList;

public class InventoryMenu {

    private static boolean HideInv = true;

    private static boolean ContextMenuHide = true;

    private static KeyStroke key;

    private static TerminalPosition topInventoryLeft;

    private static TerminalPosition CursorPos;

    private static TextGraphics InventoryGraphics = null;

    private static final String[] Options = {
            "Equip",
            "Drop",
            "Back",
    };

    private static ArrayList<Item> BufferItems = new ArrayList<>();

    private static String Selected = " ";

    private static char pointer = GameResources.PointerUp;

    private static int IndexOffset = 0;

    private static int ContextOffset = 0;

    private static int PosY = 3;

    private static int PosX = 1;

    private static final int Left = -1;

    private static final int Right = 1;

    private static final int Shift = 2;

    static int Offset = PosX;

    public static void Init(){
        try {
            InventoryGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+35, 12);

        CursorPos = topInventoryLeft.withRelative(PosX,PosY);
    }

    public static void show(){
        SwitchVisibility();
        BufferItems = Player.Inventory;
        ControlContext();
    }

    private static void SwitchVisibility(){
        HideInv = !HideInv;
    }
    private static void SwitchContextVisibility(){
        ContextMenuHide = !ContextMenuHide;
    }

    private static void ControlContext(){

        while(!HideInv){
            pointer = GameResources.PointerUp;
            key = Input.GetKey();
            try{
                if(key.getCharacter() == 'i') {
                    SwitchVisibility();
                    break;
                }
            }
            catch (NullPointerException ignored){
            }
            MoveCursor(key.getKeyType(), BufferItems.size(), false);
        }
        Offset = PosX;
        ResetIndexes();
        CursorPos = topInventoryLeft.withRelative(PosX,PosY);
    }

    private static void MoveCursor(KeyType key, int lengthmenu, boolean InContext){
        switch (key) {
            case ArrowLeft -> {
                if(!InContext){
                    Move(Left, lengthmenu);
                    break;
                }
                Move(Left);
            }
            case ArrowRight -> {
                if(!InContext){
                    Move(Right, lengthmenu);
                    break;
                }
                Move(Right);
            }
            case Enter -> {
                if(!InContext && BufferItems.size() > 0){
                    ContextItemMenu();
                    break;
                }
                Selected = Options[ContextOffset];
                Action(Selected);
            }
        }
    }

    private static void Move(int n, int lenght){
        IndexOffset += n;
        if(IndexOffset >= 0 && IndexOffset <= lenght-1){
            Offset += n;
            CursorSlide(Offset, PosY);
        }
        else{
            ClampPos(lenght);
        }
    }

    private static void Move(int n){
        ContextOffset += n;
        if(ContextOffset >= 0 && ContextOffset < Options.length){

            int b = (n == Left ? 0 : -1);

            Offset += (Options[ContextOffset+b].length() + (Shift))*n;

            CursorSlide(Offset, PosY);
        }
        else{
            ClampPos(Options.length);
        }
    }

    private static void ClampPos(int lenght){
        if(ContextOffset < 0){
            ContextOffset = 0;
            ResetOffset();
            return;
        }
        else if (ContextOffset >= lenght) {
            ContextOffset = lenght - 1;
            return;
        }
        if(IndexOffset < 0){
            IndexOffset = 0;
        }
        else if(IndexOffset >= lenght)
        {
            IndexOffset = lenght-1;
        }
    }

    private static void ContextItemMenu(){

        SwitchContextVisibility();

        PosY = 5;

        pointer = GameResources.PointerRight;

        ContextOffset = 0;

        ResetOffset();

        CursorPos = topInventoryLeft.withRelative(Offset,PosY);

        while(!Selected.equals("Back")){
            key = Input.GetKey();

            try{
                if(key.getCharacter().equals('i'))
                    break;
            }
            catch (NullPointerException ignored){
            }

            MoveCursor(key.getKeyType(), Options.length, true);
        }
        PosY = 3;

        Offset = IndexOffset+1;

        CursorPos = topInventoryLeft.withRelative(Offset,PosY);

        Selected = "";

        SwitchContextVisibility();
    }

    private static void Action(String action){
        switch (action){
            case "Equip":
                InventoryController.EquipItem(GetItem(), "FirstWeapon");
                break;
            case "Drop":
                InventoryController.DropItem(GetItem());
                break;
            case "Back":
                break;
        }
    }

    private static void CursorSlide(int col, int row){
        CursorPos = topInventoryLeft.withRelative(col, row);
        System.out.println("CursorPos: " + CursorPos.getRow() + ";" + CursorPos.getColumn());
        PutCursonOnPos(CursorPos);
    }

    private static void ResetOffset(){
        Offset = 0;
    }

    private static void ResetIndexes(){
        IndexOffset = 0;
        ContextOffset = 0;
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
            PutCursonOnPos(CursorPos);
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

    private static void PutCursonOnPos(TerminalPosition position){
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

    public static void UpdateCursor(){
        if(IndexOffset > Player.Inventory.size()){
            IndexOffset--;
            Offset = 1;
        }
    }

    public static Item GetItem(){
        if(BufferItems.size() > 0){
           return BufferItems.get(IndexOffset);
        }
        return null;
    }
}
