package kseoni.ch.roguera.input;

import com.googlecode.lanterna.input.KeyStroke;
import kseoni.ch.roguera.graphics.render.Window;

import java.util.Optional;

public class KeyInput {

    private static Window window;

    static {
        window = Window.get();
    }

    public static Optional<KeyStroke> get(){
        return Optional.ofNullable(window.keyInput());
    }

    public static Optional<KeyStroke> getWait() {
        return Optional.ofNullable(window.keyWaitInput());
    }

}
