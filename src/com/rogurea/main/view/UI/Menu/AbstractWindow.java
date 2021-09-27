/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.IViewBlock;
import com.rogurea.main.view.TerminalView;

import java.io.IOException;
import java.io.Serializable;

public abstract class AbstractWindow implements IViewBlock, Serializable {

    protected int WindowID;

    private final TerminalPosition TopLeftBasePoint;

    private TerminalPosition RelativeTopLeftPoint;

    protected TerminalPosition InnerTopLeftPoint;

    protected final TerminalSize WindowSize;

    private final TerminalSize WindowBorderSize;

    private TextGraphics WindowGraphics;

    public AbstractWindow(Position topLeftBasePoint, TerminalSize windowSize){
        this.TopLeftBasePoint = new TerminalPosition(topLeftBasePoint.x, topLeftBasePoint.y);
        this.WindowSize = windowSize;
        this.WindowBorderSize = windowSize;
        this.InnerTopLeftPoint = TopLeftBasePoint.withRelative(1,1);
        /*ViewObjects.ViewBlocks.add(this);*/
    }

    public void Show(){
        UpdatePosition();
        Draw.init(this);
        Draw.call(this);
        Input();
        Draw.reset(this);
        TerminalView.ReDrawAll(new IViewBlock[]{});
    }

    public AbstractWindow(){
        this.TopLeftBasePoint = new TerminalPosition(10, 10);
        this.WindowSize = new TerminalSize(10,10);
        this.WindowBorderSize = new TerminalSize(10,10);
        this.InnerTopLeftPoint = TopLeftBasePoint.withRelative(1,1);
    }

    protected void Content(){

    }

    protected void Input(){

    }

    @Override
    public void Init() {

        try {
            WindowGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            Debug.log("UI ERROR: Cannot setting up text graphics for window: " + WindowID);
        }
    }


    @Override
    public void Draw() {

        TerminalView.InitGraphics(WindowGraphics, RelativeTopLeftPoint, WindowSize);

        DrawBorders();

        Content();
    }

    private void DrawBorders(){
        WindowGraphics.setBackgroundColor(Colors.GetTextColor(Colors.B_GREYSCALE_233,"\u001b[48;5;"))
                .fillRectangle(RelativeTopLeftPoint, WindowSize, MapEditor.EmptyCell)
                .drawRectangle(RelativeTopLeftPoint, WindowBorderSize, '█')
                .putCSIStyledString(RelativeTopLeftPoint,Colors.WHITE_BRIGHT + WindowID);
    }

    @Override
    public void Reset() {
        WindowGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

        UpdatePosition();

        TerminalView.InitGraphics(WindowGraphics, RelativeTopLeftPoint, WindowSize);
    }

    private void UpdatePosition(){
        RelativeTopLeftPoint = TopLeftBasePoint.withRelative(Dungeon.GetCurrentRoom().RoomStructure[0].length, 0);
        InnerTopLeftPoint = RelativeTopLeftPoint.withRelative(1,1);
    }

    protected void PutElementOnPosition(Element element){
        WindowGraphics.putCSIStyledString(element.getTerminalPosition(), element.ElementTitle);
    }

    protected void PutStringOnPosition(String s, Position position){
        TerminalView.DrawBlockInTerminal(WindowGraphics, s, new TerminalPosition(position.x, position.y));
    }

    protected void PutStringOnPosition(String s, TerminalPosition position){
        TerminalView.DrawBlockInTerminal(WindowGraphics, s, position);
    }
}
