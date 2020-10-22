package com.rogurea.main.resources;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;

public class MenuContainer {

    private final TerminalPosition topLeftPosition;

    private final TextGraphics textGUI;

    private char pointer;

    private int index;

    private int offset;

    private String[] OptionNames;

    public TerminalPosition getPosition(){
        return this.topLeftPosition;
    }

    public TextGraphics getTextGUI(){
        return this.textGUI;
    }

    public char getPointer(){
        return this.pointer;
    }

    public void changePointer(char pointer){
        this.pointer = pointer;
    }

    public int getIndex(){
        return this.index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getOffset(){
        return this.offset;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public String[] getOptionNames(){
        return this.OptionNames;
    }

    public void setOptionNames(String[] names){
        this.OptionNames = names;
    }

    public MenuContainer(TerminalPosition topLeftPosition, TextGraphics textGUI, char pointer,
                         int index, int offset){
        this.topLeftPosition = topLeftPosition;
        this.textGUI = textGUI;
        this.pointer = pointer;
        this.index = index;
        this.offset = offset;
    }

    public MenuContainer(TerminalPosition topLeftPosition, TextGraphics textGUI){
        this(topLeftPosition, textGUI, 'p', 0, 0);
    }
}
