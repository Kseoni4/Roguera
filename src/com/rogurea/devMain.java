package com.rogurea;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.EditorEntity;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.mapgenerate.MapEditor;
import com.rogurea.dev.player.KeyController;
import com.rogurea.dev.player.MoveController;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.GameResources;
import com.rogurea.dev.resources.Model;
import com.rogurea.dev.view.TerminalView;
import com.rogurea.dev.view.ViewObjects;

import java.io.IOException;

public class devMain {

    public static void main(String[] args) throws IOException {
        TerminalView.InitTerminal();

        GameResources.LoadResources();

        ViewObjects.LoadViewObjects();

        TerminalView.SetGameScreen();

        Dungeon.Generate();

        //ViewObjects.mapView.setRoom(new Room(0, 12,12));

        ViewObjects.mapView.Draw();

        TerminalView.terminal.flush();

        while (true) {

            TerminalView.keyStroke = TerminalView.terminal.readInput();

            if (TerminalView.keyStroke != null) {
                if (TerminalView.keyStroke.getKeyType() == KeyType.Character) {
                    KeyController.GetKey(TerminalView.keyStroke.getCharacter());
                }
                MoveController.MovePlayer(TerminalView.keyStroke.getKeyType());
            }
        }
    }
}
