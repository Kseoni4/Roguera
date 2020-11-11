package com.rogurea.main.input;

import com.rogurea.main.items.Equipment;
import com.rogurea.main.items.InventoryController;
import com.rogurea.main.view.viewblocks.InventoryMenu;

import static com.rogurea.main.resources.ViewObject.inventoryMenu;

public class InvContextActions extends Action{

    public enum Options {
        EQUIP{
            public void select(){
                InventoryController.EquipItem((Equipment) inventoryMenu.GetItem(),
                        InventoryController.getPlace(
                                inventoryMenu.GetItem()));
            }
        },
        DROP{
            public void select(){
                InventoryController.DropItem(inventoryMenu.GetItem());
            }
        },
        BACK{
            public void select(){
                InventoryMenu.Selected = "Back";
            }
        };
        public abstract void select();
    }

    public void Do(int value) {
        Options.values()[value].select();
    }
}
