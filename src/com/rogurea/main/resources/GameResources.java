package com.rogurea.main.resources;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.rogurea.main.view.ViewObjects.*;

public class GameResources {

    public static final Font TerminalFont = new Font("Px437 IBM VGA 9x16", Font.PLAIN, 24);

    public static final String version = "0.0.8:0901:0158dev";

    public static final char EmptyCell = ' ';

    public static final char PlayerModel = '@';

    public static final char Gold = '$';

    public static final char HWall = '━';

    public static final char SWall = '─';

    public static final char Vwall = '┃';

    /*public static final char ShopWindow = '╎';*/

    public static final char LBCorner = '┗';
    public static final char RTCorner = '┓';
    public static final char LTCorner = '┏';
    public static final char RBCorner = '┛';
    public static final char LRCenter = '┣';
    public static final char RLCenter = '┫';
    public static final char TCenter  = '┳';
    public static final char BCenter  = '┻';

    public static final char[] MapStructureAtlas = {HWall, Vwall, SWall, LBCorner, RTCorner, LTCorner, RBCorner, LRCenter, RLCenter, TCenter, BCenter};

    public static final char[] CornersAtlas = {LBCorner, RTCorner, LTCorner, RBCorner};

    public static final char LongSword = '╁';

    public static final char ShortSword = '╈';

    public static final char Knife = '╽';

    public static final char ArmorChest = '₼';

    public static final char[] MeleeAtlas = {LongSword, ShortSword, Knife};

    public static final char Bow = '⟭';

    public static final char[] RangeAtlas = {Bow};

    public static final char Potion = '℗';

    public static final char[] UsableAtlas = {Potion};

    public static final String[] SwordLenght = {"Long", "Short", "Medium"};

    public static final String[] MaterialName = {"Wood", "Stone", "Iron", "Copper", "Golden", "Diamond"};

    public static final HashMap<String, String> MaterialColor;

    static {
        MaterialColor = new HashMap<>();
        MaterialColor.put("Wood", Colors.BROWN);
        MaterialColor.put("Stone", Colors.GREY);
        MaterialColor.put("Iron", Colors.IRON);
        MaterialColor.put("Copper", Colors.COPPER);
        MaterialColor.put("Golden", Colors.GOLDEN);
        MaterialColor.put("Diamond", Colors.DIAMOND);
        MaterialColor.put("HEAL", Colors.GREEN_BRIGHT);
        MaterialColor.put("BUF_ATK", Colors.RED_BRIGHT);
        MaterialColor.put("BUF_DEF", Colors.BLUE_BRIGHT);
    }

    public static final String[] ArmorMaterialName = {"leather", "chain", "iron", "steel", "titan"};

    public static final HashMap<String, String> ArmorMaterialColor;

    static {
        ArmorMaterialColor = new HashMap<>();
        ArmorMaterialColor.put("leather", Colors.BROWN);
        ArmorMaterialColor.put("chain", Colors.GREY);
        ArmorMaterialColor.put("iron", Colors.IRON);
        ArmorMaterialColor.put("steel", Colors.MAGENTA);
        ArmorMaterialColor.put("titan", Colors.VIOLET);
    }
    public static final char[] ArmorAtlas = {ArmorChest};

    public static final char[][] WearableAtlas = {ArmorAtlas, MeleeAtlas, RangeAtlas, UsableAtlas};

    public static final String[] HitsMessages = {
            "take a small byte of you for %dmg%",
            "smash you by his sword for %dmg%",
            "successfull hit! You get %dmg%"
    };

    public static HashSet<Weapon> AllWeapons = new HashSet<>();

    public static final HashMap<Character, String> ModelNameMap = new HashMap<>();

    public static String PlayerName = "Player: " + Colors.GREEN_BRIGHT + Player.nickName + " ";

    public static void UpdatePlayerName(){
        PlayerName = "Player: " + Colors.GREEN_BRIGHT + Player.nickName + " ";
    }

    public static StringBuilder getPlayerPositionInfo(){
        return  new StringBuilder("Player position "
                + "x:" + com.rogurea.main.player.Player.Pos.x
                + " "
                + "y:" + com.rogurea.main.player.Player.Pos.y + ' ');
    }

    public static StringBuilder UpdatePlayerHPMP() {
        return new StringBuilder(Colors.R + "HP: " + Colors.RED_BRIGHT + Player.HP + " "
                /*+ Colors.R + "MP: " + Colors.BLUE_BRIGHT + Player.MP + " "*/);
    }

    public static StringBuilder UpdatePlayerMoneyXP(){
        return new StringBuilder(Colors.R + "Money: " + Colors.ORANGE + Player.Money + " "
                + Colors.R + "XP: " + Colors.ORANGE + Player.XP + "/" + Player.ReqXPForNextLevel);
    }

    public static StringBuilder UpdatePlayerATKDEFDEX(){
        return new StringBuilder(Colors.R + "DEF: " + Colors.VIOLET + Formula.GetPlayerDEF() + " "
                + Colors.R + "ATK: " + Colors.PINK + Formula.GetPlayerATK() + " "
        + Colors.R + "DEX: " + Colors.DIAMOND + Player.DEX);
    }

    public static StringBuilder UpdatePlayerLvlRoom(){
        return new StringBuilder(Colors.R + "Level: " + Colors.CYAN + Player.Level + " "
                + Colors.R + "Room: " + Colors.MAGENTA + Player.CurrentRoom + " ");
    }

    public static final HashMap<String, Character> ResourcesMap = new HashMap<>();

    public static String Logo = " /$$$$$$$                                                            \n" +
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

    private static final String[] resources = {
            "MapStructure",
            "Equipment",
            "InventoryStructure",
            "Entity"
    };

    public static void LoadResources() {
        int i = 0;
        for(String file : resources){
            String showload = "= Loading resources from " + file + ".csv =";
            Debug.log(showload);
            InputStream atlas_csv = GameResources.class.getResourceAsStream("csv/"+file+".csv");
            try {
                String[] map = new String(atlas_csv.readAllBytes()).split(",|\r\n|\n");
                PutStringsIntoMap(map);
            } catch (IOException e) {
                Debug.log(e.getMessage());
            }
            i++;
        }
        Debug.log("= Loading resources is ended =");
    }

    public static char GetModel(String modelname){
        try{
            return ResourcesMap.get(modelname);
        }
        catch (NullPointerException e){
            Debug.log("RESOURCE: Get model error: no such symbol for name " + modelname);
            System.out.printf("Get model error: no such symbol for name \"%s\"\n",modelname);
            return '?';
        }
    }

    public static void MakeMap() {

        Debug.log("RESOURCE: Make char map from CharAtlas.csv");

        InputStream csvfile = GameResources.class.getResourceAsStream("csv/Roguera | CharAtlas.csv");

        String[] CharName = null;

        try {
            String maps = new String(csvfile.readAllBytes());
            CharName = maps.split(",|\r\n");
        }
        catch (IOException e) {
            Debug.log(e.getMessage());
        }

        for(int i = 1; i < (CharName != null ? CharName.length : 0); i+=2){
            char s = CharName[i-1].charAt(0);
            String n = CharName[i];
            Debug.log("RESOURCE: Char Map ["+n+"-"+s+"]");
            ModelNameMap.put(s, n);
        }
    }

    private static void PutStringsIntoMap(String[] array){
        for(int i = 1; i < (array != null ? array.length : 0); i+=2){
            char s = array[i].charAt(0);
            String n = array[i-1];
            Debug.log("RESOURCE: Map ["+n+"-"+s+"]");
            ((Map<String, Character>) GameResources.ResourcesMap).put(n,s);
        }
    }

    public static HashMap<Character, Runnable> GetKeyMap(){
        HashMap<Character, Runnable> _keymap = new HashMap<>();
        _keymap.put('r', Dungeon::RegenRoom);
        _keymap.put('c', logBlock::Clear);
        _keymap.put('i', () -> inventoryMenu.show());
        //_keymap.put(InventoryContainer.getMenuKey(), inventoryMenuUI::show);;

        return _keymap;
    }
}
