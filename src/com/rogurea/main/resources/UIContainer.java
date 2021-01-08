package com.rogurea.main.resources;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.map.Position;

import java.io.Serializable;
import java.util.HashMap;

public class UIContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String MenuTitle;

    private transient TextGraphics UIGraphics;

    private Position topLeftPosition;

    private Position CursorPosition;

    private Position menuSize;

    private Position menuBorderSize;

    private String[] MenuOptions;

    private HashMap<String, Character> MenuResources;

    private HashMap<String, Integer> ExtraData;

    private char MenuKey;

    public UIContainer(String menuTitle, Position topLeftPosition,
                       Position cursorPosition, Position menuSize, Position menuBorderSize,
                       String[] menuOptions, HashMap<String, Character> menuResources, char menuKey) {
        this.MenuTitle = menuTitle;
        this.topLeftPosition = topLeftPosition;
        this.CursorPosition = cursorPosition;
        this.menuSize = menuSize;
        this.menuBorderSize = menuBorderSize;
        this.MenuOptions = menuOptions;
        this.MenuResources = menuResources;
        this.MenuKey = menuKey;
    }

    public UIContainer(){
        this.MenuTitle = "menuTitle";
        this.topLeftPosition = new Position(0,0);
        this.CursorPosition = new Position(0,0);
        this.menuSize = new Position(0,0);
        this.menuBorderSize = new Position(0,0);
        this.MenuOptions = new String[]{};
        this.MenuResources = new HashMap<>();
        this.MenuKey = ' ';
    }

    public void InitializeExtraData(){
        this.ExtraData = new HashMap<>();
    }

    public void setMenuTitle(String menuTitle) {
        MenuTitle = menuTitle;
    }

    public void setUIGraphics(TextGraphics UIGraphics) {this.UIGraphics = UIGraphics; }

    public void setTopLeftPosition(Position topLeftPosition) {
        this.topLeftPosition = topLeftPosition;
    }

    public void setCursorPosition(Position cursorPosition) {
        CursorPosition = cursorPosition;
    }

    public void setMenuSize(Position menuSize) {
        this.menuSize = menuSize;
    }

    public void setMenuBorderSize(Position menuBorderSize) {
        this.menuBorderSize = menuBorderSize;
    }

    public void setMenuOptions(String[] menuOptions) {
        MenuOptions = menuOptions;
    }

    public void setMenuResources(HashMap<String, Character> menuResources) {
        MenuResources = menuResources;
    }

    public void setMenuKey(char menuKey) {
        MenuKey = menuKey;
    }

    public void putExtraData(String key, int value){
        ExtraData.put(key, value);
    }

    public void replaceExtraData(String key, int value){
        ExtraData.replace(key, value);
    }

    public String getMenuTitle(){
        return MenuTitle;
    }

    public TextGraphics getUIGraphics() {return this.UIGraphics; }

    public Position getTopLeftPosition() {
        return topLeftPosition;
    }

    public Position getCursorPosition() {
        return CursorPosition;
    }

    public Position getMenuSize() {
        return menuSize;
    }

    public Position getMenuBorderSize() {
        return menuBorderSize;
    }

    public String[] getMenuOptions() {
        return MenuOptions;
    }

    public HashMap<String, Character> getMenuResources() {
        return MenuResources;
    }

    public char getMenuKey() {
        return MenuKey;
    }

    public int getExtraDataFromKey(String key){
        if(ExtraData.get(key) != null)
            return ExtraData.get(key);
        else
            return -1;
    }
}
