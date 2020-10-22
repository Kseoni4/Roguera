package com.rogurea.main.resources;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.player.Player;

public class MenuContainer {

    private final TerminalPosition topLeftPosition;

    private final TextGraphics textGUI;

    private char pointer;

    private int indexoffset;

    private int indexcontext;

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

    public int getIndexoffset(){
        return this.indexoffset;
    }
    public int getIndexcontext(){ return this.indexcontext; }

    public void setIndexoffset(int index){ this.indexoffset = index; }
    public void setIndexcontext(int index){ this.indexcontext = index; }

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

    public int UpdateCursor(int index){
        while (index >= Player.Inventory.size()){
            index--;
        }
        return index;
    }

    public MenuContainer(TerminalPosition topLeftPosition, TextGraphics textGUI, char pointer,
                         int index, int offset){
        this.topLeftPosition = topLeftPosition;
        this.textGUI = textGUI;
        this.pointer = pointer;
        this.indexoffset = index;
        this.offset = offset;
    }

    public MenuContainer(TerminalPosition topLeftPosition, TextGraphics textGUI){
        this(topLeftPosition, textGUI, 'p', 0, 0);
    }
}
