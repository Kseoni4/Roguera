package kseoni.ch.roguera;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.GameLoop;
import kseoni.ch.roguera.input.KeyInput;
import kseoni.ch.roguera.items.Item;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.rendering.MapDrawer;
import kseoni.ch.roguera.rendering.TGLayer;
import kseoni.ch.roguera.rendering.TextSprite;
import kseoni.ch.roguera.rendering.Window;
import kseoni.ch.roguera.utils.ObjectPool;
import kseoni.ch.roguera.utils.SettingsLoader;

import java.io.IOException;
import java.util.Properties;

public class RogueraLauncher {

    public static void main(String[] args) {
        Properties properties = SettingsLoader.load(SettingsLoader.Settings.GAME_SETTINGS);

        int width = Integer.parseInt(properties.getProperty("window.size.width"));
        int height = Integer.parseInt(properties.getProperty("window.size.height"));
        String title = properties.getProperty("window.title");

        Window window = Window.create(width, height, title);

        GameLoop gameLoop = new GameLoop();

        gameLoop.init();

        gameLoop.start();

        window.refresh();
    }
}