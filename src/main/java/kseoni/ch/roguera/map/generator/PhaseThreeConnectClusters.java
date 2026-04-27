package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Dungeon;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.Convert;
import lombok.SneakyThrows;

import java.util.*;

class PhaseThreeConnectClusters {

    private Map<Integer, Room> rooms;

    private Map<Double, Set<Room>> distancesRooms = new HashMap<>();

    private final MapGenerate mapGen;

    public PhaseThreeConnectClusters(MapGenerate mapGen, Map<Integer, Room> rooms){
        this.rooms = rooms;
        this.mapGen = mapGen;
    }

    @SneakyThrows
    public void connectRooms(){
        // Сначала считаем дистанции между комнатами
        calculateDistances();

        if(mapGen.isDebugShowDungeonGenerate()) {
            for (Map.Entry entry : distancesRooms.entrySet()) {
                mapGen.generateDebugLog("Rooms: " + entry.getValue());
                mapGen.generateDebugLog("Distance between: " + entry.getKey());
            }
        }

        char doorV = AssetPool.get().getAsset("door_v");
        char doorH = AssetPool.get().getAsset("door_h");

        while (!distancesRooms.isEmpty()){
            // Выбираем две ближайших комнаты в словаре
            double minDistance = distancesRooms.keySet().stream().min(Comparator.comparing(Double::doubleValue)).get();

            // Убираем из словаря и достаём итератор
            Set<Room> roomSet = distancesRooms.remove(minDistance);

            Iterator<Room> it = roomSet.iterator();
            Room first = it.next();
            Room second = it.next();

            // Нашли глобальный центр комнат
            Position globalCenterFirstRoom = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter());
            Position globalCenterSecondRoom = Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter());

            // Если вторая комната находится между левым и правым краем первой комнаты по X.
            if (globalCenterSecondRoom.isInBetweenX(
                    first.getRoomLeftTopPosition(),
                    first.getRoomLeftTopPosition().getRelativePosition(first.getWidth(),0))
            ){

                Door doorInFirstRoom = new Door(new TextSprite(doorH), second.getRoomId(), first);
                Door doorInSecondRoom = new Door(new TextSprite(doorH), first.getRoomId(), second);

                Position doorPlaceFirst;
                Position doorPlaceSecond;

                if(globalCenterSecondRoom.getY() >= globalCenterFirstRoom.getY()) {
                    doorPlaceFirst = findToPlaceDoorY(first, Position.FRONT);
                    doorPlaceSecond = findToPlaceDoorY(second, Position.BACK);
                } else {
                    doorPlaceFirst = findToPlaceDoorY(first, Position.BACK);
                    doorPlaceSecond = findToPlaceDoorY(second, Position.FRONT);
                }
                mapGen.generateDebugLog("Find door first room place "+doorPlaceFirst);
                mapGen.generateDebugLog("Find door second room place "+doorPlaceSecond);


                if(first.getCell(doorPlaceFirst).isEmpty()) {
                    first.getCell(doorPlaceFirst).placeObject(doorInFirstRoom);
                    doorInSecondRoom.setNextDoor(doorInFirstRoom);
                    first.addDoor(doorInFirstRoom);
                }

                if(second.getCell(doorPlaceSecond).isEmpty()) {
                    second.getCell(doorPlaceSecond).placeObject(doorInSecondRoom);
                    doorInFirstRoom.setNextDoor(doorInSecondRoom);
                    second.addDoor(doorInSecondRoom);
                }
                continue;
            }

            // Если вторая комната находится между верхним и нижним краем первой комнаты по Y.
            if (globalCenterSecondRoom.isInBetweenY(
                    first.getRoomLeftTopPosition(),
                    first.getRoomLeftTopPosition().getRelativePosition(0,first.getHeight()))
            ){
                Door doorInFirstRoom = new Door(new TextSprite(doorV), second.getRoomId(), first);
                Door doorInSecondRoom = new Door(new TextSprite(doorV), first.getRoomId(), second);



                Position doorPlaceFirst;
                Position doorPlaceSecond;

                if(globalCenterSecondRoom.getX() >= globalCenterFirstRoom.getX()) {
                    doorPlaceFirst = findToPlaceDoorX(first, Position.RIGHT);
                    doorPlaceSecond = findToPlaceDoorX(second, Position.LEFT);
                } else {
                    doorPlaceFirst = findToPlaceDoorX(first, Position.LEFT);
                    doorPlaceSecond = findToPlaceDoorX(second, Position.RIGHT);
                }
                mapGen.generateDebugLog("Find door first room place "+doorPlaceFirst);
                mapGen.generateDebugLog("Find door second room place "+doorPlaceSecond);

                if(first.getCell(doorPlaceFirst).isEmpty()) {
                    first.getCell(doorPlaceFirst).placeObject(doorInFirstRoom);
                    doorInSecondRoom.setNextDoor(doorInFirstRoom);
                    first.addDoor(doorInFirstRoom);
                }

                if(second.getCell(doorPlaceSecond).isEmpty()) {
                    second.getCell(doorPlaceSecond).placeObject(doorInSecondRoom);
                    doorInFirstRoom.setNextDoor(doorInSecondRoom);
                    second.addDoor(doorInSecondRoom);
                }
            }
        }
    }

    private Position findToPlaceDoorX(Room room, Position direction){
        Position prevPosition = room.getRoomCenter();
        Position thisPosition;
        for(int x = room.getRoomCenter().getX(); (x >= 0 || x < room.getWidth());x+=direction.getX()){
            thisPosition = new Position(x, room.getRoomCenter().getY());

            if (room.getCell(thisPosition) == null) {
                return prevPosition;
            }

            prevPosition = thisPosition;
        }
        return room.getRoomCenter().getRelativePosition(room.getWidth(), 0);
    }

    private Position findToPlaceDoorY(Room room, Position direction){
        Position prevPosition = room.getRoomCenter();
        Position thisPosition;
        for(int y = room.getRoomCenter().getY(); (y >= 0 || y < room.getHeight());y+=direction.getY()){
            thisPosition = new Position(room.getRoomCenter().getX(), y);
            if (room.getCell(thisPosition) == null) {
                return prevPosition;
            }
            prevPosition = thisPosition;
        }
        return room.getRoomCenter().getRelativePosition(0, room.getHeight());
    }

    private void calculateDistances(){

        Room[] roomsArray = rooms.values().toArray(Room[]::new);

        int r;
        for(int l = 0; l < roomsArray.length; l++){
            r = l+1;
            Room first = roomsArray[l];

            while (r < roomsArray.length){
                Room second = roomsArray[r];
                double distance = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter())
                        .getDistance(Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter()));

                if(!distancesRooms.containsValue(Set.of(first.getRoomId(), second.getRoomId()))) {
                    distancesRooms.put(distance, new HashSet<>(Arrays.asList(first, second)));
                }
                r++;
            }
        }
    }
}
