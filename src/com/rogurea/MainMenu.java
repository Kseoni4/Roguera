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
import com.rogurea.net.RogueraSpring;
import com.rogurea.resources.Colors;
import com.rogurea.player.Player;
import com.rogurea.resources.GameResources;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;


import java.io.IOException;
import java.net.URISyntaxException;

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

    private static final int NICK_NAME_IS_BANNED_FOR_USE = 3;

    private static final int EXIT_CODE = 3;

    private static final int NEW_GAME_CODE = 1;

    private static final int LOAD_GAME_CODE = 2;

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
            case 3:{
                Label errormessage = new Label("This nickname is banned for use!").setForegroundColor(TextColor.ANSI.RED_BRIGHT);
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
                try {
                    if(Roguera.isOnline() && !RogueraSpring.checkNickName(nickname.getText())){
                        CheckErrorCode(NICK_NAME_IS_BANNED_FOR_USE);
                        return;
                    }
                } catch (URISyntaxException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                Dungeon.player.getPlayerData().setPlayerName(nickname.getText());

                Debug.toLog("[MAIN_MENU] Set nickname " + Dungeon.player.getPlayerData().getPlayerName());
            } else {
                Debug.toLog("[MAIN_MENU] set random nickname");
            }
            Roguera.codeOfMenu = 1;
            try {
                GUIWindow.close();

                windowsGUI.getActiveWindow().close();

                basePanel = null;

            } catch (IOException e) {
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

        windowsGUI.removeWindow(loadingWindow);

        MENU_WINDOWS.setComponent(basePanel.withBorder(Borders.singleLine("Main menu")));

        windowsGUI.addWindow(MENU_WINDOWS);

        MENU_WINDOWS.setPosition(centerOfScreen);

        STATUS_WINDOW.setPosition(new TerminalPosition(0, 20));

        windowsGUI.setActiveWindow(MENU_WINDOWS).waitForWindowToClose(MENU_WINDOWS);

        if(Roguera.codeOfMenu == 0){
            System.exit(0);
        }
    }

    private static void ConstructMenuPanel(){
        basePanel = new Panel();

        basePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        if(SaveLoadSystem.saveFileExists()){
            Debug.toLog("[MAIN_MENU]Save file was found");
            basePanel.addComponent(new Button("Continue game from last save", () -> {
                try {
                    SaveLoadSystem.loadGame(SaveLoadSystem.getSaveFileName());

                    CheckErrorCode(NORMAL);

                    windowsGUI.getActiveWindow().close();

                    GUIWindow.close();

                    windowsGUI = null;

                    GUIWindow = null;

                    Roguera.codeOfMenu = LOAD_GAME_CODE;
                } catch (IOException | ClassNotFoundException e) {
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

            DiscordRPC.discordShutdown();
            windowsGUI.getActiveWindow().close();
            try {
                GUIWindow.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Roguera.codeOfMenu = EXIT_CODE;
        }));
    }
}
