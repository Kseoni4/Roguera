/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.view;

import com.rogurea.view.ui.*;

import java.util.ArrayList;

public class ViewObjects {

    public static ArrayList<IViewBlock> ViewBlocks;

    public static MapView mapView;

    public static PlayerInfoView playerInfoView;

    public static LogView logView;

    public static InventoryView inventoryView;

    public static InfoGrid infoGrid;

    /*public static LogBlock logBlock;

    public static GameMapBlock gameMapBlock;

    public static ControlBlock controlBlock;

    public static PlayerInfoBlock playerInfoBlock;

    public static InventoryMenu inventoryMenu;

    public static ShopMenu shopMenu;*/

    public static String getTrimString(String message){
        String trimmedMessage = message.trim();
        int len = trimmedMessage.split(" ").length;
        for(int i = 0; i <= len; i++) {
            trimmedMessage = trimmedMessage.replaceFirst("\\[\\d{2};\\d{1};((\\d{3}m)|(\\d{2}m)|(\\d{1}m))", "");
            trimmedMessage = trimmedMessage.replaceFirst("(\\u001b|\\u001b[ESC]\\[(\\d{1}m))", "");
            trimmedMessage = trimmedMessage.replaceFirst("(\\[(\\d{1}m))", "");
            trimmedMessage = trimmedMessage.replaceFirst("\\[\\u001b|\\u001b[ESC]", "");
        }
        return trimmedMessage;
    }

    public static void LoadViewObjects(){
        ViewBlocks = new ArrayList<>();

        mapView = new MapView();

        infoGrid = new InfoGrid();

        /*inventoryMenu = new InventoryMenu();

        shopMenu = new ShopMenu();

        gameMapBlock = new GameMapBlock();

        playerInfoBlock = new PlayerInfoBlock();

        controlBlock = new ControlBlock();

        logBlock = new LogBlock();*/
    }
}
