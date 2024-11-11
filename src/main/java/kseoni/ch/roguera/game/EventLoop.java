package kseoni.ch.roguera.game;

import com.googlecode.lanterna.input.KeyStroke;
import kseoni.ch.roguera.base.Event;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.input.KeyInput;
import kseoni.ch.roguera.utils.Clock;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoop {

    private final ExecutorService executorService;

    private final List<String> eventLog;

    @Getter
    private Queue<Event<?>> events;

    private static final EventLoop INSTANCE = new EventLoop();

    public static EventLoop get(){
        return INSTANCE;
    }

    private EventLoop(){
        events = new ArrayDeque<>();
        executorService = Executors.newSingleThreadExecutor();
        eventLog = new ArrayList<>();
    }

    public Queue<Event<?>> pollEvents(){

        Event<KeyStroke> keyEvent = pollInput();

        if(Objects.nonNull(keyEvent)) {
            toLog(keyEvent);
            events.add(keyEvent);
        }

        return events;
    }

    private Event<KeyStroke> pollInput(){
        Optional<KeyStroke> keyPressed = KeyInput.get();
        return keyPressed.map(Event::new).orElse(null);
    }

    @NonNull
    public void send(Event<?> event){
        toLog(event);
        events.add(event);
    }

    @SneakyThrows
    private void init() {
        executorService.submit(() -> {
            System.out.println("Start event loop");

            long frameStart = System.nanoTime();

            while (Window.get().isNotClosed()) {
                Clock.getInstance().tick(frameStart);

                frameStart = System.nanoTime();
            }
        });
        executorService.shutdown();
    }

    private void toLog(Event<?> event){
       String eventInfo = String.format("[%s]EVENT %s", LocalDateTime.now(), event.toString());
       eventLog.add(eventInfo);
    }

    @SneakyThrows
    public void dumpEventLog(){
        BufferedWriter writer = new BufferedWriter(new FileWriter(LocalDate.now()+"_events.txt"));

        writer.write("Event Log --- Timestamp --- Event \n");
        for(String eventInfo : eventLog){
            writer.write(eventInfo);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
