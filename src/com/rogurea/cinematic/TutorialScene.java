package com.rogurea.cinematic;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyType;
import com.rogurea.base.Debug;
import com.rogurea.base.Entity;
import com.rogurea.exceptions.InterruptedSequence;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.gamemap.Room;
import com.rogurea.input.Input;
import com.rogurea.items.Chest;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;
import com.rogurea.view.TerminalView;
import com.rogurea.view.ViewObjects;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TutorialScene {

    private Actor teacher;

    private Actor player;

    private final Room sceneField;

    private TextGraphics tutorialGraphics;

    private final Position actorTextPosition = new Position(10, 8);

    private ArrayList<String> teacherLines = new ArrayList<>();

    private static boolean isAction = false;

    public TutorialScene(Room sceneField){
        this.sceneField = sceneField;
        prepareAssets();
    }

    private void prepareAssets(){
        try {
            tutorialGraphics = TerminalView.terminal.newTextGraphics();
            loadTexts();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ViewObjects.mapView.setRoom(sceneField);

        teacher = new Actor("teacher", new Model("teacher", 'T'), sceneField);

        player = new Actor("player", Dungeon.player.model, sceneField);



        teacher.placeObject(sceneField.getCell(new Position(0,5)));

        teacher.addAction(() -> {
            Position startPosition = teacher.cellPosition;

            for(int i = 1; i < TerminalView.windowWight/2; i+=2){
                teacher.moveTo(startPosition.getRelative(i,0));
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startSequence(){
        try {
            isAction = true;
            teacher.doSequence(0);
            TimeUnit.MILLISECONDS.sleep(150);

            if(ViewObjects.getTrimString(Dungeon.player.getName()).startsWith("Player")) {
                printTextAndWait(teacherLines.get(0), actorTextPosition);
            }

            printTextAndWait(teacherLines.get(1), actorTextPosition);

            printTextAndWait("Ежели ты бывалый герой, то жми escape из этого обучения и вперёд на подвиги! >", actorTextPosition);

            printTextAndWait(teacherLines.get(2), actorTextPosition);

            player.placeObject(sceneField.getCell(teacher.cellPosition.getRelative(-5,0)));

            Draw.call(ViewObjects.mapView);

            TimeUnit.MILLISECONDS.sleep(150);

            for(int i = 3; i < teacherLines.size()-2; i++){
                printTextAndWait(teacherLines.get(i), actorTextPosition);
                if(i == 5){
                    sceneField.getCell(teacher.cellPosition.getRelative(5,0)).putIntoCell(new Entity(new Model("trd", Colors.ORANGE, 'T')));
                    Draw.call(ViewObjects.mapView);
                }
                if(i == 6){
                    sceneField.getCell(teacher.cellPosition.getRelative(6,0)).putIntoCell(new Entity(Chest.chest));
                    Draw.call(ViewObjects.mapView);
                }
                if(i == 8){
                    sceneField.getCell(teacher.cellPosition.getRelative(7,0)).putIntoCell(new Entity(new Model("Gold", Colors.GOLDEN, '$')));
                    Draw.call(ViewObjects.mapView);
                }
                if(i == 9){
                    sceneField.getCell(player.cellPosition.getRelative(-2,0)).putIntoCell(new Entity(new Model("foe", Colors.RED_BRIGHT, 'B')));
                    Draw.call(ViewObjects.mapView);
                }
                if(i == 10){
                    TerminalView.drawBlockInTerminal(tutorialGraphics, Colors.VIOLET+"SCORE",new Position(TerminalView.windowWight/2,0));
                    Draw.flush();
                }
            }

            printTextAndWait(teacherLines.get(teacherLines.size()-2), actorTextPosition);

            sceneField.getCell(teacher.cellPosition.getRelative(-1,0)).putIntoCell(new Entity(GameResources.getModel("ShortSword").changeColor(Colors.BROWN)));

            Draw.call(ViewObjects.mapView);

            printTextAndWait(teacherLines.get(teacherLines.size()-1), actorTextPosition);

            Draw.clear();

        } catch (InterruptedException | InterruptedSequence e){
            Debug.toLog("[TUTORIAL][STATUS]Tutorial skipped");
            Draw.clear();
        }
        isAction = false;
    }

    private void printTextAndWait(String text, Position position) throws InterruptedSequence{
        try {
            printText(text, position);

            while(true) {
                KeyType key = Input.waitForInput().get().getKeyType();

                if (key.equals(KeyType.Escape)) {
                    isAction = false;
                    throw new InterruptedSequence("");
                }

                if (key.equals(KeyType.Enter)) {
                    break;
                }
            }

            for(int i = 0; i < text.split("#").length; i++) {
                clearLine(actorTextPosition.y+i);
            }
        } catch (InterruptedException e) {
            Debug.toLog("");
        }
    }

    private void printText(String text, Position startPosition) throws InterruptedException {
        int row = 0;
        String[] textRows = text.split("#");
        for(String s : textRows) {
            int col = 0;
            for (char liter : s.toCharArray()) {
                TerminalView.putCharInTerminal(tutorialGraphics, new TextCharacter(liter), startPosition.getRelative(col,row));
                TimeUnit.MILLISECONDS.sleep(10);
                Draw.flush();
                col++;
            }
            row++;
        }
    }
    private void clearLine(int row){
        for(int i = 0; i < TerminalView.windowWight; i++) {
            TerminalView.drawBlockInTerminal(tutorialGraphics, " ", new TerminalPosition(i, row));
            Draw.flush();
        }
    }

    private void loadTexts() throws IOException {

        InputStream isr = GameResources.class.getResourceAsStream("assets/teacherlines.res");

        BufferedInputStream bis = new BufferedInputStream(isr);

        String file = new String(bis.readAllBytes(), StandardCharsets.UTF_8);

        teacherLines = new ArrayList<>(List.of(file.split("\n")));
    }

    public static boolean isAction(){
        return isAction;
    }
}
