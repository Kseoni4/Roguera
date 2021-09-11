package com.rogurea.dev.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Room;

import java.io.IOException;

public class MapView implements IViewBlock {

    private TextGraphics MapViewGraphics = null;

    private Room currentRoom;

    public MapView(){
        ViewObjects.ViewBlocks.add(this);
    }

    public void setRoom(Room room){
        this.currentRoom = room;
    }

    public TerminalSize size = new TerminalSize(25,30);

    @Override
    public void Init() {
        try{
            MapViewGraphics = TerminalView.terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Draw() {
        currentRoom.getCells().forEach(
                cell -> TerminalView.putCharInTerminal(MapViewGraphics, cell.getFromCell().getModel(), cell.position)
        );
    }

    public void drawAround(){
        for(Cell cell : Dungeon.player.lookAround()){
            try {
                TerminalView.putCharInTerminal(MapViewGraphics, cell.getFromCell().getModel(), cell.position);
            } catch (NullPointerException ignored){
            }
        }
        TerminalView.putCharInTerminal(MapViewGraphics, Dungeon.player.getModel(), Dungeon.player.playerPosition);
        Draw.flush();
    }

    @Override
    public void Reset() {
        MapViewGraphics.fillRectangle(new TerminalPosition(0,0), size, ' ');
    }
    /*
    private TextGraphics MapDrawGraphics = null;

    private char cell;
    private final Position CellPosition = new Position();
    private HashMap<Integer, String> ColoredItemsID;
    private final Predicate<Item> GetItemByPosition = item -> item.ItemPosition.equals(CellPosition);

    public GameMapBlock(){
        ViewObjects.ViewBlocks.add(this);
    }

    public void Init(){
        try {
            MapDrawGraphics = com.rogurea.main.view.TerminalView.terminal.newTextGraphics();

            ColoredItemsID = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
     */
}