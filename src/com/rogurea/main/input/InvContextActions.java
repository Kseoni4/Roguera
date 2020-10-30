package com.rogurea.main.input;

import com.rogurea.main.items.InventoryController;
import com.rogurea.main.view.InventoryMenu;

public class InvContextActions extends Action{

    public enum Options {
        EQUIP{
            public void select(){
                InventoryController.EquipItem(InventoryMenu.GetItem(),
                        InventoryController.getPlace(
                        InventoryMenu.GetItem()));
            }
        },
        DROP{
            public void select(){
                InventoryController.DropItem(InventoryMenu.GetItem());
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
