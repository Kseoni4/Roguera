package com.rogurea.main.view.UI;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.input.*;
import com.rogurea.main.items.*;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.MenuContainer;
import com.rogurea.main.view.ViewObjects;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.IMenuAction;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.util.Arrays;

import static com.rogurea.main.view.ViewObjects.*;

public class InventoryBlock implements IViewBlock {

    private boolean HideInv = true;

    private boolean ContextMenuHide = true;

    private KeyStroke key;

    private TerminalPosition topInventoryLeft;

    private final TerminalSize InventorySize = new TerminalSize(22,5);

    private TextGraphics InventoryGraphics = null;

    private final String[] Options = {
            "Equip",
            "Drop",
            "Back",
    };

    public static String Selected = " ";

    private final char up = GameResources.GetModel("PointerUp");

    private final char right = GameResources.GetModel("PointerRight");

    private char pointer = up;

    private int IndexOffset = 0;

    private int PosY = 3;

    private final int PosX = 1;

    private MenuContainer InventoryContainer;

    private int Offset = PosX;

    private final int XOffset = 5;

    private final int YOffset = 10;

    private final TerminalSize InventoryBordersSize = new TerminalSize(23,7);

    public InventoryBlock(){
        ViewObjects.ViewBlocks.add(this);
    }

    public void Init(){

        try {
            InventoryGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+XOffset, YOffset);

        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
    }

    public void show(){
        SwitchVisibility();
        TerminalView.CurrentPointerPosition = Cursor.CursorPos;
        ControlContext();
    }

    private void SwitchVisibility(){
        HideInv = !HideInv;
    }

    private void SwitchContextVisibility(){
        ContextMenuHide = !ContextMenuHide;
    }

    private void ControlContext(){

        InventoryContainer = new MenuContainer(
                topInventoryLeft,
                InventoryGraphics,
                pointer,
                IndexOffset,
                Offset
        );

        IMenuAction action = (int index) -> {
            if(Player.Inventory.size() > 0)
                inventoryBlock.OpenContextItemMenu();
        };

        while(!HideInv){
            pointer = up;

            Draw.call(this);

            key = Input.GetKey();

            try{
                if(key.getCharacter() == 'i') {
                    SwitchVisibility();
                    break;
                }
            }
            catch (NullPointerException ignored){ }

            int shiftPlusOne = 0;
            IndexOffset = Cursor.Moving(key.getKeyType(),
                    InventoryContainer,
                    1,
                    Player.Inventory.size(),
                    PosY,
                    shiftPlusOne,
                    action);
            InventoryContainer.setIndexoffset(IndexOffset);
        }

        ResetIndex();

        InventoryContainer.setOffset(Offset);

        InventoryContainer.setIndexoffset(IndexOffset);

        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);

        Draw.reset(this);

        Draw.call(logBlock);
    }

    public void OpenContextItemMenu(){

        SwitchContextVisibility();

        PosY = 3;

        pointer = right;

        int contextOffset = 0;

        Offset = contextOffset;

        InventoryContainer.setIndexcontext(contextOffset);

        InventoryContainer.setOffset(Offset);

        InventoryContainer.changePointer(pointer);

        InventoryContainer.setOptionNames(Options);

        Cursor.CursorPos = topInventoryLeft.withRelative(Offset,PosY);

        TerminalView.CurrentPointerPosition = Cursor.CursorPos;

        IMenuAction action = (int index) -> {
            switch (index) {
                case 0 -> {
                            if(CheckUsable()){
                                InventoryController.UseItem((Usable) inventoryBlock.GetItem());
                                logBlock.Action("are drink the " + GetItem().getMaterialColor() + GetItem().name);
                                Player.GetFromInventory(item -> inventoryBlock.GetItem().id == item.id);
                                Draw.call(playerInfoBlock);
                            } else {
                                InventoryController.EquipItem((Equipment) inventoryBlock.GetItem(),
                                InventoryController.getPlace(inventoryBlock.GetItem()));
                            }
                }
                case 1 -> InventoryController.DropItem(inventoryBlock.GetItem());
                case 2 -> InventoryBlock.Selected = "Back";
            }
        };

        Draw.call(this);

        while(!Selected.equals("Back")){

            key = Input.GetKey();

            try{
                if(key.getCharacter().equals('i'))
                    break;
            }
            catch (NullPointerException ignored){
            }
            int shiftPlusN = 1;
            contextOffset = Cursor.Moving(key.getKeyType(), InventoryContainer, 2, Options.length, PosY, shiftPlusN, action);

            InventoryContainer.setIndexcontext(contextOffset);

            IndexOffset = InventoryContainer.UpdateCursor(IndexOffset);

            Draw.call(this);
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

    public void Draw(){
        if(!HideInv){
            TerminalView.InitGraphics(InventoryGraphics, topInventoryLeft, InventorySize);
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
        } else{
            Draw.reset(this);
        }
    }

    private void DrawInventoryBorders(){

        char InvHWallDown = GameResources.GetModel("InvHWallDown");
        char InvHWallUp = GameResources.GetModel("InvHWallUp");
        char InvVWallRight = GameResources.GetModel("InvVWallRight");
        char InvVWallLeft = GameResources.GetModel("InvVWallLeft");
        char InvLPCorner = GameResources.GetModel("InvLPCorner");
        char InvRPorner = GameResources.GetModel("InvRPorner");
        char InvLDorner = GameResources.GetModel("InvLDorner");
        char InvRDorner = GameResources.GetModel("InvRDorner");

        InventoryGraphics.setBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"));

        InventoryGraphics.fillRectangle(topInventoryLeft, InventorySize.withRelative(0,0), MapEditor.EmptyCell);

        InventoryGraphics.drawRectangle(topInventoryLeft.withRelative(-1,-2),
                InventoryBordersSize
                , '█');

        InventoryGraphics.putCSIStyledString(topInventoryLeft.getColumn(), topInventoryLeft.getRow()-2,
                Colors.WHITE_BRIGHT +"Inventory");
        InventoryGraphics.drawLine(
                topInventoryLeft,
                new TerminalPosition(topInventoryLeft.getColumn()+10, topInventoryLeft.getRow()),
                InvHWallDown
        );
        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+2),
                new TerminalPosition(topInventoryLeft.getColumn()+10, topInventoryLeft.getRow()+2),
                InvHWallUp
        );

        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+1),
                new TerminalPosition(topInventoryLeft.getColumn(), topInventoryLeft.getRow()+1),
                InvVWallRight
        );
        InventoryGraphics.drawLine(
                new TerminalPosition(topInventoryLeft.getColumn()+11, topInventoryLeft.getRow()+1),
                new TerminalPosition(topInventoryLeft.getColumn()+11, topInventoryLeft.getRow()+1),
                InvVWallLeft
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn(),
                topInventoryLeft.getRow(),
                InvLPCorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn()+11,
                topInventoryLeft.getRow(),
                InvRPorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn(),
                topInventoryLeft.getRow()+2,
                InvLDorner
        );
        InventoryGraphics.setCharacter(
                topInventoryLeft.getColumn()+11,
                topInventoryLeft.getRow()+2,
                InvRDorner
        );

    }

    private void PutCursorOnPos(TerminalPosition position){
        TerminalView.SetPointerIntoPosition(InventoryGraphics, pointer, position);
    }

    private void ShowItemInfo(){
        Item item = GetItem();
        StringBuilder info = new StringBuilder();

        if(item != null) {
            info.append(item.getMaterialColor());

            info.append(item._model).append(' ')
                    .append(item.name);

            InventoryGraphics.drawLine(topInventoryLeft.withRelative(0,-1),
                    topInventoryLeft.withRelative(item.name.length(),-1), ' ');

            TerminalView.DrawBlockInTerminal(InventoryGraphics, info.toString(), topInventoryLeft.getColumn(),
                    topInventoryLeft.getRow()-1);

            info.delete(0, info.length());

            info.append(Colors.ORANGE).append('$').append(item.SellPrice).append(' ');

            TerminalView.DrawBlockInTerminal(InventoryGraphics, info.toString(), topInventoryLeft.getColumn()+12,
                    topInventoryLeft.getRow());

            info.delete(0, info.length());

            if (item instanceof Weapon)
                info.append("ATK: ").append(Colors.RED_BRIGHT).append(((Weapon) item).GetStats());

            else if (item instanceof Armor)
                info.append("DEF: ").append(Colors.VIOLET).append(((Armor) item).GetStats());
            else if (item instanceof Potion)
                info.append("PTS:+").append(item.getMaterialColor()).append((((Potion) item).GetPotionPointsEffect()));

            TerminalView.DrawBlockInTerminal(InventoryGraphics, info.toString(), topInventoryLeft.getColumn()+12,
                    topInventoryLeft.getRow()+1);
        }
    }

    private void ShowContextMenu() {
        int offset = 1;
        if (!ContextMenuHide) {
            for (String opt : Options) {
                if(CheckUsable()) {
                    opt = opt.replace("Equip", "Use");
                }
                InventoryGraphics.putCSIStyledString(topInventoryLeft.withRelative(offset, PosY),
                        opt);
                offset += opt.length() + 2;
            }
        }
    }

    public Item GetItem(){
        if(Player.Inventory.size() > 0){
            return Player.Inventory.get(IndexOffset);
        }
        return null;
    }

    private boolean CheckUsable(){
       return Arrays.stream(GetItem().getClass().getInterfaces()).anyMatch(aClass -> aClass == Usable.class);
    }

    private void ResetIndex(){
        IndexOffset = 0;
        Offset = PosX;
    }

    public void Reset(){
        InventoryGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        TerminalView.InitGraphics(InventoryGraphics, topInventoryLeft.withRelative(-1,-2), InventoryBordersSize);
        topInventoryLeft = new TerminalPosition(Dungeon.CurrentRoom[0].length+XOffset, YOffset);
        Cursor.CursorPos = topInventoryLeft.withRelative(PosX,PosY);
        ResetIndex();
    }
}
