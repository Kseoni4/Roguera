package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.map.Room;

import java.util.*;

public class MapValidator {

    private final Set<Room> rooms;

    public MapValidator(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public boolean validateRooms(){

        if (!allRoomHasAtLeastOneDoor()){
            System.out.println("Rooms are not full connected by doors");
            return false;
        }

        if(!isConnected()){
            System.out.println("validate rooms connection depth false");
            return false;
        }

        return true;
    }

    private boolean isConnected(){
        if (rooms.isEmpty()) return true;

        Set<Room> visited = new HashSet<>();
        Deque<Room> queue = new ArrayDeque<>();

        Room start = rooms.iterator().next();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Room current = queue.poll();

            for (Door door : current.getDoors().values()) {
                Door next = door.getNextDoor();
                if (next == null) return false;             // висячая дверь — считаем невалидной картой

                Room neighbor = next.getCurretRoom();
                if (neighbor == null) return false;          // дверь без комнаты — тоже невалидно

                if (visited.add(neighbor)) {                 // add возвращает true, если добавили
                    queue.add(neighbor);
                }
            }
        }

        return visited.size() == rooms.size();
    }

    private boolean allRoomHasAtLeastOneDoor(){
        return rooms.stream().allMatch(room -> !room.getDoors().isEmpty());
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