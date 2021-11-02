/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.resources;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.view.PlayerInfoWindow;
import com.rogurea.view.ViewObjects;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameResources {

    public static Font TerminalFont = null;

    public static final String VERSION = "v0.2.6:0111:2330";

    public static final char EMPTY_CELL = ' ';

    public static final char PLAYER_MODEL = '@';

    private static final HashMap<String, Model> MODEL_HASH_MAP = new HashMap<>();

    private static final ArrayList<String> WHOOPS_TEXT = new ArrayList<>();

    private static final ArrayList<String> MOB_NAMES = new ArrayList<>();

    private static final String[] resources = {
            "MapStructure",
            "Equipment",
            "InventoryStructure",
            "Entity"
    };

    private static final String[] textResources = {
            "whoops",
            "mobnames"
    };

    public static void loadFont(){
        if(TerminalFont == null) {
            Debug.toLog("[RESOURCES]Loading font...");
            try {
                InputStream file_font = GameResources.class.getResourceAsStream("PxPlus_IBM_VGA_9x16.ttf");

                assert file_font != null;
                TerminalFont = Font.createFont(Font.TRUETYPE_FONT, file_font).deriveFont(24f);

                Debug.toLog("[RESOURCES]" + Colors.GREEN_BRIGHT + "Font loaded successfully");
            } catch (FontFormatException | IOException e) {
                Debug.toLog("[RESOURCES]" + Colors.RED_BRIGHT + "Error in loading font:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static final String FILE_PATH = "";
    private static final String FILE_EXTENSION = ".res";

    public static void clearResources(){
        MODEL_HASH_MAP.clear();
        MOB_NAMES.clear();
        WHOOPS_TEXT.clear();
    }

    public static void loadResources() {
        for(String file : resources){
            String showload = "[RESOURCES]= Loading resources from " + file + ".res =";
            Debug.toLog(showload);
            try {
                InputStream atlas_csv = GameResources.class.getResourceAsStream(FILE_PATH +file+ FILE_EXTENSION);

                byte[] buffer = new byte[1024];

                assert atlas_csv != null;
                int len = atlas_csv.read(buffer);

                byte[] inputstring = Arrays.copyOf(buffer, len);

                String[] map = new String(inputstring, StandardCharsets.UTF_8).split(",|\r\n|\n");

                Debug.toLog("[RESOURCES] Getting string: " + Arrays.toString(map));

                putStringsIntoMap(map);
            } catch (IOException e) {
                Debug.toLog(e.getMessage());
            }
        }
        loadTextResources();
        Debug.toLog("[RESOURCES]= Loading resources is ended =");
    }

    private static void loadTextResources() {
        for(String file : textResources){
            try {
                InputStream text_csv = GameResources.class.getResourceAsStream(FILE_PATH + file + FILE_EXTENSION);
                byte[] buffer = new byte[1024];

                assert text_csv != null;
                int len = text_csv.read(buffer);

                byte[] inputstring = Arrays.copyOf(buffer, len);

                String[] map = new String(inputstring, StandardCharsets.UTF_8).split(",|\r\n|\n");

                Debug.toLog("[RESOURCES]= Getting string: " + Arrays.toString(map));

                switch (file){
                    case "whoops":
                        WHOOPS_TEXT.addAll(Arrays.asList(map));
                        break;
                    case "mobnames":
                        MOB_NAMES.addAll(Arrays.asList(map));
                        break;
                }
            }catch (IOException e){
                Debug.toLog(e.getMessage());
            }
        }
    }

    public static Model getModel(String modelname){
        try{
            return MODEL_HASH_MAP.get(modelname);
        }
        catch (NullPointerException e){
            Debug.toLog(Colors.ORANGE+"[RESOURCE]: Get model error: no such symbol for name " + modelname);
            System.out.printf(Colors.ORANGE+"[RESOURCES]Get model error: no such symbol for name \"%s\"\n",modelname);
            return new Model();
        }
    }

    private static void putStringsIntoMap(String[] array){
        for(int i = 1; i < (array != null ? array.length : 0); i+=2){
            char model = array[i].charAt(0);

            String name = array[i-1];
            Debug.toLog("[RESOURCE]: Map ["+name+"-"+model+"]");
            MODEL_HASH_MAP.put(name, new Model(name, model));
        }
    }

    public static ArrayList<String> getWhoopsText(){
        return WHOOPS_TEXT;
    }

    public static ArrayList<String> getMobNames(){
        return MOB_NAMES;
    }

    public static HashMap<Character, Runnable> getKeyMap(){
        HashMap<Character, Runnable> _keymap = new HashMap<>();
        _keymap.put('r', Dungeon::RegenRoom);
        _keymap.put('j', () -> new PlayerInfoWindow(Dungeon.player).show());
        //_keymap.put('i', () -> ViewObjects.inventoryView.openToInput());
        _keymap.put('1', () -> ViewObjects.inventoryView.useItem(0));
        _keymap.put('2', () -> ViewObjects.inventoryView.useItem(1));
        _keymap.put('3', () -> ViewObjects.inventoryView.useItem(2));
        _keymap.put('4', () -> ViewObjects.inventoryView.useItem(3));
        _keymap.put('5', () -> ViewObjects.inventoryView.useItem(4));
        //_keymap.put('c', logBlock::Clear);
        //_keymap.put('i', () -> inventoryMenu.show());
        //_keymap.put(InventoryContainer.getMenuKey(), inventoryMenuUI::show);;

        return _keymap;
    }
    public static final String LOGO =
            " /$$$$$$$\n" +
            "| $$__  $$                                                           \n" +
            "| $$  \\ $$  /$$$$$$   /$$$$$$  /$$   /$$  /$$$$$$   /$$$$$$  /$$$$$$ \n" +
            "| $$$$$$$/ /$$__  $$ /$$__  $$| $$  | $$ /$$__  $$ /$$__  $$|____  $$\n" +
            "| $$__  $$| $$  \\ $$| $$  \\ $$| $$  | $$| $$$$$$$$| $$  \\__/ /$$$$$$$\n" +
            "| $$  \\ $$| $$  | $$| $$  | $$| $$  | $$| $$_____/| $$      /$$__  $$\n" +
            "| $$  | $$|  $$$$$$/|  $$$$$$$|  $$$$$$/|  $$$$$$$| $$     |  $$$$$$$\n" +
            "|__/  |__/ \\______/  \\____  $$ \\______/  \\_______/|__/      \\_______/\n" +
            "                     /$$  \\ $$                                       \n" +
            "                    |  $$$$$$/                                       \n" +
            "                     \\______/        "+VERSION+"                            \n";


}

