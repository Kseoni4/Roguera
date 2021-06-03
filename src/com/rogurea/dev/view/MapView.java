package com.rogurea.dev.view;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Room;
import com.rogurea.dev.player.Player;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Item;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.resources.Colors;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Predicate;

public class MapView implements IViewBlock {

    private TextGraphics MapViewGraphics = null;

    private Room currentRoom;

    public MapView(){
        ViewObjects.ViewBlocks.add(this);
    }

    public void setRoom(Room room){
        this.currentRoom = room;
    }

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
                cell -> TerminalView.PutCharInTerminal(MapViewGraphics, cell.getFromCell().getModel(), cell.position)
        );
    }

    public void DrawAround(){
        for(Cell cell : Dungeon.player.lookAround()){
            try {
                TerminalView.PutCharInTerminal(MapViewGraphics, cell.getFromCell().getModel(), cell.position);
            } catch (NullPointerException e){
                continue;
            }
        }
        TerminalView.PutCharInTerminal(MapViewGraphics, Dungeon.player.getModel(), Dungeon.player.PlayerPosition);
        Draw.flush();
    }

    @Override
    public void Reset() {

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