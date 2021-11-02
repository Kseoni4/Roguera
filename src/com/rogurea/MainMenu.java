package com.rogurea;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.VirtualScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;
import com.rogurea.base.Debug;
import com.rogurea.base.DisposeListener;
import com.rogurea.exceptions.NickNameAlreadyUsed;
import com.rogurea.gamelogic.SaveLoadSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.net.ConnectingWorker;
import com.rogurea.player.Player;
import com.rogurea.resources.GameResources;
import com.rogurea.net.JDBСQueries;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainMenu {

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    private static Panel BasePanel;

    private static Panel NewGamePanel;

    private static Screen GUIWindow;

    public static MultiWindowTextGUI WindowsGUI;

    private static final BasicWindow MenuWindows = new BasicWindow();

    private static final BasicWindow NewGameWindow = new BasicWindow();

    private static final BasicWindow STATUS_WINDOW = new BasicWindow();

    private static TerminalPosition CenterOfScreen;

    private static int Normal = 0;

    private static int CorruptedSaveFile = 1;

    private static String checkedResources = null;

    private static void startMainMenuRP(){
        Debug.toLog("[DISCORD_RP]Start main menu presence");
        DiscordRichPresence mainMenuRP = new DiscordRichPresence.Builder("Ready to start").setDetails("In Main Menu").setBigImage("icon","Roguera").build();
        DiscordRPC.discordUpdatePresence(mainMenuRP);
        Debug.toLog("[DISCORD_RP] RP set");
    }

    public static void start(int code){
        try {
            Debug.toLog("[MAIN_MENU]Loading main menu");

            startMainMenuRP();

            if(GameResources.TerminalFont == null)
                GameResources.loadFont();

            defaultTerminalFactory.setTerminalEmulatorTitle("Roguera " + GameResources.VERSION);

            defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(GameResources.TerminalFont));

            defaultTerminalFactory.setTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode);

            GUIWindow = defaultTerminalFactory.createScreen();

            GUIWindow.startScreen();

            Roguera.terminals.add(GUIWindow);

            CenterOfScreen = new TerminalPosition(
                    GUIWindow.getTerminalSize().getColumns()/2-10,
                    GUIWindow.getTerminalSize().getRows()/2);

            WindowsGUI = new MultiWindowTextGUI(GUIWindow, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));

            WindowsGUI.setEOFWhenNoWindows(true);

            Roguera.disposeListenerThread.execute(new DisposeListener(WindowsGUI));

            WindowsGUI.getBackgroundPane().setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));

            Label logo = new Label(GameResources.LOGO).setPosition(CenterOfScreen);

            WindowsGUI.getBackgroundPane().setComponent(logo);

            OpenMenuWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void CheckErrorCode(int code){
        switch (code){
            case 0: {Debug.toLog("[MAIN_MENU]: All good"); break;}
            case 1: {
                Label errormessage = new Label("Saving file has been corrupted").setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                BasePanel.addComponent(errormessage);
                break;
            }
            case 2:{
                Label errormessage = new Label("This nickname is already in use").setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                NewGamePanel.addComponent(errormessage);
                break;
            }
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
        NewGamePanel = new Panel();

        NewGamePanel.setLayoutManager(new LinearLayout());

        NewGamePanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        TextBox nickname = new TextBox(new TerminalSize(10,1));

        NewGamePanel.addComponent(nickname.withBorder(Borders.singleLine("Enter your nickname"))).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        NewGamePanel.addComponent(new Button("Back to menu", () ->{
            WindowsGUI.getActiveWindow().close();
            OpenMenuWindow();
        })).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        NewGamePanel.addComponent(new Button("Start game", () -> {
            Dungeon.player = new Player();
            if(nickname.getText().length() > 0) {
                Debug.toLog("[MAIN_MENU] get input nickname: " + nickname.getText());

                Dungeon.player.getPlayerData().setPlayerName(nickname.getText());

                Debug.toLog("[MAIN_MENU] Set nickname " + Dungeon.player.getPlayerData().getPlayerName());
            } else {
                Debug.toLog("[MAIN_MENU] set random nickname");
            }
            try {
                GUIWindow.close();

                WindowsGUI.getActiveWindow().close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BasePanel = null;

                Main.newGame();

                Main.startSequence();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        })).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        NewGameWindow.setComponent(NewGamePanel);
    }
    private static void OpenMenuWindow() {

        if (BasePanel == null)
            ConstructMenuPanel();

        Panel loadingPanel = new Panel();

        BasicWindow loadingWindow = new BasicWindow();

        loadingPanel.addComponent(new Label("Connecting to db..."));

        loadingWindow.setComponent(loadingPanel.withBorder(Borders.doubleLine()));

        WindowsGUI.addWindow(loadingWindow);

        loadingWindow.setPosition(CenterOfScreen);

        Future<Optional<Connection>> sqlSocket = Executors.newSingleThreadExecutor().submit(new ConnectingWorker());

        try {
            WindowsGUI.setActiveWindow(loadingWindow).updateScreen();
            sqlSocket.get().ifPresentOrElse(JDBСQueries::setConnection, () -> Debug.toLog("[JDBC] Error on connecting to the database"));
            loadingWindow.close();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        WindowsGUI.removeWindow(loadingWindow);

        MenuWindows.setComponent(BasePanel.withBorder(Borders.singleLine("Main menu")));

        Panel statusPanel = new Panel();

        statusPanel.withBorder(Borders.singleLine("Online status"));

        statusPanel.addComponent(new Label((JDBСQueries.checkConnect() ? "Connected" : "Not connected"))
                .setForegroundColor((JDBСQueries.checkConnect() ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.RED_BRIGHT)));

        STATUS_WINDOW.setComponent(statusPanel);

        WindowsGUI.addWindow(MenuWindows);

        WindowsGUI.addWindow(STATUS_WINDOW);

        MenuWindows.setPosition(CenterOfScreen);

        STATUS_WINDOW.setPosition(new TerminalPosition(0, 20));

        WindowsGUI.setActiveWindow(MenuWindows).waitForWindowToClose(MenuWindows);

        System.exit(1);
    }

    private static void ConstructMenuPanel(){
        BasePanel = new Panel();

        BasePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        if(SaveLoadSystem.SaveFileExists()){
            Debug.toLog("[MAIN_MENU]Save file was found");
            BasePanel.addComponent(new Button("Continue game from last save", () -> {
                try {
                    SaveLoadSystem.loadGame(SaveLoadSystem.GetSaveFileName());

                    Main.loadGame();

                    CheckErrorCode(Normal);

                    WindowsGUI.getActiveWindow().close();

                    GUIWindow.close();

                    Main.startSequence();
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    Debug.toLog("ERROR: Saving file has been obsoleted.");
                    e.printStackTrace();
                    CheckErrorCode(CorruptedSaveFile);
                }
            }));
        } else{
            Debug.toLog("[MAIN_MENU]Save files was not found");
        }

        BasePanel.addComponent(new Button("New Game", ()-> {
            WindowsGUI.getActiveWindow().close();
            OpenNewGameWindow();
        }));

        BasePanel.addComponent(new Button("Quit", () -> {
            /*try {
                Debug.log("=== Instance ended by quit from menu === ");
                Player.nickName = "MenuLog";
                Debug.SaveLogToFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            DiscordRPC.discordShutdown();
            WindowsGUI.getActiveWindow().close();
            try {
                GUIWindow.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }));
/*        Label checkConnect = new Label("Online");
        BasePanel.addComponent(checkConnect);*/
        //BasePanel.addComponent();
    }
}
