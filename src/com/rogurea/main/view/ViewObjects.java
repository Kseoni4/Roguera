package com.rogurea.main.view;

import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.UIContainer;
import com.rogurea.main.view.UI.*;

import java.io.*;
import java.util.ArrayList;

public class ViewObjects {

    public static ArrayList<IViewBlock> ViewBlocks;

    public static UIContainer InventoryContainer = GetDeserializeContainer("Inventory");

    public static LogBlock logBlock;

    public static GameMapBlock gameMapBlock;

    public static ControlBlock controlBlock;

    public static InventoryBlock inventoryBlock;

    public static InventoryMenu_new inventoryMenuUI;

    public static PlayerInfoBlock playerInfoBlock;

    public static UIContainer GetDeserializeContainer(String title){
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(title + ".ui"));

            UIContainer container = (UIContainer) objectInputStream.readObject();

            System.out.println("Десериализировали объект " + container.getMenuTitle());

            return container;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(Colors.RED_BRIGHT + title + ".ui is not found" +Colors.R);
            return new UIContainer();
        }
    }

    public static void LoadViewObjects(){
        ViewBlocks = new ArrayList<>();

        logBlock = new LogBlock();

        gameMapBlock = new GameMapBlock();

        controlBlock = new ControlBlock();

        inventoryBlock = new InventoryBlock();

        inventoryMenuUI = new InventoryMenu_new(InventoryContainer);

        playerInfoBlock = new PlayerInfoBlock();
    }
}
