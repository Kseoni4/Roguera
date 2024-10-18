package kseoni.ch.roguera.graphics.sprites;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

public class AssetPool {

    private HashMap<String, Character> assets;

    private static AssetPool INSTANCE;

    private AssetPool(){
        assets = new HashMap<>();
    }

    public static AssetPool get(){
        if (INSTANCE == null) {
            INSTANCE = new AssetPool();
        }
        return INSTANCE;
    }

    @SneakyThrows
    public void loadAssets(String filename){
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String assetLine;

        while ((assetLine = bufferedReader.readLine()) != null){
            String[] asset = assetLine.split("-");
            System.out.println("Load asset "+ Arrays.toString(asset));
            assets.put(asset[0], asset[1].charAt(0));
        }
    }

    public char getAsset(String assetName){
        return assets.get(assetName);
    }

}
