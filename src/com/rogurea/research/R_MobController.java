package com.rogurea.research;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.Objects;

public class R_MobController {

    static boolean ScanZone(R_Mob mob){
        for(int Zone = 1; Zone <= mob.ScanZone; Zone++) {
            try{
                if (mob.ScanForPlayer(R_Dungeon.CurrentRoom[mob.MobPosX + Zone][mob.MobPosY]))
                    return true;
                if (mob.ScanForPlayer(R_Dungeon.CurrentRoom[mob.MobPosX - Zone][mob.MobPosY]))
                    return true;
                if (mob.ScanForPlayer(R_Dungeon.CurrentRoom[mob.MobPosX][mob.MobPosY + Zone]))
                    return true;
                if (mob.ScanForPlayer(R_Dungeon.CurrentRoom[mob.MobPosX][mob.MobPosY - Zone]))
                    return true;
            }
            catch (ArrayIndexOutOfBoundsException e){
                return false;
            }

        }
        return false;
    }

    static boolean MobsScan() throws IOException {
        for(R_Mob mob : R_Dungeon.CurrentRoomCreatures.keySet()){
            if(ScanZone(mob))
                return true;
        }
        return false;
    }

    static void RemoveMob(R_Mob mob){
        R_Dungeon.CurrentRoomCreatures.remove(mob);
        R_Dungeon.CurrentRoom[mob.MobPosX][mob.MobPosY] = ' ';
        Objects.requireNonNull(R_Dungeon.Rooms
                .stream().filter(
                        room -> room.NumberOfRoom == R_Player.CurrentRoom
                )
                .findAny()
                .orElse(null))
                .RoomCreatures.remove(mob);
    }

}
