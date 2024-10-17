package kseoni.ch.roguera.rendering;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import lombok.SneakyThrows;

import java.util.HashMap;

public class Window {

    private final Screen terminal;

    private static Window INSTANCE;

    private HashMap<TGLayer, TextGraphics> graphicsMap;

    @SneakyThrows
    private Window(int width, int height, String title) {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(width, height));
        factory.setTerminalEmulatorTitle(title);

        this.graphicsMap = new HashMap<>();

        this.terminal = factory.createScreen();
        this.graphicsMap.put(TGLayer.BACKGROUND, this.terminal.newTextGraphics());
        this.graphicsMap.put(TGLayer.FOREGROUND, this.terminal.newTextGraphics());
        this.graphicsMap.put(TGLayer.UI, this.terminal.newTextGraphics());

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

    public TextGraphics getLayer(TGLayer layer){
        return graphicsMap.get(layer);
    }

    @SneakyThrows
    public void close(){
        terminal.close();
    }
    @SneakyThrows
    public void clearScreen(){
        terminal.clear();
    }
}
