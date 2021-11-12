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
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;
import com.rogurea.base.Debug;
import com.rogurea.gamelogic.SaveLoadSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.Colors;
import com.rogurea.player.Player;
import com.rogurea.resources.GameResources;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;


import java.io.IOException;

public class MainMenu {

    private static final DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

    private static Panel basePanel;

    private static Panel newGamePanel;

    private static Screen GUIWindow;

    public static MultiWindowTextGUI windowsGUI;

    private static final BasicWindow MENU_WINDOWS = new BasicWindow();

    private static final BasicWindow NEW_GAME_WINDOW = new BasicWindow();

    private static final BasicWindow STATUS_WINDOW = new BasicWindow();

    private static TerminalPosition centerOfScreen;

    private static final int NORMAL = 0;

    private static final int CORRUPTED_SAVE_FILE = 1;

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

            centerOfScreen = new TerminalPosition(
                    GUIWindow.getTerminalSize().getColumns()/2-10,
                    GUIWindow.getTerminalSize().getRows()/2);

            windowsGUI = new MultiWindowTextGUI(GUIWindow, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));

            windowsGUI.setEOFWhenNoWindows(true);

            windowsGUI.getBackgroundPane().setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));

            Label logo = new Label(GameResources.LOGO).setPosition(centerOfScreen);

            windowsGUI.getBackgroundPane().setComponent(logo);

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
                basePanel.addComponent(errormessage);
                break;
            }
            case 2:{
                Label errormessage = new Label("This nickname is already in use").setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                newGamePanel.addComponent(errormessage);
                break;
            }
        }
    }

    private static void OpenNewGameWindow() {
        NEW_GAME_WINDOW.setTitle("New game");

        ConstructNewGamePanel();

        windowsGUI.addWindow(NEW_GAME_WINDOW);

        NEW_GAME_WINDOW.setPosition(centerOfScreen);

        windowsGUI.setActiveWindow(NEW_GAME_WINDOW).waitForWindowToClose(NEW_GAME_WINDOW);
    }

    private static void ConstructNewGamePanel(){
        newGamePanel = new Panel();

        newGamePanel.setLayoutManager(new LinearLayout());

        newGamePanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        TextBox nickname = new TextBox(new TerminalSize(10,1));

        newGamePanel.addComponent(nickname.withBorder(Borders.singleLine("Enter your nickname"))).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        newGamePanel.addComponent(new Button("Back to menu", () ->{
            windowsGUI.getActiveWindow().close();
            OpenMenuWindow();
        })).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        newGamePanel.addComponent(new Button("Start game", () -> {
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

                windowsGUI.getActiveWindow().close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                basePanel = null;

                Main.newGame();

                Main.startSequence();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        })).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        NEW_GAME_WINDOW.setComponent(newGamePanel);
    }
    private static void OpenMenuWindow() {

        if (basePanel == null)
            ConstructMenuPanel();

        Panel loadingPanel = new Panel();

        BasicWindow loadingWindow = new BasicWindow();

        loadingPanel.addComponent(new Label("Connecting to db..."));

        loadingWindow.setComponent(loadingPanel.withBorder(Borders.doubleLine()));

        windowsGUI.addWindow(loadingWindow);

        loadingWindow.setPosition(centerOfScreen);

        //Future<Optional<Connection>> sqlSocket = Executors.newSingleThreadExecutor().submit(new ConnectingWorker());

        /*try {
            windowsGUI.setActiveWindow(loadingWindow).updateScreen();
            sqlSocket.get().ifPresentOrElse(JDBСQueries::setConnection, () -> Debug.toLog("[JDBC] Error on connecting to the database"));
            loadingWindow.close();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }*/

        windowsGUI.removeWindow(loadingWindow);

        MENU_WINDOWS.setComponent(basePanel.withBorder(Borders.singleLine("Main menu")));

        /*Panel statusPanel = new Panel();

        statusPanel.withBorder(Borders.singleLine("Online status"));

        statusPanel.addComponent(new Label((JDBСQueries.checkConnect() ? "Connected" : "Not connected"))
                .setForegroundColor((JDBСQueries.checkConnect() ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.RED_BRIGHT)));

        STATUS_WINDOW.setComponent(statusPanel);*/

        windowsGUI.addWindow(MENU_WINDOWS);

        //windowsGUI.addWindow(STATUS_WINDOW);

        MENU_WINDOWS.setPosition(centerOfScreen);

        STATUS_WINDOW.setPosition(new TerminalPosition(0, 20));

        windowsGUI.setActiveWindow(MENU_WINDOWS).waitForWindowToClose(MENU_WINDOWS);

        System.exit(1);
    }

    private static void ConstructMenuPanel(){
        basePanel = new Panel();

        basePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        if(SaveLoadSystem.saveFileExists()){
            Debug.toLog("[MAIN_MENU]Save file was found");
            basePanel.addComponent(new Button("Continue game from last save", () -> {
                try {
                    SaveLoadSystem.loadGame(SaveLoadSystem.getSaveFileName());

                    Main.disableNewGame();

                    CheckErrorCode(NORMAL);

                    windowsGUI.getActiveWindow().close();

                    GUIWindow.close();

                    Main.startSequence();
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    Debug.toLog(Colors.RED_BRIGHT+"[ERROR][MAIN_MENU]: Saving file has been obsoleted.");
                    e.printStackTrace();
                    CheckErrorCode(CORRUPTED_SAVE_FILE);
                }
            }));
        } else{
            Debug.toLog("[MAIN_MENU]Save files was not found");
        }

        basePanel.addComponent(new Button("New Game", ()-> {
            windowsGUI.getActiveWindow().close();
            OpenNewGameWindow();
        }));

        basePanel.addComponent(new Button("Quit", () -> {
            /*try {
                Debug.log("=== Instance ended by quit from menu === ");
                Player.nickName = "MenuLog";
                Debug.SaveLogToFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            DiscordRPC.discordShutdown();
            windowsGUI.getActiveWindow().close();
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
