package com.rogurea.main;

import java.util.Scanner;

public class Input {

    static Scanner getInput = new Scanner(System.in);

    public static int WaitFromPlayer(){

        System.out.print("-> ");
        return getInput.nextInt();
    }

}
