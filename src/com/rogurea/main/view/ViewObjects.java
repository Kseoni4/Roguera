package com.rogurea.main.view;

import com.rogurea.main.view.UI.*;
import com.rogurea.main.view.UI.Menu.ExitDungeonMenu;
import com.rogurea.main.view.UI.Menu.InventoryMenu;
import com.rogurea.main.view.UI.Menu.ShopMenu;

import java.util.ArrayList;

public class ViewObjects {

    public static ArrayList<IViewBlock> ViewBlocks;

    public static LogBlock logBlock;

    public static GameMapBlock gameMapBlock;

    public static ControlBlock controlBlock;

    public static PlayerInfoBlock playerInfoBlock;

    public static InventoryMenu inventoryMenu;

    public static ShopMenu shopMenu;

    public static void LoadViewObjects(){
        ViewBlocks = new ArrayList<>();

        inventoryMenu = new InventoryMenu();

        shopMenu = new ShopMenu();

        gameMapBlock = new GameMapBlock();

        playerInfoBlock = new PlayerInfoBlock();

        controlBlock = new ControlBlock();

        logBlock = new LogBlock();
    }
}
