package com.rogurea.main.view.UI;

import com.rogurea.main.input.Cursor_UI;
import com.rogurea.main.view.IMenuAction;
import com.rogurea.main.items.*;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.UIContainer;
import com.rogurea.main.view.Menu;
import com.rogurea.main.view.TerminalView;
import java.io.IOException;

public class InventoryMenu_new extends Menu {

    private int IndexElement = 0;
    private int IndexOption = 0;

    public InventoryMenu_new(UIContainer menuContainer) {
        super(menuContainer);

        this.menuContainer.InitializeExtraData();
        int offsetX = 1;
        this.menuContainer.putExtraData("OffsetX", offsetX);
        try {
            MenuGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void MenuContextContent() {
        int offset = 1;
        for (String opt : menuContainer.getMenuOptions()) {
            MenuGraphics.putCSIStyledString(RelativePosition.withRelative(offset, 3),
                    opt);
            offset += opt.length() + 2;
        }
    }

    @Override
    protected void MenuContent() {
        int offset = 1;
        for(Item item : Player.Inventory){
            TerminalView.DrawBlockInTerminal(MenuGraphics,
                    (item.getClass() == Weapon.class ?
                            GameResources.MaterialColor.get(((Weapon) item).Material)
                            : Colors.MAGENTA) + item._model,
                    RelativePosition.withRelative(offset,1));
            offset++;
        }
        ShowItemInfo();
    }

    private void ShowItemInfo(){
        Item item = GetItem();
        StringBuilder info = new StringBuilder();

        if(item != null) {

            if(item instanceof Equipment)
                info.append(GameResources.MaterialColor.get(((Equipment) item).Material));

            info.append(item._model).append(' ')
                    .append(item.name);

            MenuGraphics.drawLine(RelativePosition.withRelative(0,-1),
                    RelativePosition.withRelative(item.name.length(),-1), ' ');

            TerminalView.DrawBlockInTerminal(MenuGraphics, info.toString(), RelativePosition.getColumn(),
                    RelativePosition.getRow()-1);

            info.delete(0, info.length());

            info.append(Colors.ORANGE).append('$').append(item.SellPrice).append(' ');

            TerminalView.DrawBlockInTerminal(MenuGraphics, info.toString(), RelativePosition.getColumn()+12,
                    RelativePosition.getRow());

            info.delete(0, info.length());

            if (item instanceof Weapon)
                info.append("ATK: ").append(Colors.RED_BRIGHT).append(((Weapon) item).GetStats());

            else if (item instanceof Armor)
                info.append("DEF: ").append(Colors.VIOLET).append(((Armor) item).GetStats());

            TerminalView.DrawBlockInTerminal(MenuGraphics, info.toString(), RelativePosition.getColumn()+12,
                    RelativePosition.getRow()+1);
        }
    }

    public Item GetItem(){
        if(Player.Inventory.size() > 0){
            return Player.Inventory.get(IndexElement);
        }
        return null;
    }

    @Override
    protected void MenuActions() {

        IMenuAction menuAction = (int index) -> {
            if(Player.Inventory.size() > 0)
                openContext();
        };

        int shiftGapOne = 1;
        IndexElement = Cursor_UI.Moving(Key.getKeyType(), IndexElement, Pointer, shiftGapOne, menuContainer, menuAction);
    }

    @Override
    protected void MenuContextActions() {

        IMenuAction menuContextAction = (int index) -> {
            switch (index) {
                case 0 -> InventoryController.EquipItem((Equipment) GetItem(),
                        InventoryController.getPlace(GetItem()));
                case 1 -> InventoryController.DropItem(GetItem());
                case 2 -> this.Selected = "Back";
            }
        };

        int shiftGapTwo = 2;
        IndexOption = Cursor_UI.Moving(Key.getKeyType(), IndexOption, Pointer, shiftGapTwo, menuContainer, menuContextAction);
    }
}