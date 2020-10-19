package com.rogurea.prototype_old;

import java.util.ArrayList;
import java.util.List;

public class PrintDebugInfo {

    static List<String> GetCreaturesList(Room r){
        List<String> MobNames = new ArrayList<>();
        if(r.RoomCreatures != null)
            for (Creature mob : r.RoomCreatures){
                MobNames.add(mob.Name);
            }
        return MobNames;

    }

    public static void Room(Room r) {
        System.out.printf(
                "Room number %d \n" +
                        "\tLast room? %b\n" +
                        "\tCreatures: %s \n" +
                        "\tNext room: %s\n",
                        r.NumberOfRoom,
                        r.IsEndRoom,
                        GetCreaturesList(r),
                        (r.nextRoom != null ? r.nextRoom.NumberOfRoom : "end")
        );
    }
}
