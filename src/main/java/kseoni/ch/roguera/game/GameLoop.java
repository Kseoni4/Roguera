package kseoni.ch.roguera.game;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.CoordinateDrawer;
import kseoni.ch.roguera.input.KeyInput;
import kseoni.ch.roguera.map.*;
import kseoni.ch.roguera.graphics.ui.MapDrawer;
import kseoni.ch.roguera.graphics.sprites.TextSprite;

public class GameLoop {

    private final Player player;

    private final PlayerController playerController;

    private final MapDrawer mapDrawer;

    private final Dungeon dungeon;

    private Floor floor;

    private Room room;

    public GameLoop(){
        mapDrawer = new MapDrawer();
        player = new Player("Player", new TextSprite('@', TextColor.ANSI.GREEN_BRIGHT, null));
        playerController = new PlayerController(player);
        dungeon = Dungeon.get();
        floor = dungeon.currentFloor();
        room = floor.currentRoom();
    }

    public void init(){
        player.setPosition(new Position(1,2));
        room.getCell(player.getPosition()).placeObject(player);

        /*
        CoordinateDrawer drawer = new CoordinateDrawer();
        for(int x = 0; x < 10; x++) {
            //drawer.draw(new Cell(new Position(x+2, 0), new Wall(new TextSprite(String.valueOf(x).charAt(0)))));
        }
        for(int y = 0; y < 10; y++) {
            //drawer.draw(new Cell(new Position(0, y+1), new Wall(new TextSprite(String.valueOf(y).charAt(0)))));
        }*/
        for(Room room : floor.getRooms()){
            System.out.println("Draw room "+room);
            for(Cell cell : room.getCells().values()) {
                mapDrawer.draw(cell, room.getRoomLeftTopPosition());
            }
        }
        mapDrawer.refresh();
    }


    public void start() {
        while (Window.get().isNotClosed()) {
            System.out.println("Await input");
            KeyType key = KeyInput.get().getKeyType();
            playerController.movePlayer(key);

            for (Cell cell : room.getCells().values()) {
                mapDrawer.draw(cell, room.getRoomLeftTopPosition());
         }
            mapDrawer.refresh();
        }
        Window.get().close();
    }
}
