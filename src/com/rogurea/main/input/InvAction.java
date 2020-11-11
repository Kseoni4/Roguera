package com.rogurea.main.input;

import com.rogurea.main.player.Player;

import static com.rogurea.main.resources.ViewObject.inventoryMenu;

public class InvAction extends Action {

    enum Options {
        OPENCONTEXTMENU {
            public void select(){
                if(Player.Inventory.size() > 0)
                    inventoryMenu.OpenContextItemMenu();
            }
        };
        public abstract void select();
    }

    public void Do(int value) {
        value = 0;
        Options.values()[value].select();
    }
}
