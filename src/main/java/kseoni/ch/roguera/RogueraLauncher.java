package kseoni.ch.roguera;

import kseoni.ch.roguera.game.GameLoop;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.utils.SettingsLoader;

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