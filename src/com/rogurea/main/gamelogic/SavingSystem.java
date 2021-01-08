package com.rogurea.main.gamelogic;

import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.player.Player;
import com.rogurea.main.player.PlayerContainer;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

import java.io.*;
import java.util.Arrays;

public class SavingSystem {

    public static void saveGame() throws IOException {

        Debug.log("Saving game...");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(Player.nickName+".sav")
        );

        PlayerContainer savePlayerFile = GetPlayerContainer();

        savePlayerFile.SaveFileVer = GameResources.version;

        objectOutputStream.writeObject(savePlayerFile);

        Debug.log("Save success: " + Player.nickName+".sav has created");
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
                Player.XPForNextLevel,
                Player.Inventory,
                Player.Equip,
                Player.playerStatistics
        );
    }

    public static void loadGame(String SaveFileName) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(SaveFileName)
        );

        Debug.log("Loading game from file: " + SaveFileName);

        PlayerContainer loadedFile = null;
        try {
            loadedFile = (PlayerContainer) objectInputStream.readObject();
        }catch (NotSerializableException e){
            Debug.log("ERROR: Saving file has been obsoleted. \n\tFile ver: "
                    + ((PlayerContainer) objectInputStream.readObject()).SaveFileVer);
            return;
        }

        Player.LoadPlayerDataFromFile(loadedFile);

        Debug.log("Player name: " + Player.nickName);

        GameResources.UpdatePlayerName();

        GetRandom.SetRNGSeed(loadedFile.RandomSeed);

        Debug.log("Save file is loaded");
    }

    public static String GetSaveFileName(){
        File directory = new File("./");

        File[] files = directory.listFiles(((dir, name) -> name.endsWith(".sav")));

        return files[0].getName();
    }

    public static boolean SaveFileExists(){
        File directory = new File("./");

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".sav"));

        return files.length > 0;
    }
}
