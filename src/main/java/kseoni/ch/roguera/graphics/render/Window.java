package kseoni.ch.roguera.graphics.render;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.*;
import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.utils.SettingsLoader;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

public class Window {

    private final TerminalScreen terminal;

    private static Window INSTANCE;

    private final HashMap<TGLayer, RenderLayer> graphicsMap;

    private boolean isClosed;

    //private final SwingTerminalFrame swingTerminalFrame;

    @SneakyThrows
    private Window(int width, int height, String title) {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(width, height));
        factory.setTerminalEmulatorTitle(title);

        Properties properties = SettingsLoader.load(SettingsLoader.Settings.GAME_SETTINGS);

        Font gameFont = Font.createFont(
                Font.TRUETYPE_FONT, getClass().getResourceAsStream(properties.getProperty("font.filepath"))
        ).deriveFont(
                Float.parseFloat(properties.getProperty("font.size"))
        );


        SwingTerminalFontConfiguration fontConfiguration = new SwingTerminalFontConfiguration(
                false,
                AWTTerminalFontConfiguration.BoldMode.NOTHING,
                gameFont
        );

        factory.setTerminalEmulatorFontConfiguration(fontConfiguration);

        factory.setPreferTerminalEmulator(true);

        factory.setTerminalEmulatorFrameAutoCloseTrigger(
                TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode
        );

        this.graphicsMap = new HashMap<>();

        this.terminal = factory.createScreen();
        this.graphicsMap.put(TGLayer.BACKGROUND, new RenderLayer(this.terminal
                .newTextGraphics()
                //.newTextGraphics(new TerminalPosition(0,0),
                //        new TerminalSize(50, 30)))
        ));
        this.graphicsMap.put(TGLayer.FOREGROUND, new RenderLayer(this.terminal.newTextGraphics()));
        this.graphicsMap.put(TGLayer.UI, new RenderLayer(this.terminal.newTextGraphics()));

        this.terminal.setCursorPosition(null);
        this.terminal.startScreen();

        fillBackdrop();

        //this.swingTerminalFrame = terminal.getTerminal();
    }

    private void fillBackdrop() {
        TextGraphics gfx = terminal.newTextGraphics();
        gfx.setBackgroundColor(Palette.BASE);
        gfx.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                terminal.getTerminalSize(),
                new TextCharacter(' ').withBackgroundColor(Palette.BASE)
        );
    }

    public static Window create(int width, int height, String title){
        if(INSTANCE == null){
            INSTANCE = new Window(width, height, title);
            EventLoop.get().send(
                    Event.raise(String.format("Window W: %d H: %d has been created", width, height))
            );
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
        return terminal.pollInput();
    }

    @SneakyThrows
    public boolean isNotClosed(){
        return !this.isClosed;
    }

    @SneakyThrows
    public void close(){
        terminal.stopScreen();
        this.isClosed = true;
        EventLoop.get().send(Event.raise("Window closed"));
    }
    @SneakyThrows
    public void clearScreen(){
        terminal.clear();
        fillBackdrop();
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

    public TerminalScreen getRawScreen() { return terminal; }
}
