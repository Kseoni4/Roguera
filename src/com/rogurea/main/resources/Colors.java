package com.rogurea.main.resources;
import com.googlecode.lanterna.TextColor;

public class Colors {

    //Syntax for foreground - \u001b[38;5;#m
    //Syntax for background - \u001b[48;5;#m
    // \u001b = ESC;

    public static final String R = "\u001b[0m";

    /* Foreground colors */

    public static final String RED_BRIGHT   =   "\u001b[38;5;9m";
    public static final String GREEN_BRIGHT =   "\u001b[38;5;10m";
    public static final String BLUE_BRIGHT  =   "\u001b[38;5;12m";
    public static final String MAGENTA      =   "\u001b[38;5;90m";
    public static final String CYAN         =   "\u001b[38;5;50m";
    public static final String ORANGE       =   "\u001b[38;5;214m";
    public static final String PINK         =   "\u001b[38;5;207m";
    public static final String VIOLET       =   "\u001b[38;5;200m";
    public static final String BROWN        =   "\u001b[38;5;130m";
    public static final String GREY         =   "\u001b[38;5;253m";
    public static final String IRON         =   "\u001b[38;5;250m";
    public static final String COPPER       =   "\u001b[38;5;202m";
    public static final String GOLDEN       =   "\u001b[38;5;221m";
    public static final String DIAMOND      =   "\u001b[38;5;39m";
    public static final String DEEPBLUE     =   "\u001b[38;5;17m";
    public static final String WHITE_BRIGHT =   "\u001b[38;5;15m";

    /* Background colors */

    public static final String B_RED_BRIGHT   =   "\u001b[48;5;9m";
    public static final String B_GREEN_BRIGHT =   "\u001b[48;5;10m";
    public static final String B_BLUE_BRIGHT  =   "\u001b[48;5;12m";
    public static final String B_MAGENTA      =   "\u001b[48;5;90m";
    public static final String B_CYAN         =   "\u001b[48;5;50m";
    public static final String B_ORANGE       =   "\u001b[48;5;214m";
    public static final String B_PINK         =   "\u001b[48;5;207m";
    public static final String B_VIOLET       =   "\u001b[48;5;200m";
    public static final String B_DEEPBLUE     =   "\u001b[48;5;17m";
    public static final String B_DARKRED      =   "\u001b[48;5;52m";
    public static final String B_GREYSCALE_237 =   "\u001b[48;5;237m";
    public static final String B_GREYSCALE_233 =   "\u001b[48;5;233m";


    public static TextColor GetTextColor(String color, String target){

        color = color.replace(target, "#");
        color = color.replace("m", "");
        color = color.replace(";", "");
       return TextColor.Factory.fromString(color);
    }
}
