package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.input.Input;
import com.rogurea.main.items.*;
import com.rogurea.main.map.Position;
import com.rogurea.main.map.Shop;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;

public class ShopMenu extends MenuWithTabs{

    private Shop RoomShop;

    private Position InnerInventoryPosition;

    public ShopMenu() {
        super(new Position(5,10), "Shop", new TerminalSize(28,10));
    }

    public void setRoomShop(Shop roomShop){
        this.RoomShop = roomShop;
    }

    @Override
    protected void MenuElements() {
        KeyStroke key = Input.GetKey();

        if(key.getKeyType().equals(KeyType.Escape)){
            UIHide = Disable();
            return;
        }

        if(MenuElements.size() > 0) {
            MenuCursor.SelectElement(key.getKeyType());

            SetupElements();

            UpdateIndex();
        }
    }

    @Override
    protected void MenuContext() {

    }

    @Override
    protected void MenuContextContent() {

    }

    @Override
    public void openContext() {

    }

    @Override
    protected void SetupElements() {
        if(!MenuElements.isEmpty())
            ClearItemList();
        try {
            for (Item item : Player.Inventory) {
                MenuElements.add(new ShopMenu.ItemElement(item));
            }
            //MenuElements.sort(Comparator.comparingInt(value -> Integer.parseInt(value.ElementTitle)));
        }catch (Exception e) {
            Debug.log("UI ERROR: failed setup elements");
        }
        updateElements();
    }

    @Override
    protected void MenuContent() {
        DrawInventoryBorders();

        DrawInventoryItems();

        PutPointerNearElement(MenuCursor);

        ShowItemInfo();
    }

    private void DrawInventoryItems(){
        if(!MenuElements.isEmpty()) {
            for (Element itemElement : MenuElements) {
                if (itemElement.getClass().equals(ShopMenu.ItemElement.class)) {
                    itemElement.setElementPosition(InnerInventoryPosition.getRelative(MenuElements.indexOf(itemElement), 2));
                    itemElement.setElementPointerPosition(itemElement.ElementPosition.getRelative(0, 2));
                    PutElementOnPosition(itemElement);
                }
            }
            updateElements();
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

        TerminalSize InventoryBordersSize = new TerminalSize(12,3);

        int UpperRow = 2;

        int RighterCol = 10;

        SetBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"));

        FillSpaceByEmpty(InnerTopLeftPoint.withRelativeRow(UpperRow), InventoryBordersSize);

        DrawCustomLine(InnerTopLeftPoint.withRelativeRow(UpperRow), InnerTopLeftPoint.withRelative(RighterCol,UpperRow), InvHWallDown);

        DrawCustomLine(InnerTopLeftPoint.withRelativeRow(UpperRow+2), InnerTopLeftPoint.withRelative(RighterCol,UpperRow+2), InvHWallUp);

        DrawCustomLine(InnerTopLeftPoint.withRelativeRow(UpperRow+1), InnerTopLeftPoint.withRelativeRow(UpperRow+1), InvVWallRight);

        DrawCustomLine(InnerTopLeftPoint.withRelative(RighterCol+1,UpperRow+1),
                InnerTopLeftPoint.withRelative(RighterCol+1,UpperRow+1), InvVWallLeft);

        PutCharacterOnPosition(InnerTopLeftPoint.withRelative(0,UpperRow), InvLPCorner);
        PutCharacterOnPosition(InnerTopLeftPoint.withRelative(RighterCol+1,UpperRow), InvRPorner);
        PutCharacterOnPosition(InnerTopLeftPoint.withRelativeRow(UpperRow+2), InvLDorner);
        PutCharacterOnPosition(InnerTopLeftPoint.withRelative(RighterCol+1,UpperRow+2), InvRDorner);

        InnerInventoryPosition = new Position(InnerTopLeftPoint.getColumn()+1, InnerTopLeftPoint.getRow()+1);
    }

    private void UpdateIndex(){
        if(MenuCursor.IndexOfElement > 0 && MenuCursor.IndexOfElement > Player.Inventory.size()-1){
            MenuCursor.IndexOfElement -= 1;
        }
    }

    private void ShowItemInfo(){

        DrawCustomLine(InnerTopLeftPoint, InnerTopLeftPoint.withRelativeColumn(24), ' ');

        PutStringOnPosition("Choose an item to sell", InnerTopLeftPoint);

        PutStringOnPosition("Item: ", InnerTopLeftPoint.withRelativeRow(1));

        StringBuilder info = new StringBuilder();

        if(Player.Inventory.size() > 0) {

            UpdateIndex();

            Item item = Player.Inventory.get(MenuCursor.IndexOfElement);

            info.append(item.getMaterialColor());

            info.append(item.name);

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(6, 1));

            info.delete(0, info.length());

            info.append(Colors.ORANGE).append('$').append(item.SellPrice).append(' ');

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(12, 2));

            info.delete(0, info.length());

            if (item instanceof Weapon)
                info.append("ATK: ").append(Colors.RED_BRIGHT).append(((Weapon) item).GetStats());

            else if (item instanceof Armor)
                info.append("DEF: ").append(Colors.VIOLET).append(((Armor) item).GetStats());
            else if (item instanceof Potion)
                info.append("PTS:+").append(item.getMaterialColor()).append((((Potion) item).GetPotionPointsEffect()));

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(12,3));

            PutStringOnPosition("ENTER to sell", InnerTopLeftPoint.withRelative(12,4));
        } else{
            info.append("Inventory is empty");

            PutStringOnPosition("ESC to exit", InnerTopLeftPoint.withRelative(12,3));

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(6, 1));
        }
    }

    private void ClearItemList(){
        MenuElements.clear();
    }

    private class ItemElement extends Element{

        public ItemElement(Item item) {
            String itemModelWithColor = GetItemColor(item);
            setElementTitle(itemModelWithColor);
            ElementAction = ()-> Player.GetGold(new Gold(RoomShop.SellItem(Integer.parseInt(ElementName))));
            ElementName = String.valueOf(item.id);
        }

        private String GetItemColor(Item item){
            return (item instanceof Equipment ? GameResources.MaterialColor.get(
                    ((Equipment) item).Material) : item.getMaterialColor()) + item._model;
        }
    }
}
