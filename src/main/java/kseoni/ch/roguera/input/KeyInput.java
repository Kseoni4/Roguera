package kseoni.ch.roguera.input;

import com.googlecode.lanterna.input.KeyStroke;
import kseoni.ch.roguera.rendering.Window;

public class KeyInput {

    private static Window window;

    static {
        window = Window.get();
    }

    public static KeyStroke get(){
        return window.keyInput();
    }

}
