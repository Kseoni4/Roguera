package kseoni.ch.roguera.graphics.render;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

public class Window {

    private final TerminalScreen terminal;

    private static Window INSTANCE;

    private final HashMap<TGLayer, RenderLayer> graphicsMap;

    private final SwingTerminalFrame swingTerminalFrame;

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
        this.graphicsMap.put(TGLayer.BACKGROUND, new RenderLayer(this.terminal
                .newTextGraphics()
                .newTextGraphics(new TerminalPosition(2,1),
                        new TerminalSize(50, 20))));
        this.graphicsMap.put(TGLayer.FOREGROUND, new RenderLayer(this.terminal.newTextGraphics()));
        this.graphicsMap.put(TGLayer.UI, new RenderLayer(this.terminal.newTextGraphics()));

        this.terminal.setCursorPosition(null);
        this.terminal.startScreen();

        this.swingTerminalFrame = (SwingTerminalFrame) terminal.getTerminal();
    }

    public static Window create(int width, int height, String title){
        if(INSTANCE == null){
            INSTANCE = new Window(width, height, title);
        }
        return INSTANCE;
    }

    public static Window get(){
        try{
            if(INSTANCE == null){
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
    public boolean isNotClosed(){
        return swingTerminalFrame.isDisplayable();
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

    public int getWight(){
        return terminal.getTerminalSize().getColumns();
    }

    public int getHeight(){
        return terminal.getTerminalSize().getRows();
    }
}
