package kseoni.ch.roguera;

import kseoni.ch.roguera.rendering.Window;

import java.io.IOException;
import java.util.Properties;

public class RogueraLauncher {

    public static void main(String[] args) throws InterruptedException {
        Properties properties = loadProperties("game-settings.properties");

        int width = Integer.parseInt(properties.getProperty("window.size.width"));
        int height = Integer.parseInt(properties.getProperty("window.size.height"));
        String title = properties.getProperty("window.title");

        Window.create(width, height, title);
    }

    private static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(RogueraLauncher.class.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}