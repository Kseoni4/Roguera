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
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.ui.AnimationDrawer;
import kseoni.ch.roguera.graphics.ui.EventsSidebar;
import kseoni.ch.roguera.graphics.ui.FooterDrawer;
import kseoni.ch.roguera.graphics.ui.HeaderDrawer;
import kseoni.ch.roguera.graphics.ui.PlayerSidebar;
import kseoni.ch.roguera.graphics.ui.layout.BorderStyle;
import kseoni.ch.roguera.graphics.ui.layout.Camera;
import kseoni.ch.roguera.graphics.ui.layout.Layout;
import kseoni.ch.roguera.graphics.ui.parts.UIFrame;
import kseoni.ch.roguera.input.KeyInput;
import kseoni.ch.roguera.map.*;
import kseoni.ch.roguera.graphics.ui.MapDrawer;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.utils.Clock;
import kseoni.ch.roguera.utils.Convert;
import kseoni.ch.roguera.utils.ObjectPool;
import kseoni.ch.roguera.utils.SettingsLoader;
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

    private final boolean debugShowKeyInput;

    private final Layout layout;

    private final UIFrame mapFrame;

    private final Camera camera;

    private final HeaderDrawer headerDrawer;

    private final PlayerSidebar playerSidebar;

    private final EventsSidebar eventsSidebar;

    private final FooterDrawer footerDrawer;

    private final AnimationDrawer animationDrawer;

    private final Map<Character, Runnable> keyBindings = new HashMap<>(Map.of(
            'g', this::regenerateMap,
            'r', () -> redrawFloor(floor),
            'q', () -> Window.get().close()
    ));

    public GameLoop(){
        layout = new Layout(Window.get().getWight(), Window.get().getHeight());
        camera = new Camera(layout.getMain().inset(1), 2, new Position(0, 0));
        mapDrawer = new MapDrawer(layout.getMain().inset(1), camera);
        mapFrame = new UIFrame(layout.getMain(), BorderStyle.SHARP, "Map");
        player = new Player("Player", new TextSprite('@', TextColor.ANSI.GREEN_BRIGHT, Palette.BASE));
        playerController = new PlayerController(player);
        player.setPlayerController(playerController);
        dungeon = Dungeon.get();
        floor = dungeon.currentFloor();
        room = floor.currentRoom();
        debugShowKeyInput = Boolean.parseBoolean(SettingsLoader.getSettingValue("debug.show.key-input"));
        headerDrawer = new HeaderDrawer(player, layout.getHeader());
        playerSidebar = new PlayerSidebar(layout.getSidebarLeft(), player);
        eventsSidebar = new EventsSidebar(layout.getSidebarRight());
        footerDrawer = new FooterDrawer(layout.getFooter());
        animationDrawer = new AnimationDrawer(camera, player);
    }

    public void init(){
        player.setPosition(new Position(1,2));
        room.getCell(player.getPosition()).placeObject(player);
        camera.centerOn(playerWorldPos());
        mapFrame.render(Window.get().getRenderLayer(TGLayer.UI));
        redrawFloor(floor);
    }

    private Position playerWorldPos() {
        return Convert.toGlobalPosition(room.getRoomLeftTopPosition(), player.getPosition());
    }


    @SneakyThrows
    public void start() {

        long frameStart = System.nanoTime();

        while (Window.get().isNotClosed()) {

            pollingEvents();

            animationDrawer.draw(System.currentTimeMillis(), floor, room);
            headerDrawer.draw();
            playerSidebar.render();
            eventsSidebar.render();
            footerDrawer.draw();
            Window.get().refresh();

            Clock.getInstance().tick(frameStart);
            frameStart = System.nanoTime();

        }
        Window.get().close();

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        System.out.println("Used Memory: " + usedMemory / 1024 + " KB");
        System.out.println("Free Memory: " + freeMemory / 1024 + " KB");
        System.out.println("Total Memory: " + totalMemory / 1024 + " KB");

    }

    private void pollingEvents(){
        for (Event<?> event : EventLoop.get().pollEvents()) {

            if(event.getValue() instanceof KeyStroke keyRaw){
                if(debugShowKeyInput)
                    System.out.println("Get input "+keyRaw);

                if(keyRaw.getKeyType().equals(KeyType.Character)) {
                    char key = Character.toLowerCase(keyRaw.getCharacter());

                    if(keyBindings.containsKey(key))
                        keyBindings.get(key).run();
                }

                if(keyRaw.getKeyType().toString().startsWith("Arrow")) {
                    KeyType keyType = keyRaw.getKeyType();
                    playerController.movePlayer(keyType);
                }

                if(Dungeon.get().currentFloor().currentRoom() != room) {
                    room = Dungeon.get().currentFloor().currentRoom();
                }

                camera.follow(playerWorldPos());
                redrawFloor(floor);
            }
            EventLoop.get().getEvents().remove(event);
        }
    }

    private void drawRoom(Room room) {
        if(Boolean.parseBoolean(SettingsLoader.getSettingValue("debug.show.draw-room")))
            System.out.println("Draw room "+room);

        for (Cell cell : room.getCells().values()) {
            mapDrawer.draw(cell, room.getRoomLeftTopPosition());
        }
    }

    public void redrawFloor(Floor floor) {
        mapDrawer.clear();
        for(Room room : floor.getRooms()){
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

        camera.centerOn(playerWorldPos());
        redrawFloor(floor);
    }
}
