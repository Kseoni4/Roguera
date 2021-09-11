package com.rogurea.dev.view;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.resources.Model;

import java.io.IOException;

public class BordersView implements IViewBlock {

    private TextGraphics BordersViewGraphics = null;

    private Position center;

    private Model borderVWall;

    private Model borderCRCorner;

    private Model borderHWall;

    public BordersView(){
        ViewObjects.ViewBlocks.add(this);
    }

    @Override
    public void Init() {
        try{
            BordersViewGraphics = TerminalView.terminal.newTextGraphics();
            center = new Position(ViewObjects.mapView.size.getColumns(), ViewObjects.mapView.size.getRows()/2);
            borderVWall = new Model("B_VWall", '║');
            borderHWall = new Model("B_HWall", '═');
            borderCRCorner = new Model("B_CRCorner",'╠');

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Draw() {
        try {
            for (int y = center.y; y >= 0; y--) {
                TerminalView.putCharInTerminal(BordersViewGraphics, borderVWall.get(), center.getRelative(0, y - center.y));
            }
            for (int y = center.y; y <= ViewObjects.mapView.size.getRows(); y++) {
                TerminalView.putCharInTerminal(BordersViewGraphics, borderVWall.get(), center.getRelative(0, y - center.y));
            }
            for (int x = center.x; x <= TerminalView.terminal.getTerminalSize().getColumns(); x++) {
                TerminalView.putCharInTerminal(BordersViewGraphics, borderHWall.get(), center.getRelative(x - center.x, -(center.y - 2)));
            }
            TerminalView.putCharInTerminal(BordersViewGraphics, borderCRCorner.get(), center.getRelative(0, -(center.y - 2)));
        } catch (IOException ignored){}
    }

    @Override
    public void Reset() {

    }
}
