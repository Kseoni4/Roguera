package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.Palette;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.graphics.sprites.TilesUtils;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.Convert;
import kseoni.ch.roguera.utils.PositionUtils;
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

    record Edge(Room a, Room b, double dist) {}

    private List<Edge> buildMst(Collection<Room> rooms){

        mapGen.generateDebugLog("Build MST for rooms");

        List<Edge> allEdges = new ArrayList<>();
        Room[] arr = rooms.toArray(Room[]::new);

        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                Position ci = Convert.toGlobalPosition(arr[i].getRoomLeftTopPosition(), arr[i].getRoomCenter());
                Position cj = Convert.toGlobalPosition(arr[j].getRoomLeftTopPosition(), arr[j].getRoomCenter());
                allEdges.add(new Edge(arr[i], arr[j], ci.getDistance(cj)));
            }
        }

        allEdges.sort(Comparator.comparingDouble(Edge::dist));

        mapGen.generateDebugLog("Sorted edges: %s".formatted(allEdges));

        DSU<Room> dsu = new DSU<>(rooms);
        List<Edge> mst = new ArrayList<>();

        mapGen.generateDebugLog("Created DSU %s".formatted(dsu.parent));

        for (Edge e : allEdges) {
            if (dsu.union(e.a, e.b)) {           // вернёт true если объединили (были в разных)
                mst.add(e);
                if (mst.size() == rooms.size() - 1) break;
            }
        }
        return mst;

    }

    public void connectRooms(){
        List<Edge> mst = buildMst(rooms.values());

        mapGen.generateDebugLog("Built MST: %s".formatted(mst));

        for (Edge e : mst) {
            Room first = e.a();
            Room second = e.b();
            placeDoorBetween(first, second);   // те же 80 строк, вынесенные в метод
        }
    }

    private void placeDoorBetween(Room first, Room second) {
        Position centerA = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter());
        Position centerB = Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter());

        int dx = centerB.getX() - centerA.getX();
        int dy = centerB.getY() - centerA.getY();

        if (Math.abs(dx) >= Math.abs(dy)) {
            // По X разнесены сильнее → дверь на боковой стене.
            boolean bIsRight = dx >= 0;
            Position placeA = findToPlaceDoorX(first,  bIsRight ? Position.RIGHT : Position.LEFT);
            Position placeB = findToPlaceDoorX(second, bIsRight ? Position.LEFT  : Position.RIGHT);
            installDoorPair(first, second, placeA, placeB, AssetPool.get().getAsset("door_v"));
        } else {
            // По Y разнесены сильнее → дверь на верхней/нижней стене.
            boolean bIsBelow = dy >= 0;
            Position placeA = findToPlaceDoorY(first,  bIsBelow ? Position.FRONT : Position.BACK);
            Position placeB = findToPlaceDoorY(second, bIsBelow ? Position.BACK  : Position.FRONT);
            installDoorPair(first, second, placeA, placeB, AssetPool.get().getAsset("door_h"));
        }
    }

    private void installDoorPair(Room first, Room second, Position placeA, Position placeB, char glyph) {
        Door doorInFirst  = createDoor(glyph, second, first);
        Door doorInSecond = createDoor(glyph, first,  second);

        if (first.getCell(placeA) != null && first.getCell(placeA).isEmpty()) {
            first.getCell(placeA).placeObject(doorInFirst);
            doorInSecond.setNextDoor(doorInFirst);
            first.addDoor(doorInFirst);
        }
        if (second.getCell(placeB) != null && second.getCell(placeB).isEmpty()) {
            second.getCell(placeB).placeObject(doorInSecond);
            doorInFirst.setNextDoor(doorInSecond);
            second.addDoor(doorInSecond);
        }
    }

    private void placeDoorBetween_(Room first, Room second) {
        char doorV = AssetPool.get().getAsset("door_v");
        char doorH = AssetPool.get().getAsset("door_h");


        Position globalCenterFirstRoom = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter());
        Position globalCenterSecondRoom = Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter());

        mapGen.generateDebugLog("First room global center = %s".formatted(globalCenterFirstRoom));
        mapGen.generateDebugLog("Second room global center = %s".formatted(globalCenterSecondRoom));

        // Если вторая комната находится между левым и правым краем первой комнаты по X.
        if (globalCenterSecondRoom.isInBetweenX(
                first.getRoomLeftTopPosition(),
                first.getRoomLeftTopPosition().getRelativePosition(first.getWidth(), 0))
        ) {

            Door doorInFirstRoom = createDoor(doorH, second, first);
            Door doorInSecondRoom = createDoor(doorH, first, second);

            Position doorPlaceFirst;
            Position doorPlaceSecond;

            if (globalCenterSecondRoom.getY() >= globalCenterFirstRoom.getY()) {
                doorPlaceFirst = findToPlaceDoorY(first, Position.FRONT);
                doorPlaceSecond = findToPlaceDoorY(second, Position.BACK);
            } else {
                doorPlaceFirst = findToPlaceDoorY(first, Position.BACK);
                doorPlaceSecond = findToPlaceDoorY(second, Position.FRONT);
            }
            mapGen.generateDebugLog("Find door(H) first room place " + doorPlaceFirst);
            mapGen.generateDebugLog("Find door(H) second room place " + doorPlaceSecond);


            if (first.getCell(doorPlaceFirst).isEmpty()) {
                first.getCell(doorPlaceFirst).placeObject(doorInFirstRoom);
                doorInSecondRoom.setNextDoor(doorInFirstRoom);
                first.addDoor(doorInFirstRoom);
            }

            if (second.getCell(doorPlaceSecond).isEmpty()) {
                second.getCell(doorPlaceSecond).placeObject(doorInSecondRoom);
                doorInFirstRoom.setNextDoor(doorInSecondRoom);
                second.addDoor(doorInSecondRoom);
            }
            return;
        }

        // Если вторая комната находится между верхним и нижним краем первой комнаты по Y.
        if (globalCenterSecondRoom.isInBetweenY(
                first.getRoomLeftTopPosition(),
                first.getRoomLeftTopPosition().getRelativePosition(0, first.getHeight()))
        ) {
            Door doorInFirstRoom = createDoor(doorV, second, first);
            Door doorInSecondRoom = createDoor(doorV, first, second);

            Position doorPlaceFirst;
            Position doorPlaceSecond;

            if (globalCenterSecondRoom.getX() >= globalCenterFirstRoom.getX()) {
                doorPlaceFirst = findToPlaceDoorX(first, Position.RIGHT);
                doorPlaceSecond = findToPlaceDoorX(second, Position.LEFT);
            } else {
                doorPlaceFirst = findToPlaceDoorX(first, Position.LEFT);
                doorPlaceSecond = findToPlaceDoorX(second, Position.RIGHT);
            }
            mapGen.generateDebugLog("Find door(V) first room place " + doorPlaceFirst);
            mapGen.generateDebugLog("Find door(V) second room place " + doorPlaceSecond);

            if (first.getCell(doorPlaceFirst).isEmpty()) {
                first.getCell(doorPlaceFirst).placeObject(doorInFirstRoom);
                doorInSecondRoom.setNextDoor(doorInFirstRoom);
                first.addDoor(doorInFirstRoom);
            }

            if (second.getCell(doorPlaceSecond).isEmpty()) {
                second.getCell(doorPlaceSecond).placeObject(doorInSecondRoom);
                doorInFirstRoom.setNextDoor(doorInSecondRoom);
                second.addDoor(doorInSecondRoom);
            }
        }
    }

    @SneakyThrows
    public void connectRooms_() {
        // Сначала считаем дистанции между комнатами
        calculateDistances();

        if (mapGen.isDebugShowDungeonGenerate()) {
            for (Map.Entry entry : distancesRooms.entrySet()) {
                mapGen.generateDebugLog("Rooms: " + entry.getValue());
                mapGen.generateDebugLog("Distance between: " + entry.getKey());
            }
        }

        char doorV = AssetPool.get().getAsset("door_v");
        char doorH = AssetPool.get().getAsset("door_h");

        while (!distancesRooms.isEmpty()) {
            // Выбираем две ближайших комнаты в словаре
            double minDistance = distancesRooms.keySet().stream().min(Comparator.comparing(Double::doubleValue)).get();

            // Убираем из словаря и достаём итератор
            Set<Room> roomSet = distancesRooms.remove(minDistance);

            Iterator<Room> it = roomSet.iterator();
            Room first = it.next();
            Room second = it.next();

            mapGen.generateDebugLog("Rooms with min distance: %s <--%f--> %s".formatted(
                    first.toString(),
                    minDistance,
                    second.toString()
            ));

            // Нашли глобальный центр комнат
//            Position globalCenterFirstRoom = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter());
//            Position globalCenterSecondRoom = Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter());
//
//            mapGen.generateDebugLog("First room global center = %s".formatted(globalCenterFirstRoom));
//            mapGen.generateDebugLog("Second room global center = %s".formatted(globalCenterSecondRoom));
//
//            // Если вторая комната находится между левым и правым краем первой комнаты по X.
//            if (globalCenterSecondRoom.isInBetweenX(
//                    first.getRoomLeftTopPosition(),
//                    first.getRoomLeftTopPosition().getRelativePosition(first.getWidth(),0))
//            ){
//
//                Door doorInFirstRoom = createDoor(doorH, second, first);
//                Door doorInSecondRoom = createDoor(doorH, first, second);
//
//                Position doorPlaceFirst;
//                Position doorPlaceSecond;
//
//                if(globalCenterSecondRoom.getY() >= globalCenterFirstRoom.getY()) {
//                    doorPlaceFirst = findToPlaceDoorY(first, Position.FRONT);
//                    doorPlaceSecond = findToPlaceDoorY(second, Position.BACK);
//                } else {
//                    doorPlaceFirst = findToPlaceDoorY(first, Position.BACK);
//                    doorPlaceSecond = findToPlaceDoorY(second, Position.FRONT);
//                }
//                mapGen.generateDebugLog("Find door(H) first room place "+doorPlaceFirst);
//                mapGen.generateDebugLog("Find door(H) second room place "+doorPlaceSecond);
//
//
//                if(first.getCell(doorPlaceFirst).isEmpty()) {
//                    first.getCell(doorPlaceFirst).placeObject(doorInFirstRoom);
//                    doorInSecondRoom.setNextDoor(doorInFirstRoom);
//                    first.addDoor(doorInFirstRoom);
//                }
//
//                if(second.getCell(doorPlaceSecond).isEmpty()) {
//                    second.getCell(doorPlaceSecond).placeObject(doorInSecondRoom);
//                    doorInFirstRoom.setNextDoor(doorInSecondRoom);
//                    second.addDoor(doorInSecondRoom);
//                }
//                continue;
//            }
//
//            // Если вторая комната находится между верхним и нижним краем первой комнаты по Y.
//            if (globalCenterSecondRoom.isInBetweenY(
//                    first.getRoomLeftTopPosition(),
//                    first.getRoomLeftTopPosition().getRelativePosition(0,first.getHeight()))
//            ){
//                Door doorInFirstRoom = createDoor(doorV, second, first);
//                Door doorInSecondRoom = createDoor(doorV, first, second);
//
//                Position doorPlaceFirst;
//                Position doorPlaceSecond;
//
//                if(globalCenterSecondRoom.getX() >= globalCenterFirstRoom.getX()) {
//                    doorPlaceFirst = findToPlaceDoorX(first, Position.RIGHT);
//                    doorPlaceSecond = findToPlaceDoorX(second, Position.LEFT);
//                } else {
//                    doorPlaceFirst = findToPlaceDoorX(first, Position.LEFT);
//                    doorPlaceSecond = findToPlaceDoorX(second, Position.RIGHT);
//                }
//                mapGen.generateDebugLog("Find door(V) first room place "+doorPlaceFirst);
//                mapGen.generateDebugLog("Find door(V) second room place "+doorPlaceSecond);
//
//                if(first.getCell(doorPlaceFirst).isEmpty()) {
//                    first.getCell(doorPlaceFirst).placeObject(doorInFirstRoom);
//                    doorInSecondRoom.setNextDoor(doorInFirstRoom);
//                    first.addDoor(doorInFirstRoom);
//                }
//
//                if(second.getCell(doorPlaceSecond).isEmpty()) {
//                    second.getCell(doorPlaceSecond).placeObject(doorInSecondRoom);
//                    doorInFirstRoom.setNextDoor(doorInSecondRoom);
//                    second.addDoor(doorInSecondRoom);
//                }
//            }
//        }
        }
    }

    private static Door createDoor(char doorSprite, Room secondRoom, Room firstRoom) {
        return new Door(new TextSprite(doorSprite, null, Palette.BASE), secondRoom.getRoomId(), firstRoom);
    }

    private Position findToPlaceDoorX(Room room, Position direction){
        Position prevPosition = room.getRoomCenter();
        Position thisPosition;
        for(int x = room.getRoomCenter().getX(); (x >= 0 || x < room.getWidth()); x += direction.getX()){
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
        for(int y = room.getRoomCenter().getY(); (y >= 0 || y < room.getHeight()); y+= direction.getY()){
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

                if(!distancesRooms.containsValue(Set.of(first.getRoomId(), second.getRoomId()))
                        && !distancesRooms.containsValue(Set.of(second.getRoomId(), first.getRoomId()))) {
                    distancesRooms.put(distance, new HashSet<>(Arrays.asList(first, second)));
                }
                r++;
            }
        }
    }

    private static class DSU<T> {
        private final Map<T, T> parent = new HashMap<>();

        DSU(Collection<T> items) { for (T t : items) parent.put(t, t); }

        T find(T x) {
            T p = parent.get(x);
            if (p == x) return x;
            T root = find(p);
            parent.put(x, root);          // path compression
            return root;
        }

        boolean union(T x, T y) {
            T rx = find(x), ry = find(y);
            if (rx == ry) return false;
            parent.put(rx, ry);
            return true;
        }
    }
}
