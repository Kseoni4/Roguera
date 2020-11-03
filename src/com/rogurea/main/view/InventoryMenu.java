package com.rogurea.main.view;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.input.*;
import com.rogurea.main.items.Armor;
import com.rogurea.main.items.Item;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
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

    private static final TerminalSize InventorySize = new TerminalSize(22,5);

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

    private static final int XOffset = 5;

    private static final int YOffset = 10;

    private static final TerminalSize InventoryBordersSize = new TerminalSize(23,7);

    public static void Init(){

        try {
            InventoryGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+XOffset, YOffset);

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

            InventoryContainer.setIndexoffset(IndexOffset);
        }

        ResetIndex();

        InventoryContainer.setOffset(Offset);

        InventoryContainer.setIndexoffset(IndexOffset);

        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
    }

    public static void OpenContextItemMenu(){

        SwitchContextVisibility();

        PosY = 3;

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

            System.out.println("CO: " + contextOffset);

            InventoryContainer.setIndexcontext(contextOffset);

            IndexOffset = InventoryContainer.UpdateCursor(IndexOffset);
        }

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
                        (item.getClass() == Weapon.class ?
                                GameResources.MaterialColor.get(((Weapon) item).Material)
                                : Colors.MAGENTA) + item._model,
                        topInventoryLeft.withRelative(offset,1));
                offset++;
            }
            PutCursorOnPos(TerminalView.CurrentPointerPosition);
            ShowItemInfo();
            ShowContextMenu();
        }
    }

    private static void DrawInventoryBorders(){

        InventoryGraphics.setBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"));

        InventoryGraphics.fillRectangle(topInventoryLeft, InventorySize.withRelative(0,0), MapEditor.EmptyCell);

        InventoryGraphics.drawRectangle(topInventoryLeft.withRelative(-1,-2),
                InventoryBordersSize
                , Symbols.BLOCK_MIDDLE);

        InventoryGraphics.putCSIStyledString(topInventoryLeft.getColumn(), topInventoryLeft.getRow()-2,
                Colors.WHITE_BRIGHT +"Inventory");
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
        Item item = GetItem();
        StringBuffer info = new StringBuffer();
        if(item != null) {

            if(item.getClass() == Weapon.class)
                info.append(GameResources.MaterialColor.get(((Weapon) item).Material));

            info.append(item._model).append(' ')
                    .append(item.name);

            TerminalView.DrawBlockInTerminal(InventoryGraphics, info.toString(), topInventoryLeft.getColumn(),
                    topInventoryLeft.getRow()-1);

                info.delete(0, info.length());

             info.append(Colors.ORANGE).append('$').append(item.SellPrice).append(' ');

            TerminalView.DrawBlockInTerminal(InventoryGraphics, info.toString(), topInventoryLeft.getColumn()+12,
                    topInventoryLeft.getRow());

            info.delete(0, info.length());

            if (item.getClass() == Weapon.class)
                info.append("ATK: ").append(Colors.RED_BRIGHT).append(((Weapon) item).getDamage());

            else if (item.getClass() == Armor.class)
                info.append("DEF: ").append(Colors.VIOLET).append(((Armor) item).getDefense());

            TerminalView.DrawBlockInTerminal(InventoryGraphics, info.toString(), topInventoryLeft.getColumn()+12,
                    topInventoryLeft.getRow()+1);
        }
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
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+XOffset, YOffset);
        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
        ResetIndex();
    }
}
