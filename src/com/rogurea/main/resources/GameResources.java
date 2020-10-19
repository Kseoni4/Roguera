package com.rogurea.main.resources;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextColor;
import com.rogurea.main.player.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class GameResources {

    public static final String version = "0.0.3:1910:1526";

    public static final char EmptyCell = ' ';

    public static final char PlayerModel = '@';

    public static final char Bot = '♂';

    public static final char Gold = '$';

    public static final char[] rsymbls = {
            '$', '/', '\\',
            '#', '!', '&',
            '^', '(', '*', ')',
            Symbols.BLOCK_SPARSE,
            Symbols.DOUBLE_LINE_VERTICAL,
            Symbols.DOUBLE_LINE_HORIZONTAL,
            Symbols.BULLET,
            Symbols.BLOCK_DENSE,
            Symbols.BLOCK_MIDDLE,
            Symbols.BLOCK_SOLID,
            Symbols.OUTLINED_SQUARE
    };
    public static String getPlayerPositionInfo(){
        return  "Player position "
                + "x:" + com.rogurea.main.player.Player.Pos.x
                + " "
                + "y:" + com.rogurea.main.player.Player.Pos.y + ' ';
    }

    public static final char HWall = '━';

    public static final char VWall = '┃';

    public static final char InvHWallDown = '┬';
    public static final char InvHWallUp = '┴';

    public static final char InvVWallRight = '├';
    public static final char InvVWallLeft = '┤';

    public static final char InvLPCorner = '┌';
    public static final char InvLDorner = '└';
    public static final char InvRPorner = '┐';
    public static final char InvRDorner = '┘';

    public static final char PointerUp = '⏶';

    public static final char NextRoom = Symbols.ARROW_DOWN;

    public static final char BackRoom = Symbols.ARROW_UP;

    public static final char[] RoomEntityAtlas = {NextRoom, BackRoom};

    public static final char[] ProgessBar = {'◯','◔','◑','◕','●'};

    public static final char LongSword = '╁';

    public static final char ShortSword = '╈';

    public static final char[] MeleeAtlas = {LongSword, ShortSword};

    public static final char Bow = '⟭';

    public static final char[] RangeAtlas = {Bow};

    public static String PlayerInfo =

            "Player: " + Player.nickName
            + "HP: " + Player.HP
            + "MP: " + Player.MP
            +"Level: " + Player.Level
            + "Room: " + Player.CurrentRoom;

    public static final char chair = '◎';

    public static final char RandomChest = '⍰';

    public static final char table_hex = '⎔';

    public static final char table = '⎕';

    public static final char table_rect = '⌷';

    public static final char Snake = '⟆';

    public static final char[] FurnitureAtlas = {chair, table_rect, table, table_hex};

    public static final char[][] EntityAtlas = {RoomEntityAtlas};

    public static final char[][] PropAtlas = {FurnitureAtlas};

    public static final char[][] WeaponAtlas = {MeleeAtlas, RangeAtlas};

    public static final char[][][] EverythingAtlas = {WeaponAtlas, PropAtlas, EntityAtlas};

    public static HashMap<Character, String> ModelNameMap = new HashMap<>();

    public static String UpdatePlayerInfo() {
        return "Player: " + Colors.GREEN_BRIGHT + Player.nickName + " "
                + Colors.R + "HP: " + Colors.RED_BRIGHT + Player.HP + " "
                + Colors.R + "MP: " + Colors.BLUE_BRIGHT + Player.MP + " "
                + Colors.R + "Level: " + Colors.CYAN + Player.Level + " "
                + Colors.R + "Room: " + Colors.MAGENTA + Player.CurrentRoom + " ";
    }

    public static void MakeMap() throws IllegalAccessException {

        InputStream csvfile = GameResources.class.getResourceAsStream("Roguera | CharAtlas.csv");

        String[] CharName = null;

        try {
            String maps = new String(csvfile.readAllBytes());
            CharName = maps.split(",|\r\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 1; i < (CharName != null ? CharName.length : 0); i+=2){
            char s = CharName[i-1].charAt(0);
            String n = CharName[i];
            ModelNameMap.put(s, n);
        }


    }
}
