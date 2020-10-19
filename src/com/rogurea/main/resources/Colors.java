package com.rogurea.main.resources;

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

    /* Background colors */

    public static final String B_RED_BRIGHT   =   "\u001b[48;5;9m";
    public static final String B_GREEN_BRIGHT =   "\u001b[48;5;10m";
    public static final String B_BLUE_BRIGHT  =   "\u001b[48;5;12m";
    public static final String B_MAGENTA      =   "\u001b[48;5;90m";
    public static final String B_CYAN         =   "\u001b[48;5;50m";
    public static final String B_ORANGE       =   "\u001b[48;5;214m";
    public static final String B_PINK         =   "\u001b[48;5;207m";
    public static final String B_VIOLET       =   "\u001b[48;5;200m";
}
