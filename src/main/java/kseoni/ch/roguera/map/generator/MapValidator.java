package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.map.Room;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class MapValidator {

    private final Set<Room> rooms;

    public MapValidator(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public boolean validateRooms(){

        if (!anyRoomHasDoor()){
            return false;
        }

        Room room = rooms.iterator().next();

        if(!validateRoomsConnectionDepth(room, room, 1)){
            return false;
        }

        return true;
    }

    private boolean anyRoomHasDoor(){
        return rooms.stream().noneMatch(room -> room.getDoors().isEmpty());
    }

    private boolean validateRoomsConnectionDepth(Room roomFrom, Room roomTo, int depth){

        if(roomTo.getDoors().size() < 2 && roomTo.getDoors().containsKey(roomFrom.getRoomId())){
            if(depth < rooms.size()){
                return false;
            }
        }

        if(roomTo.getDoors().size() < 2 && roomTo.getDoors().containsKey(roomFrom.getRoomId())){
            if(depth >= rooms.size()) {
                return true;
            }
        }

        for(Room r : rooms){
            if((r != roomTo && r != roomFrom) && roomTo.getDoors().containsKey(r.getRoomId())){

                if(Objects.isNull(roomTo.getDoors().get(r.getRoomId()).getNextDoor())){
                    return false;
                }

                if(depth >= rooms.size() * 2){
                    return false;
                }

                Room nextRoom = roomTo.getDoors().get(r.getRoomId()).getNextDoor().getCurretRoom();
                return validateRoomsConnectionDepth(roomTo, nextRoom, depth+1);
            }
        }

        return true;
    }
}