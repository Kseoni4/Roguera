package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.input.Input;
import com.rogurea.main.items.*;
import com.rogurea.main.map.Position;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.Draw;

import java.util.Arrays;
import java.util.Objects;

import static com.rogurea.main.view.ViewObjects.*;

public class InventoryMenu extends MenuWithContext {

    private final char MenuKey = 'i';

    private Position InnerInventoryPosition;

    public InventoryMenu() {
        super(new Position(5,10), "Inventory", new TerminalSize(28,8));
    }

    @Override
    protected void MenuElements() {

        Debug.log("UI: Open inventory");

        KeyStroke key = Input.GetKey();

        try{
            if(key.getCharacter() == MenuKey){
                UIHide = Disable();
                ClearItemList();
                Debug.log("UI: Close inventory");
            }
        }catch (NullPointerException ignored){}

        if(MenuElements.size() > 0) {
            MenuCursor.SelectElement(key.getKeyType());
        }
    }

    @Override
    public void openContext() {
        UIContextHide = Enable();

        MenuContext();

        updateElements();
    }

    @Override
    protected void MenuContext() {

        SetupContextElement();

        while(!UIContextHide) {

            Draw.call(this);

            if(MenuElements.isEmpty()){
                UIContextHide = Disable();
                break;
            }

            KeyStroke key = Input.GetKey();

            ContextCursor.SelectElement(key.getKeyType());

            SetupElements();

            UpdateIndex();
        }
        Draw.call(this);
    }

    private void SetupContextElement(){

        int InnerX = InnerTopLeftPoint.getColumn();

        int InnerY = InnerTopLeftPoint.getRow();

        MenuContextElements.clear();

        MenuContextElements.add(new Element("UseItem", "Use", new Position(InnerX+3, InnerY+5), ()->{
            Debug.log("INVENTORY: Using item " + Player.Inventory.get(MenuCursor.IndexOfElement).name);

            Usable usableItem = (Usable) Player.Inventory.get(MenuCursor.IndexOfElement);

            InventoryController.UseItem(usableItem);

            logBlock.Action("are drink the " + ((Potion) usableItem).getMaterialColor() + ((Potion) usableItem).name);

            Player.GetFromInventory(item -> ((Potion) usableItem).id == item.id);

            Draw.call(playerInfoBlock);
        }));

        MenuContextElements.add(new Element("Equip", "Equip", new Position(InnerX+1, InnerY+5),()->{
                Item item = Player.Inventory.get(MenuCursor.IndexOfElement);

                Debug.log("INVENTORY: Equipping item " + Player.Inventory.get(MenuCursor.IndexOfElement).name);

                InventoryController.EquipItem((Equipment) item,
                        InventoryController.getPlace(item));
        }));

        MenuContextElements.add(new Element("Drop", "Drop", new Position(InnerX+7, InnerY+5),()->{
            MenuElements.removeIf(element -> Objects.equals(element.ElementName, String.valueOf(Player.Inventory.get(MenuCursor.IndexOfElement).id)));
            InventoryController.DropItem(Player.Inventory.get(MenuCursor.IndexOfElement));
        }));

        MenuContextElements.add(new Element("Back", "Back", new Position(InnerX+12, InnerY+5),()->{
            UIContextHide = Disable();
        }));

        MenuContextElements.forEach(element -> element.setElementPointerPosition(element.ElementPosition.getRelative(-1, 0)));

        ContextPointer = GameResources.GetModel("PointerRight");

        ContextCursor.CursorPonter = ContextPointer;

        setContextElements();
    }

    private boolean CheckUsable(){
        return Arrays.stream(Player.Inventory.get(MenuCursor.IndexOfElement)
                .getClass()
                .getInterfaces())
                .anyMatch(aClass -> aClass == Usable.class);
    }

    @Override
    protected void MenuContextContent() {

        if(CheckUsable()){
            MenuContextElements.removeIf(element -> element.ElementName.equals("Equip"));
        }else{
            MenuContextElements.removeIf(element -> element.ElementName.equals("UseItem"));
        }

        for(Element element : MenuContextElements){
            PutElementOnPosition(element);
        }

        PutPointerNearElement(ContextCursor);
    }

    @Override
    protected void SetupElements() {
        if(!MenuElements.isEmpty())
            ClearItemList();
        try {
            for (Item item : Player.Inventory) {
                MenuElements.add(new ItemElement(item));
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
                if (itemElement.getClass().equals(ItemElement.class)) {
                    itemElement.setElementPosition(InnerInventoryPosition.getRelative(MenuElements.indexOf(itemElement), 1));
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

        int UpperRow = 1;

        int RighterCol = 10;

        SetBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"));

        FillSpaceByEmpty(InnerTopLeftPoint, InventoryBordersSize);

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

        PutStringOnPosition("Item: ", InnerTopLeftPoint);

        StringBuilder info = new StringBuilder();

        if(Player.Inventory.size() > 0) {

            UpdateIndex();

            Item item = Player.Inventory.get(MenuCursor.IndexOfElement);

            info.append(item.getMaterialColor());

            info.append(item.name);

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(6, 0));

            info.delete(0, info.length());

            info.append(Colors.ORANGE).append('$').append(item.SellPrice).append(' ');

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(12, 1));

            info.delete(0, info.length());

            if (item instanceof Weapon)
                info.append("ATK: ").append(Colors.RED_BRIGHT).append(((Weapon) item).GetStats());

            else if (item instanceof Armor)
                info.append("DEF: ").append(Colors.VIOLET).append(((Armor) item).GetStats());
            else if (item instanceof Potion)
                info.append("PTS:+").append(item.getMaterialColor()).append((((Potion) item).GetPotionPointsEffect()));

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(12,2));
        } else{
            info.append("Inventory is empty");

            PutStringOnPosition(info.toString(), InnerTopLeftPoint.withRelative(6, 0));
        }
    }

    private void ClearItemList(){
        MenuElements.clear();
    }

    private class ItemElement extends Element{

        public ItemElement(Item item) {
            String itemModelWithColor = GetItemColor(item);
            setElementTitle(itemModelWithColor);
            ElementAction = ()-> inventoryMenu.openContext();
            ElementName = String.valueOf(item.id);
        }

        private String GetItemColor(Item item){
            return (item instanceof Equipment ? GameResources.MaterialColor.get(
                    ((Equipment) item).Material) : item.getMaterialColor()) + item._model;
        }
    }
}
