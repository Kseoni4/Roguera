package com.rogurea.gamelogic;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.net.JDBСQueries;
import com.rogurea.player.Player;
import com.rogurea.player.PlayerData;
import com.rogurea.resources.GameResources;
import com.rogurea.view.ViewObjects;

import java.io.*;

public class SaveLoadSystem {
    private static File CurrentSaveFile;

    public static void saveGame() throws IOException {

        Player player = Dungeon.player;

        String playerName = ViewObjects.getTrimString(player.getPlayerData().getPlayerName());

        CurrentSaveFile = new File(playerName+".sav");

        Debug.toLog("[SAVE]: Saving game...");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(CurrentSaveFile)
        );

        PlayerData savePlayerFile = Dungeon.player.getPlayerData();

        JDBСQueries.updUserHash(String.valueOf(Dungeon.player.getPlayerData().getScoreHash().hashCode()));

        Debug.toLog("[SAVE] Score hash: "+Dungeon.player.getPlayerData().getScoreHash().hashCode());

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

        //Debug.toLog("[SAVE]: room " + savePlayerFile.getCurrentRoom().roomNumber + " has saved");

        Debug.toLog("[SAVE] Saved score hash: "+savePlayerFile.getScoreHash());

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

        Debug.toLog("[LOAD] Score hash from loadingFile: "+loadedFile.getScoreHash().hashCode());

        int scoreHash = JDBСQueries.getUserScoreHash();

        /*if(Dungeon.player.getPlayerData().checkSavedScoreHash(scoreHash)){
            Debug.toLog("[LOAD] Score hash is OK");
        } else {
            Debug.toLog("[LOAD] Score hash is BAD");
            throw new IOException("Save file is corrupt");
        }*/

        Debug.toLog("[LOAD]Player name: " + Dungeon.player.getName());

        Dungeon.savedRoom = loadedFile.getCurrentRoom();

        Dungeon.player.Inventory = loadedFile.getPlayerInventory();

        Dungeon.player.Equipment = loadedFile.getPlayerEquipment();

        //GetRandom.SetRNGSeed(loadedFile.getRandomSeed());

        Debug.toLog("[LOAD]Save file is loaded");
    }

    public static String GetSaveFileName() {
        File directory = new File("./");

        File[] files = directory.listFiles(((dir, name) -> name.endsWith(".sav")));

        try {
            return files[files.length - 1].getName();
        } catch (NullPointerException e) {
            Debug.toLog("ERROR: Save file exist but not found");
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
