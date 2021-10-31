package com.rogurea;

public class Roguera {

    public static boolean isDebug = false;

    public static boolean isSaveToLogFile = false;

    public static boolean isClearMap = false;

    public static void main(String[] args) {
        checkCLI(args);

        MainMenu.start(0);
    }
    private static void checkCLI(String[] args){
        for(String argument : args){
            if(argument.equals("--debug")){
                isDebug = true;
            }
            if(argument.equals("--savelog")){
                isSaveToLogFile = true;
            }
            if(argument.equals("--clearmap")){
                isClearMap = true;
            }
        }
    }
}
