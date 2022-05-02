package com.rogurea.utils;

import com.rogurea.base.Debug;
import com.rogurea.net.PlayerDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SavePlayerDTO {

    public static void save(PlayerDTO playerDTO){
        try {
            Debug.toLog("Saving player dto = "+playerDTO.toString());
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File(playerDTO.getNickName()+".auth")));
            outputStream.writeObject(playerDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
