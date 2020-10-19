package com.rogurea.main.creatures;

import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;

import java.io.IOException;
import java.util.Objects;

public class MobController {

    static boolean ScanZone(Mob mob){
        for(int Zone = 1; Zone <= mob.ScanZone; Zone++) {
            try{
                if (mob.ScanForPlayer(Dungeon.CurrentRoom[mob.MobPosX + Zone][mob.MobPosY]))
                    return true;
                if (mob.ScanForPlayer(Dungeon.CurrentRoom[mob.MobPosX - Zone][mob.MobPosY]))
                    return true;
                if (mob.ScanForPlayer(Dungeon.CurrentRoom[mob.MobPosX][mob.MobPosY + Zone]))
                    return true;
                if (mob.ScanForPlayer(Dungeon.CurrentRoom[mob.MobPosX][mob.MobPosY - Zone]))
                    return true;
            }
            catch (ArrayIndexOutOfBoundsException e){
                return false;
            }

        }
        return false;
    }

    static boolean MobsScan() throws IOException {
        for(Mob mob : Dungeon.CurrentRoomCreatures.keySet()){
            if(ScanZone(mob))
                return true;
        }
        return false;
    }

    static void RemoveMob(Mob mob){
        Dungeon.CurrentRoomCreatures.remove(mob);
        Dungeon.CurrentRoom[mob.MobPosX][mob.MobPosY] = ' ';
        Objects.requireNonNull(Dungeon.Rooms
                .stream().filter(
                        room -> room.NumberOfRoom == Player.CurrentRoom
                )
                .findAny()
                .orElse(null))
                .RoomCreatures.remove(mob);
    }

}
