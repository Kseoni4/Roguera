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
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(playerDTO.getNickName()+".auth"));
            outputStream.writeObject(playerDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
