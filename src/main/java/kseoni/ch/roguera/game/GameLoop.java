package kseoni.ch.roguera.game;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.game.creature.Player;
import kseoni.ch.roguera.game.entity.Scriptable;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.input.KeyInput;
import kseoni.ch.roguera.map.*;
import kseoni.ch.roguera.graphics.ui.MapDrawer;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.utils.Clock;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameLoop {

    private final Player player;

    private final PlayerController playerController;

    private final MapDrawer mapDrawer;

    private final Dungeon dungeon;

    private Floor floor;

    private Room room;

    private final Map<Character, Scriptable<?>> keyBindings = new HashMap<>(Map.of(
            'g', (G) -> regenerateMap(),
            'r', (G) -> redrawFloor(floor),
            'q', (G) -> Window.get().close()
    ));

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


    @SneakyThrows
    public void start() {

        long frameStart = System.nanoTime();

        while (Window.get().isNotClosed()) {

            for (Event<?> event : EventLoop.get().pollEvents()){
                System.out.println("Get event "+event);

                if(event.getValue() instanceof KeyStroke keyRaw){
                    System.out.println("Get input "+keyRaw);
                    KeyType key = keyRaw.getKeyType();

                    if(keyRaw.getKeyType().equals(KeyType.Character)) {
                        if(keyBindings.containsKey(keyRaw.getCharacter()))
                            keyBindings.get(keyRaw.getCharacter()).doAction(null);
                    }

                    if(keyRaw.getKeyType().toString().startsWith("Arrow"))
                        playerController.movePlayer(key);

                    if(Dungeon.get().currentFloor().currentRoom() != room){
                        drawRoom(room);
                        room = Dungeon.get().currentFloor().currentRoom();
                    }
                    drawRoom(room);
                }
                EventLoop.get().getEvents().remove(event);
            }
            Clock.getInstance().tick(frameStart);
            frameStart = System.nanoTime();

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

    private void regenerateMap(){
        EventLoop.get().send(Event.raise("Regenerate map signal"));
        mapDrawer.clear();
        floor.regenerate();
        playerController.reset();
        player.setPosition(new Position(1, 2));
        room = floor.currentRoom();
        room.getCell(player.getPosition()).placeObject(player);

        redrawFloor(floor);
    }
}
