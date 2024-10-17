package kseoni.ch.roguera.rendering;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Properties;

public class Window {

    private final TerminalScreen terminal;

    private static Window INSTANCE;

    private final HashMap<TGLayer, RenderLayer> graphicsMap;

    @SneakyThrows
    private Window(int width, int height, String title) {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(width, height));
        factory.setTerminalEmulatorTitle(title);

        Properties properties = SettingsLoader.load(SettingsLoader.Settings.GAME_SETTINGS);

        Font gameFont = Font.createFont(
                Font.TRUETYPE_FONT, new File(properties.getProperty("font.filepath"))
        ).deriveFont(
                Float.parseFloat(properties.getProperty("font.size"))
        );


        SwingTerminalFontConfiguration fontConfiguration = new SwingTerminalFontConfiguration(
                false,
                AWTTerminalFontConfiguration.BoldMode.NOTHING,
                gameFont
        );

        factory.setTerminalEmulatorFontConfiguration(fontConfiguration);


        this.graphicsMap = new HashMap<>();

        this.terminal = factory.createScreen();
        this.graphicsMap.put(TGLayer.BACKGROUND, new RenderLayer(this.terminal.newTextGraphics()));
        this.graphicsMap.put(TGLayer.FOREGROUND, new RenderLayer(this.terminal.newTextGraphics()));
        this.graphicsMap.put(TGLayer.UI, new RenderLayer(this.terminal.newTextGraphics()));

        this.terminal.setCursorPosition(null);
        this.terminal.startScreen();
    }

    public static Window create(int width, int height, String title){
        if(INSTANCE==null){
            INSTANCE = new Window(width, height, title);
        }
        return INSTANCE;
    }

    public static Window get(){
        try{
            if(INSTANCE==null){
                throw new IllegalStateException("Window hasn't been initialized yet.");
            }
        } catch (IllegalStateException e){
            System.err.println(e.getMessage());
        }
        return INSTANCE;
    }

    public RenderLayer getRenderLayer(TGLayer layer){
        return graphicsMap.get(layer);
    }
    @SneakyThrows
    public KeyStroke keyInput(){
        return terminal.readInput();
    }

    @SneakyThrows
    public void close(){
        terminal.close();
    }
    @SneakyThrows
    public void clearScreen(){
        terminal.clear();
    }
    @SneakyThrows
    public void refresh(){
        terminal.refresh();
    }
}
