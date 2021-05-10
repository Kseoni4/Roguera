package com.rogurea.main.gamelogic;

import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.Player;
import com.rogurea.main.player.PlayerContainer;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

import java.io.*;

public class SavingSystem {

    public static void saveGame() throws IOException {

        Debug.log("SAVE: Saving game...");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(Player.nickName+".sav")
        );

        PlayerContainer savePlayerFile = GetPlayerContainer();

        savePlayerFile.SaveFileVer = GameResources.version;

        savePlayerFile.CurrentDungeonLenght = Dungeon.CurrentDungeonLenght;

        Debug.log("SAVE: Current dungeon length " + savePlayerFile.CurrentDungeonLenght);

        savePlayerFile.CurrentRoomObject = Dungeon.GetCurrentRoom();

        Debug.log("SAVE: room " + savePlayerFile.CurrentRoomObject.NumberOfRoom + " has saved");

        objectOutputStream.writeObject(savePlayerFile);

        Debug.log("SAVE: success! " + Player.nickName+".sav has created");
    }

    private static PlayerContainer GetPlayerContainer(){
        return new PlayerContainer(
                Player.nickName,
                Player.GetPlayerPosition(),
                Player.HP,
                Player.MP,
                Player.Level,
                Player.CurrentRoom,
                Player.Money,
                Player.attempt,
                Player.RandomSeed,
                Player.XP,
                Player.ATK,
                Player.DEF,
                Player.DEX,
                Player.ReqXPForNextLevel,
                Player.Inventory,
                Player.Equip,
                Player.playerStatistics
        );
    }

    public static void loadGame(String SaveFileName) throws IOException, ClassNotFoundException, StreamCorruptedException, InvalidClassException {
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(SaveFileName)
        );

        Debug.log("Loading game from file: " + SaveFileName);

        PlayerContainer loadedFile;

        loadedFile = (PlayerContainer) objectInputStream.readObject();

        Player.LoadPlayerDataFromFile(loadedFile);

        Debug.log("layer name: " + Player.nickName);

        GameResources.UpdatePlayerName();

        Dungeon.CurrentDungeonLenght = loadedFile.CurrentDungeonLenght;

        Dungeon.SavedRoom = loadedFile.CurrentRoomObject;

        Dungeon.SavedRoom.roomSize = BaseGenerate.RoomSize.BIG;

        GetRandom.SetRNGSeed(loadedFile.RandomSeed);

        Debug.log("Save file is loaded");
    }

    public static String GetSaveFileName(){
        File directory = new File("./");

        File[] files = directory.listFiles(((dir, name) -> name.endsWith(".sav")));

        return files[files.length-1].getName();
    }

    public static boolean SaveFileExists(){
        File directory = new File("./");

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".sav"));

        return files.length > 0;
    }
}
