package kseoni.ch.roguera.game;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.graphics.render.Window;
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
        player.setPlayerController(playerController);
        dungeon = Dungeon.get();
        floor = dungeon.currentFloor();
        room = floor.currentRoom();
    }

    public void init(){
        player.setPosition(new Position(1,2));
        room.getCell(player.getPosition()).placeObject(player);
        redrawFloor(floor);
    }


    public void start() {
        while (Window.get().isNotClosed()) {
            System.out.println("Await input");

            KeyStroke keyRaw = KeyInput.get();
            KeyType key = keyRaw.getKeyType();

            if(keyRaw.getKeyType().equals(KeyType.Character) && keyRaw.getCharacter().equals('g')){
                mapDrawer.clear();
                floor.regenerate();
                playerController.reset();
                player.setPosition(new Position(1,2));
                room = floor.currentRoom();
                room.getCell(player.getPosition()).placeObject(player);

                redrawFloor(floor);
                continue;
            }

            if(keyRaw.getKeyType().equals(KeyType.Character) && keyRaw.getCharacter().equals('r')){
                redrawFloor(floor);
            }
            playerController.movePlayer(key);

            if(Dungeon.get().currentFloor().currentRoom() != room){
                drawRoom(room);
                room = Dungeon.get().currentFloor().currentRoom();
            }

            drawRoom(room);
        }
        Window.get().close();
    }

    private void drawRoom(Room room) {
        for (Cell cell : room.getCells().values()) {
            mapDrawer.draw(cell, room.getRoomLeftTopPosition());
        }
        mapDrawer.refresh();
    }

    public void redrawFloor(Floor floor) {
        mapDrawer.clear();
        for(Room room : floor.getRooms()){
            System.out.println("Draw room "+room);
            drawRoom(room);
        }
        mapDrawer.refresh();
    }
}
