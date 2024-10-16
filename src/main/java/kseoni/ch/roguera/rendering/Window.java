package kseoni.ch.roguera.rendering;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import lombok.SneakyThrows;

public class Window {

    private final Terminal terminal;

    private static Window INSTANCE;

    @SneakyThrows
    private Window(int width, int height, String title) {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(width, height));
        factory.setTerminalEmulatorTitle(title);

        this.terminal = factory.createTerminal();
        this.terminal.enterPrivateMode();
        this.terminal.flush();
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
    public boolean isClosed(){
        return terminal == null;
    }

    @SneakyThrows
    public void close(){
        terminal.exitPrivateMode();
        terminal.close();
    }
    @SneakyThrows
    public void clearScreen(){
        terminal.clearScreen();
        terminal.flush();
    }
}
