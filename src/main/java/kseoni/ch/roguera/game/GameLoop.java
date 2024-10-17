package kseoni.ch.roguera.game;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.controller.PlayerController;
import kseoni.ch.roguera.creature.Player;
import kseoni.ch.roguera.input.KeyInput;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.DungeonMap;
import kseoni.ch.roguera.graphics.ui.MapDrawer;
import kseoni.ch.roguera.graphics.sprites.TextSprite;

public class GameLoop {

    private final Player player;

    private final PlayerController playerController;

    private final MapDrawer mapDrawer;

    public GameLoop(){
        mapDrawer = new MapDrawer();
        player = new Player("Player", new TextSprite('@', TextColor.ANSI.GREEN_BRIGHT, null));
        playerController = new PlayerController(player);
    }

    public void init(){
        player.setPosition(new Position(0,0));
        DungeonMap.currentRoom()
                .getCell(new Position(0,0))
                .placeObject(player);

        for(Cell cell : DungeonMap.currentRoom().getCells().values()){
            mapDrawer.draw(cell);
        }
        mapDrawer.refresh();
    }


    public void start(){

        while (true){
            System.out.println("Await input");
            KeyType key = KeyInput.get().getKeyType();
            playerController.movePlayer(key);
            for(Cell cell : DungeonMap.currentRoom().getCells().values()){
                mapDrawer.draw(cell);
            }
            mapDrawer.refresh();
        }

    }
}
