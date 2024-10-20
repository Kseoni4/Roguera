package kseoni.ch.roguera.utils;

import kseoni.ch.roguera.RogueraLauncher;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class SettingsLoader {

    public enum Settings
    {
        GAME_SETTINGS("game-settings.properties");

        private final String fileName;

        Settings(String fileName){
            this.fileName = fileName;
        }
    }

    private static Properties settings;

    public static Properties load(Settings settingsName) {
        if(settings == null){
            Properties properties = new Properties();
            try {
                properties.load(RogueraLauncher.class.getClassLoader().getResourceAsStream(settingsName.fileName));
                settings = properties;
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        return settings;
    }

    public static String getSettingValue(String settingName){
        if(settings == null){
            load(Settings.GAME_SETTINGS);
        }
        return settings.getProperty(settingName);
    }
}
