/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.resources;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.view.PlayerInfoWindow;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameResources {

    public static Font TerminalFont = null;

    public static final String VERSION = "0.2.2:2709:2353dev";

    public static final char EMPTY_CELL = ' ';

    public static final char PLAYER_MODEL = '@';

    private static final HashMap<String, Model> MODEL_HASH_MAP = new HashMap<>();

    private static final String[] resources = {
            "MapStructure",
            "Equipment",
            "InventoryStructure",
            "Entity"
    };

    public static void loadFont(){
        Debug.toLog("Loading font...");
        try{
            InputStream file_font = GameResources.class.getResourceAsStream("PxPlus_IBM_VGA_9x16.ttf");

            assert file_font != null;
            TerminalFont = Font.createFont(Font.TRUETYPE_FONT, file_font).deriveFont(24f);

            Debug.toLog("Font loaded successfully");
        } catch (FontFormatException | IOException e) {
            Debug.toLog("Error in loading font:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final String FILE_PATH = "";
    private static final String FILE_EXTENSION = ".res";

    public static void loadResources() {
        for(String file : resources){
            String showload = "= Loading resources from " + file + ".res =";
            Debug.toLog(showload);
            try {
                InputStream atlas_csv = GameResources.class.getResourceAsStream(FILE_PATH +file+ FILE_EXTENSION);

                byte[] buffer = new byte[1024];

                assert atlas_csv != null;
                int len = atlas_csv.read(buffer);

                byte[] inputstring = Arrays.copyOf(buffer, len);

                String[] map = new String(inputstring, StandardCharsets.UTF_8).split(",|\r\n|\n");

                Debug.toLog("= Getting string: " + Arrays.toString(map));

                putStringsIntoMap(map);
            } catch (IOException e) {
                Debug.toLog(e.getMessage());
            }
        }
        Debug.toLog("= Loading resources is ended =");
    }

    public static Model getModel(String modelname){
        try{
            return MODEL_HASH_MAP.get(modelname);
        }
        catch (NullPointerException e){
            Debug.toLog("RESOURCE: Get model error: no such symbol for name " + modelname);
            System.out.printf("Get model error: no such symbol for name \"%s\"\n",modelname);
            return new Model();
        }
    }

    private static void putStringsIntoMap(String[] array){
        for(int i = 1; i < (array != null ? array.length : 0); i+=2){
            char model = array[i].charAt(0);

            String name = array[i-1];
            Debug.toLog("RESOURCE: Map ["+name+"-"+model+"]");
            MODEL_HASH_MAP.put(name, new Model(name, model));
        }
    }


    public static HashMap<Character, Runnable> getKeyMap(){
        HashMap<Character, Runnable> _keymap = new HashMap<>();
        _keymap.put('r', Dungeon::RegenRoom);
        _keymap.put('j', () -> new PlayerInfoWindow(Dungeon.player).show());
        //_keymap.put('c', logBlock::Clear);
        //_keymap.put('i', () -> inventoryMenu.show());
        //_keymap.put(InventoryContainer.getMenuKey(), inventoryMenuUI::show);;

        return _keymap;
    }
/*
    public static String Logo =
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
            "                     \\______/                                        \n";


 */
}

