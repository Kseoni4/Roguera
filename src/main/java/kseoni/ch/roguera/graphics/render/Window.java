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
import java.util.HashMap;
import java.util.Properties;

public class Window {

    private final TerminalScreen terminal;

    private static Window INSTANCE;

    private final HashMap<TGLayer, RenderLayer> graphicsMap;

    private boolean isClosed;

    @SneakyThrows
    private Window(String title) {
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

        TerminalSize gridSize = resolveGridSize(properties, fontConfiguration);

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(gridSize);
        factory.setTerminalEmulatorTitle(title);
        factory.setTerminalEmulatorFontConfiguration(fontConfiguration);
        factory.setPreferTerminalEmulator(true);
        factory.setTerminalEmulatorFrameAutoCloseTrigger(
                TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode
        );

        this.graphicsMap = new HashMap<>();
        this.terminal = factory.createScreen();
        this.graphicsMap.put(TGLayer.BACKGROUND, new RenderLayer(this.terminal.newTextGraphics()));
        this.graphicsMap.put(TGLayer.FOREGROUND, new RenderLayer(this.terminal.newTextGraphics()));
        this.graphicsMap.put(TGLayer.UI, new RenderLayer(this.terminal.newTextGraphics()));

        this.terminal.setCursorPosition(null);
        this.terminal.startScreen();

        fillBackdrop();
    }

    /**
     * Lanterna's TerminalSize is grid (cols × rows), not pixels. Этот метод позволяет
     * указать размер окна одним из двух способов:
     *
     *  - window.pixel.width / window.pixel.height — физические пиксели; cols/rows
     *    рассчитываются из метрик шрифта (cell = fontWidth × fontHeight).
     *  - window.size.width / window.size.height — колонки/строки напрямую (fallback,
     *    если pixel.* пустые).
     */
    private TerminalSize resolveGridSize(Properties props, SwingTerminalFontConfiguration font) {
        int charW = font.getFontWidth();
        int charH = font.getFontHeight();

        String pixW = props.getProperty("window.pixel.width", "").trim();
        String pixH = props.getProperty("window.pixel.height", "").trim();

        int cols, rows;
        if (!pixW.isEmpty() && !pixH.isEmpty()) {
            cols = Math.max(1, Integer.parseInt(pixW) / charW);
            rows = Math.max(1, Integer.parseInt(pixH) / charH);
        } else {
            cols = Integer.parseInt(props.getProperty("window.size.width"));
            rows = Integer.parseInt(props.getProperty("window.size.height"));
        }

        System.out.printf(
                "Window grid: %d×%d (cell %d×%dpx ≈ %d×%dpx)%n",
                cols, rows, charW, charH, cols * charW, rows * charH
        );

        return new TerminalSize(cols, rows);
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

    public static Window create(String title) {
        if (INSTANCE == null) {
            INSTANCE = new Window(title);
            TerminalSize size = INSTANCE.terminal.getTerminalSize();
            EventLoop.get().send(
                    Event.raise(String.format(
                            "Window grid %d×%d created",
                            size.getColumns(), size.getRows()
                    ))
            );
        }
        return INSTANCE;
    }

    public static Window get() {
        try {
            if (INSTANCE == null) {
                throw new IllegalStateException("Window hasn't been initialized yet.");
            }
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
        return INSTANCE;
    }

    public RenderLayer getRenderLayer(TGLayer layer) {
        return graphicsMap.get(layer);
    }

    @SneakyThrows
    public KeyStroke keyInput() {
        return terminal.pollInput();
    }

    @SneakyThrows
    public boolean isNotClosed() {
        return !this.isClosed;
    }

    @SneakyThrows
    public void close() {
        terminal.stopScreen();
        this.isClosed = true;
        EventLoop.get().send(Event.raise("Window closed"));
    }

    @SneakyThrows
    public void clearScreen() {
        terminal.clear();
        fillBackdrop();
    }

    @SneakyThrows
    public void refresh() {
        terminal.refresh();
    }

    public int getWight() {
        return terminal.getTerminalSize().getColumns();
    }

    public int getHeight() {
        return terminal.getTerminalSize().getRows();
    }

    public TerminalScreen getRawScreen() {
        return terminal;
    }
}
