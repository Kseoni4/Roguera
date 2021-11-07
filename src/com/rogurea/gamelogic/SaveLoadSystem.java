package com.rogurea.gamelogic;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.net.JDBСQueries;
import com.rogurea.player.Player;
import com.rogurea.player.PlayerData;
import com.rogurea.resources.GameResources;
import com.rogurea.view.ViewObjects;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SaveLoadSystem {
    private static File CurrentSaveFile;

    public static void saveGame() throws IOException {

        Player player = Dungeon.player;

        String playerName = ViewObjects.getTrimString(player.getPlayerData().getPlayerName());

        File saveDirectory = new File("saves/");

        saveDirectory.mkdir();

        CurrentSaveFile = new File(saveDirectory.getName()+"/"+playerName+".sav");

        Debug.toLog("[SAVE]: Saving game...");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(CurrentSaveFile)
        );

        PlayerData savePlayerFile = Dungeon.player.getPlayerData();

        JDBСQueries.updUserHash(String.valueOf(Dungeon.player.getPlayerData().getScoreHash().hashCode()));

        savePlayerFile.setSaveFileVersion(GameResources.VERSION);

        //savePlayerFile.CurrentDungeonLenght = Dungeon.CurrentDungeonLenght;

        //Debug.log("SAVE: Current dungeon length " + savePlayerFile.CurrentDungeonLenght);

        savePlayerFile.setScoreHash(savePlayerFile.getScoreHash());

        savePlayerFile.setCurrentRoom(Dungeon.getCurrentRoom());

        savePlayerFile.setCurrentFloor(Dungeon.getCurrentFloor());

        savePlayerFile.setPlayerPositionData(Dungeon.player.playerPosition);

        savePlayerFile.setHP(Dungeon.player.getHP());

        savePlayerFile.setPlayerInventory(Dungeon.player.Inventory);

        savePlayerFile.setPlayerEquipment(Dungeon.player.Equipment);

        Debug.toLog("[SAVE]: room " + savePlayerFile.getCurrentRoom().roomNumber + " has saved");

         objectOutputStream.writeObject(savePlayerFile);

        Debug.toLog("[SAVE]: success! " + player.getPlayerData().getPlayerName()+".sav has created");
    }

    public static void loadGame(String SaveFileName) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(SaveFileName)
        );

        Debug.toLog("[LOAD]Loading game from file: " + SaveFileName);

        PlayerData loadedFile;

        loadedFile = (PlayerData) objectInputStream.readObject();

        Dungeon.player = new Player(loadedFile);

        Dungeon.player.setCurrentRoom((byte) loadedFile.getCurrentRoom().roomNumber);

        Dungeon.player.setPlayerData(loadedFile);

        Debug.toLog("[LOAD] Player ID from loadingFile: "+loadedFile.getPlayerID());

        Debug.toLog("[LOAD] Player ID from save: "+Dungeon.player.getPlayerData().getPlayerID());

        int scoreHash = JDBСQueries.getUserScoreHash();

        Debug.toLog("[LOAD]Player name: " + Dungeon.player.getName());

        Dungeon.savedRoom = loadedFile.getCurrentRoom();

        Dungeon.player.Inventory = loadedFile.getPlayerInventory();

        Dungeon.player.Equipment = loadedFile.getPlayerEquipment();

        //GetRandom.SetRNGSeed(loadedFile.getRandomSeed());

        Debug.toLog("[LOAD]Save file is loaded");
    }

    public static String GetSaveFileName() {
        File directory = new File("./");

        ArrayList<File> files = new ArrayList<File>(List.of(Objects.requireNonNull(directory.listFiles(((dir, name) -> name.endsWith(".sav"))))));

        files.sort(Comparator.comparingLong(File::lastModified));

        try {
            Debug.toLog("[LOAD] Load file with name: "+ files.get(files.size()-1).getName());
            return files.get(files.size()-1).getName();
        } catch (NullPointerException e) {
            Debug.toLog("[ERROR] Save file exist but not found");
            Debug.toLog(e.getMessage());
            return "";
        }
    }

    public static boolean SaveFileExists(){
        File directory = new File("./");

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".sav"));

        return files.length > 0;
    }
    public static boolean DeleteSaveFile(){
        return CurrentSaveFile.delete();
    }
}
