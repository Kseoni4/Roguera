package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.input.Cursor;
import com.rogurea.main.input.Input;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.resources.UIContainer;

import java.io.IOException;
import java.util.HashMap;

/**
 * Абстрактный класс {@code Menu} скрывает стандартные
 * функции для отображения окна и даёт возможность
 * переопределения только того, что конкретно должно отрисовываться
 * в окне и какая логика работы осуществляется.
 */
public abstract class Menu implements IViewBlock, IMenu {

    protected TerminalPosition topLeftPosition;

    protected TerminalPosition RelativePosition;

    protected String MenuTitle;

    protected TextGraphics MenuGraphics;

    protected Position _topLeftPosition;

    protected Position CursorPosition;

    protected TerminalSize MenuSize;

    protected TerminalSize MenuBorderSize;

    protected boolean hide;

    protected boolean hideContext;

    protected char Pointer;

    protected String[] MenuOptions;

    protected KeyStroke Key;

    protected char MenuKey;

    protected HashMap<String, Character> MenuResources;

    protected int xOffset, yOffset;

    protected String Selected = " ";

    protected UIContainer menuContainer;

    public Menu(UIContainer menuContainer) {
        ViewObjects.ViewBlocks.add(this);
        UnpackUIContainer(menuContainer);
    }

    private void UnpackUIContainer(UIContainer menuContainer){
        MenuTitle = menuContainer.getMenuTitle();
        _topLeftPosition = menuContainer.getTopLeftPosition();
        CursorPosition = menuContainer.getCursorPosition();
        MenuSize = new TerminalSize(menuContainer.getMenuSize().x, menuContainer.getMenuSize().y);
        MenuBorderSize = new TerminalSize(menuContainer.getMenuBorderSize().x, menuContainer.getMenuBorderSize().y);
        hide = true;
        hideContext = true;
        MenuOptions = menuContainer.getMenuOptions();
        MenuKey = menuContainer.getMenuKey();
        MenuResources = menuContainer.getMenuResources();
        this.menuContainer = menuContainer;
    }

    public void setDefaultOffsets(int xOffset, int yOffset){
        this.xOffset = xOffset;

        this.yOffset = yOffset;

        this._topLeftPosition = this._topLeftPosition.getRelative(xOffset, yOffset);
    }

    @Override
    public final void Init() {
        topLeftPosition = new TerminalPosition(_topLeftPosition.x, _topLeftPosition.y);

        System.out.println(topLeftPosition.toString());

        try {
            MenuGraphics = TerminalView.terminal.newTextGraphics();
            menuContainer.setUIGraphics(MenuGraphics);

            System.out.println(MenuGraphics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public final void Draw() {
        if (!hide){

            RelativePosition = topLeftPosition.withRelative(Dungeon.GetCurrentRoom().RoomStructure[0].length,0);

            TerminalView.InitGraphics(MenuGraphics, RelativePosition,MenuSize);

            Borders();

            MenuContent();

            PutCursorOnPos(TerminalView.CurrentPointerPosition);

            if(!hideContext){
                MenuContextContent();
            }
        }
    }

    private void Borders(){
        MenuGraphics.setBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"))
                    .fillRectangle(RelativePosition, MenuSize.withRelative(0,0), MapEditor.EmptyCell)
                    .drawRectangle(RelativePosition.withRelative(-1,-2), MenuBorderSize, '█')
                    .putCSIStyledString(RelativePosition.getColumn(), RelativePosition.getRow()-2,Colors.WHITE_BRIGHT + MenuTitle);
    }

    @Override
    public final void Reset() {
        MenuGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        TerminalView.InitGraphics(MenuGraphics, topLeftPosition.withRelative(-1,-2), MenuBorderSize);

        Cursor.CursorPos = topLeftPosition.withRelative(CursorPosition.x, CursorPosition.y);
    }

    @Override
    public final void show() {
        Enable();

        System.out.println("Open " + MenuTitle);

        TerminalView.CurrentPointerPosition = Cursor.CursorPos;

        getControl();

        TerminalView.ReDrawAll(this);
    }

    private void Enable(){
        hide = false;
    }

    private void EnableContext(){
        hideContext = false;
    }

    private void Disable(){
        hide = true;
    }

    private void DisableContext(){
        hide = true;
    }

    /** Get user input control */

    private void getControl() {

        Pointer = MenuResources.get("MainPointer");

        System.out.println(MenuTitle+"//Hide is " + hide);

        while(!hide){
            Draw.call(this);

            Cursor.CursorPos = RelativePosition.withRelative(CursorPosition.x, CursorPosition.y);

            Key = Input.GetKey();

            System.out.println(MenuTitle + "//Get focus on");

            System.out.println("CurPos: " + RelativePosition.getRow() + " " + RelativePosition.getColumn());

            try{
                if(Key.getCharacter().equals(MenuKey)){
                    System.out.println("Close " + MenuTitle);
                    Disable();
                    break;
                }
            }catch (NullPointerException ignored){}

            MenuActions();

            Draw.call(this);
        }
/*
        Draw.reset(this);
*/
    }

    private void PutCursorOnPos(TerminalPosition position){
        TerminalView.SetPointerIntoPosition(MenuGraphics, Pointer, position);
    }

    /* Open context menu and change focus of input */

    public void openContext() {
        EnableContext();

        Pointer = MenuResources.get("ContextPointer");

        while (!Selected.toLowerCase().equals("back")){

            Key = Input.GetKey();

            try{
                if(Key.getCharacter().equals(MenuKey))
                    break;
            }
            catch (NullPointerException ignored){
            }

            MenuContextActions();

            Draw.call(this);
        }

        DisableContext();
    }

    /* Custom implements of context content
    (e.g. what and how it will be draw) */

    protected void MenuContextContent(){

    }

    /* Custom implements of base menu content
    (e.g. what and how it will be draw) */

    protected void MenuContent(){

    }

    /* Custom implements of WHAT menu will be do */

    protected void MenuActions(){

    }

    /* Custom implements of WHAT context menu will be do */

    protected void MenuContextActions(){

    }
}
