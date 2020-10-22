package com.rogurea.main.input;

import com.rogurea.main.items.InventoryController;
import com.rogurea.main.player.Player;
import com.rogurea.main.view.InventoryMenu;

public class InvAction extends Action {

    enum Options {
        OPENCONTEXTMENU {
            public void select(){
                if(Player.Inventory.size() > 0)
                    InventoryMenu.OpenContextItemMenu();
            }
        };
        public abstract void select();
    }

    public void Do(int value) {
        value = 0;
        Options.values()[value].select();
    }
}
