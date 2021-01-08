package com.rogurea.main.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.rogurea.Main;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.gamelogic.SavingSystem;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainMenu {

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    private static Panel BasePanel;

    private static Screen GUIWindow;

    public static MultiWindowTextGUI WindowsGUI;

    private static final BasicWindow MenuWindows = new BasicWindow();

    private static final BasicWindow NewGameWindow = new BasicWindow();

    private static TerminalPosition CenterOfScreen;

    public static void start(){
        try {
            defaultTerminalFactory.setTerminalEmulatorTitle("Roguera " + GameResources.version);

            GUIWindow = defaultTerminalFactory.createScreen();

            GUIWindow.startScreen();

            CenterOfScreen = new TerminalPosition(
                    GUIWindow.getTerminalSize().getColumns()/2-10,
                    GUIWindow.getTerminalSize().getRows()/2);

            WindowsGUI = new MultiWindowTextGUI(GUIWindow, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));

            WindowsGUI.getBackgroundPane().setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));

            Label logo = new Label(GameResources.Logo).setPosition(CenterOfScreen);

            WindowsGUI.getBackgroundPane().setComponent(logo);

            OpenMenuWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void OpenNewGameWindow() {
        NewGameWindow.setTitle("New game");

        ConstructNewGamePanel();

        WindowsGUI.addWindow(NewGameWindow);

        NewGameWindow.setPosition(CenterOfScreen);

        WindowsGUI.setActiveWindow(NewGameWindow).waitForWindowToClose(NewGameWindow);
    }

    private static void ConstructNewGamePanel(){
        Panel NewGamePanel = new Panel();

        TextBox nickname = new TextBox(new TerminalSize(10,1));

        NewGamePanel.addComponent(nickname);

        NewGamePanel.addComponent(new Button("Back to menu", () ->{
                WindowsGUI.getActiveWindow().close();
                OpenMenuWindow();
        }));

        NewGamePanel.addComponent(new Button("Start game", () -> {
            WindowsGUI.getActiveWindow().close();
            if(nickname.getText().length() > 0) {
                Debug.log("MAIN MENU: get input nickname: " + nickname.getText());
                Player.nickName = nickname.getText();
                GameResources.UpdatePlayerName();
                Debug.log("MAIN MENU: Set nickname " + Player.nickName);
            }
            try {
                GUIWindow.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        NewGameWindow.setComponent(NewGamePanel);
    }

    private static void OpenMenuWindow() {
        if(BasePanel == null)
            ConstructMenuPanel();

        MenuWindows.setComponent(BasePanel.withBorder(Borders.singleLine("Main menu")));

        WindowsGUI.addWindow(MenuWindows);

        MenuWindows.setPosition(CenterOfScreen);

        WindowsGUI.setActiveWindow(MenuWindows).waitForWindowToClose(MenuWindows);
    }

    private static void ConstructMenuPanel(){
        BasePanel = new Panel();

        BasePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        if(SavingSystem.SaveFileExists()){
            Debug.log("Save file was found");
            BasePanel.addComponent(new Button("Continue game", () -> {
                WindowsGUI.getActiveWindow().close();
                try {
                    Main.NewGame = !Main.NewGame;
                    GUIWindow.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        } else{
            Debug.log("Save files was not found");
        }

        BasePanel.addComponent(new Button("New Game", ()-> {
            WindowsGUI.getActiveWindow().close();
            OpenNewGameWindow();
        }));

        BasePanel.addComponent(new Button("Quit", () -> {
            try {
                Debug.log("=== Instance ended by quit from menu === ");
                Debug.SaveLogToFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }));
    }
}
