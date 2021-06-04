package com.rogurea.dev.view;

import com.rogurea.dev.view.IViewBlock;

import java.util.ArrayList;

public class ViewObjects {

    public static ArrayList<IViewBlock> ViewBlocks;

    public static MapView mapView;

    /*public static LogBlock logBlock;

    public static GameMapBlock gameMapBlock;

    public static ControlBlock controlBlock;

    public static PlayerInfoBlock playerInfoBlock;

    public static InventoryMenu inventoryMenu;

    public static ShopMenu shopMenu;*/

    public static void LoadViewObjects(){
        ViewBlocks = new ArrayList<>();

        mapView = new MapView();
        /*inventoryMenu = new InventoryMenu();

        shopMenu = new ShopMenu();

        gameMapBlock = new GameMapBlock();

        playerInfoBlock = new PlayerInfoBlock();

        controlBlock = new ControlBlock();

        logBlock = new LogBlock();*/
    }
}
