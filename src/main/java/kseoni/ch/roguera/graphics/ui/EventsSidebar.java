package kseoni.ch.roguera.graphics.ui;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.game.EventLoop;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.render.RenderLayer;
import kseoni.ch.roguera.graphics.ui.layout.BorderStyle;
import kseoni.ch.roguera.graphics.ui.layout.Region;

import java.util.List;

/**
 * Правый sidebar — лента последних событий из {@link EventLoop#getEventLog()}.
 * Verbose-формат лога ([timestamp]EVENT Event(value=…)) сжимается до значения,
 * цвет подбирается по эвристике из текста.
 */
public class EventsSidebar extends Panel {

    private static final String EVENT_VALUE_PREFIX = "Event(value=";

    public EventsSidebar(Region region) {
        super(region, BorderStyle.SHARP, "Events");
    }

    @Override
    protected void renderContent(RenderLayer layer, Region content) {
        List<String> log = EventLoop.get().getEventLog();
        int maxRows = content.height();
        int width = content.width() - 2;
        if (maxRows <= 0 || width <= 0 || log.isEmpty()) {
            return;
        }

        int startIdx = Math.max(0, log.size() - maxRows);
        int x = content.x() + 1;
        int y = content.y();

        for (int i = startIdx; i < log.size(); i++) {
            String line = compact(log.get(i));
            if (line.length() > width) {
                line = line.substring(0, width - 1) + "…";
            }
            layer.putString(x, y, line, colorOf(line), Palette.BASE);
            y++;
            if (y >= content.bottom()) break;
        }
    }

    private String compact(String eventInfo) {
        int idx = eventInfo.indexOf(EVENT_VALUE_PREFIX);
        if (idx < 0) return eventInfo;
        String body = eventInfo.substring(idx + EVENT_VALUE_PREFIX.length());
        if (body.endsWith(")")) body = body.substring(0, body.length() - 1);
        return body;
    }

    private TextColor colorOf(String s) {
        if (s.contains("Door") || s.contains("door")) return Palette.MAUVE;
        if (s.contains("Window")) return Palette.BLUE;
        if (s.contains("Regenerate") || s.contains("regen")) return Palette.YELLOW;
        if (s.contains("removed") || s.contains("cleared")) return Palette.OVERLAY;
        return Palette.SUBTEXT;
    }
}
