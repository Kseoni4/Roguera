package com.rogurea.gamelogic;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Floor;
import com.rogurea.player.Player;
import com.rogurea.player.PlayerData;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.view.ViewObjects;

import java.io.*;
import java.util.*;

public class SaveLoadSystem {

    private static File currentSaveFile;

    private static PlayerData playerDataFile;

    private static ObjectOutputStream saveFileStream;

    private static File saveFile;

    private static File saveFileTemp;

    private static final String DIRECTORY = "./saves/";

    private static final String EXTENSION = ".sav";

    public static void saveGame() throws IOException {

        createSaveDirectory();

        playerDataFile = Dungeon.player.getPlayerData();

        putDataInSaveFile();

        String playerName = ViewObjects.getTrimString(playerDataFile.getPlayerName());

        String saveFileName = DIRECTORY + playerName + EXTENSION;

        //JDBСQueries.updUserHash(String.valueOf(Dungeon.player.getPlayerData().getScoreHash().hashCode()));
                            //"./saves/player228.sav"
        saveFile = new File(saveFileName);

        saveFileStream = new ObjectOutputStream(new FileOutputStream(saveFile));

        try {

            trySaveToTemp();

        }  catch (IOException e){
            Debug.toLog(Colors.RED_BRIGHT+"[ERROR][SAVE] Error with save file");
            e.printStackTrace();
            return;
        }

        saveFileStream.writeObject(playerDataFile);

        saveFileStream.flush();

        saveFileStream.close();

        Debug.toLog("[SAVE] room " + playerDataFile.getCurrentRoom().roomNumber + " has saved");

        Debug.toLog("[SAVE] success! " + saveFileName + " has created");

        Debug.toLog("[SAVE] remove temp file");

        saveFileTemp.delete();
    }

    public static void loadGame(String saveFileName) throws IOException, ClassNotFoundException {

        FileInputStream saveFileInputStream = new FileInputStream(saveFileName);

        ObjectInputStream saveObjectInputStream = new ObjectInputStream(saveFileInputStream);

        Debug.toLog("[LOAD]Loading game from file: " + saveFileName);

        PlayerData savedFile = (PlayerData) saveObjectInputStream.readObject();


        try {
            getDataFromSavedFile(savedFile);
        } catch (IndexOutOfBoundsException e){
            Debug.toLog(Colors.RED_BRIGHT+"[ERROR][LOAD] load game was failed. See stacktrace:");
            e.printStackTrace();
        }

        saveObjectInputStream.close();

        saveFileInputStream.close();

        Debug.toLog("[LOAD]Save file is loaded");

        /*ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(saveFileName)
        );

        Debug.toLog("[LOAD]Loading game from file: " + saveFileName);

        PlayerData loadedFile;

        Dungeon.player = new Player();

        loadedFile = (PlayerData) objectInputStream.readObject();

        Dungeon.player.setCurrentRoom((byte) loadedFile.getCurrentRoom().roomNumber);

        Dungeon.player.setPlayerData(loadedFile);

        Dungeon.player.playerPosition = loadedFile.getPlayerPositionData();

        Debug.toLog("[LOAD] Player ID from loadingFile: "+loadedFile.getPlayerID());

        Debug.toLog("[LOAD] Player ID from save: "+Dungeon.player.getPlayerData().getPlayerID());

        int scoreHash = JDBСQueries.getUserScoreHash();

        Debug.toLog("[LOAD]Player name: " + Dungeon.player.getName());

        Dungeon.savedRoom = loadedFile.getCurrentRoom();

        Dungeon.player.Inventory = loadedFile.getPlayerInventory();

        Dungeon.player.Equipment = loadedFile.getPlayerEquipment();

        //GetRandom.SetRNGSeed(loadedFile.getRandomSeed());

        Debug.toLog("[LOAD]Save file is loaded");*/
    }

    private static void createSaveDirectory(){
                                    //"./saves/"
        File saveDirectory = new File(DIRECTORY);

        if(!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }
    }

    private static void putDataInSaveFile(){
        playerDataFile.setPlayerPositionData(Dungeon.player.playerPosition);
        playerDataFile.setPlayerInventory(Dungeon.player.Inventory);
        playerDataFile.setPlayerQuickEquipment(Dungeon.player.quickEquipment);
        playerDataFile.setPlayerEquipment(Dungeon.player.Equipment);
        playerDataFile.setSaveFileVersion(GameResources.VERSION);
        playerDataFile.setCurrentFloor(Dungeon.getCurrentFloor().get());
        playerDataFile.setCurrentRoom(Dungeon.getCurrentRoom());
    }

    private static void getDataFromSavedFile(PlayerData savedFile) throws IndexOutOfBoundsException {
        Dungeon.player = new Player(savedFile);

        int floorNumber = savedFile.getCurrentFloor().getFloorNumber();

        Floor.setCounterFromLoad(floorNumber);

        Dungeon.currentFloorNumber = floorNumber;

        Dungeon.floors.add(savedFile.getCurrentFloor());

        Dungeon.rooms = savedFile.getCurrentFloor().getRooms();

        Dungeon.savedRoom = savedFile.getCurrentRoom();

        Dungeon.player.setCurrentRoom((byte) savedFile.getCurrentRoom().roomNumber);

        //int scoreHash = JDBСQueries.getUserScoreHash();

    }

    private static void trySaveToTemp() throws IOException {

        saveFileTemp = new File(DIRECTORY + "_temp_" + EXTENSION);

        ObjectOutputStream saveFileTempStream = new ObjectOutputStream(new FileOutputStream(saveFileTemp));

        saveFileTempStream.writeObject(playerDataFile);

        saveFileTempStream.flush();

        saveFileTempStream.close();
     }

    public static String getSaveFileName() {
        File directory = new File("./saves/");

        ArrayList<File> files = new ArrayList<>(List.of(Objects.requireNonNull(directory.listFiles(((dir, name) -> name.endsWith(".sav"))))));

        files.sort(Comparator.comparingLong(File::lastModified));

        try {
            Debug.toLog("[LOAD] Load file with name: "+ files.get(files.size()-1).getName());
            return "./saves/"+files.get(files.size()-1).getName();
        } catch (NullPointerException e) {
            Debug.toLog("[ERROR] Save file exist but not found");
            Debug.toLog(e.getMessage());
            return "";
        }
    }

    public static boolean saveFileExists(){
        File directory = new File("./saves/");

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".sav"));
        if(files != null) {
            return files.length > 0;
        } else {
            return false;
        }
    }

    public static boolean deleteSaveFile(){
        return currentSaveFile.delete();
    }
}
