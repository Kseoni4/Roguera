package kseoni.ch.roguera.map.generator;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.map.Wall;
import kseoni.ch.roguera.utils.Convert;
import lombok.SneakyThrows;

import java.util.*;

class PhaseTwoMergeRooms {

    private Map<Integer, Room> tempRoomMap;

    private final MapGenerate mapGen;

    public PhaseTwoMergeRooms(MapGenerate mapGen,Map<Integer, Room> tempRoomMap){
        this.tempRoomMap = tempRoomMap;
        this.mapGen = mapGen;
    }

    @SneakyThrows
    public void mergeRooms() {
        // Clusterization rooms
        List<Set<Room>> clusters = clusterizeRooms();

        mapGen.generateDebugLog("Clusters count: " + clusters.size());
        mapGen.generateDebugLog("Clusters:");
        int i = 0;
        for (Set<Room> cluster : clusters) {
            mapGen.generateDebugLog("\tCluster [" + i + "]: ");
            mapGen.generateDebugLog(cluster.toString());
            i++;
        }

        i = 0;
        for (Set<Room> cluster : clusters) {
            mapGen.generateDebugLog("Merge rooms in cluster " + "[" + i + "]");
            i++;
            if (cluster.size() < 2) {
                continue;
            }
            Iterator<Room> clusterIterator = cluster.iterator();
            Room first = clusterIterator.next();
            Room second = clusterIterator.next();
            Room combined = intersectAndCombine(clusterIterator, first, second);
            tempRoomMap.put(combined.getRoomId(), combined);

/*            LinkedHashSet<Position> cornersPosition = findCorners(combined);

            while (cornersPosition.size() > 0) {
                Position cornerPosition = cornersPosition.removeFirst();
                combined.getCell(cornerPosition).placeObject(new Wall(new TextSprite('.', TextColor.ANSI.RED_BRIGHT, null)));
            }*/
        }


        // Merge rooms in each cluster into a single room
        // Remove rooms from tempMap and add only merged
    }

    private List<Set<Room>> clusterizeRooms() {
        List<Set<Room>> clusters = new ArrayList<>();
        Set<Room> visited = new HashSet<>();

        Set<Room> rooms = new HashSet<>(tempRoomMap.values());

        for (Room room : rooms) {
            if (!visited.contains(room)) {
                Set<Room> cluster = new HashSet<>();
                Queue<Room> toVisit = new LinkedList<>();
                toVisit.add(room);

                while (!toVisit.isEmpty()) {
                    Room currentRoom = toVisit.poll();
                    if (visited.add(currentRoom)) {
                        cluster.add(currentRoom);
                        rooms.stream()
                                .filter(
                                        r -> {
                                            Position currentRoomCenterGlobal = Convert.toGlobalPosition(currentRoom.getRoomLeftTopPosition(), currentRoom.getRoomCenter());
                                            Position otherRoomCenterGlobal = Convert.toGlobalPosition(r.getRoomLeftTopPosition(), r.getRoomCenter());
                                            double betweenCentersDistance = currentRoomCenterGlobal.getDistance(otherRoomCenterGlobal);
                                            return !visited.contains(r)
/*                                                    && (betweenCentersDistance <= (double) r.getWidth()
                                                    || betweenCentersDistance <= (double) r.getHeight())*/
                                                    && hasIntersects(currentRoom, r);
                                        })
                                .forEach(toVisit::add);
                    }
                }
                clusters.add(cluster);
            }
        }
        return clusters;
    }

    private Room intersectAndCombine(Iterator<Room> clusterIterator, Room first, Room second) {
        if (Objects.isNull(second)) {
            tempRoomMap.remove(first.getRoomId());
            return first;
        }

        if (!clusterIterator.hasNext()) {
            tempRoomMap.remove(first.getRoomId());
            tempRoomMap.remove(second.getRoomId());
            return combine(first, second);
        }

        tempRoomMap.remove(first.getRoomId());

        Room next = clusterIterator.next();

        Room combined = intersectAndCombine(clusterIterator, second, next);

        return intersectAndCombine(clusterIterator, first, combined);
    }

    private Room combine(Room first, Room second) {
        /*System.out.println("==========COMBINE============");
        System.out.println("Room first: ");
        System.out.println(first);
        System.out.println("Room second: ");
        System.out.println(second);*/

        Position fLt = first.getRoomLeftTopPosition();
        Position sLt = second.getRoomLeftTopPosition();

        Position newLt = new Position(
                Math.min(fLt.getX(), sLt.getX()),
                Math.min(fLt.getY(), sLt.getY())
        );

        // Сдвиги для перевода локальных координат в новый фрейм.
        int dxFirst  = fLt.getX() - newLt.getX();
        int dyFirst  = fLt.getY() - newLt.getY();
        int dxSecond = sLt.getX() - newLt.getX();
        int dySecond = sLt.getY() - newLt.getY();

        HashMap<Position, Cell> newCells = new HashMap<>();

        // 1) cells первой комнаты — со сдвигом dxFirst/dyFirst.
        for (Cell c : first.getCells().values()) {
            Position np = new Position(
                    c.getPosition().getX() + dxFirst,
                    c.getPosition().getY() + dyFirst
            );
            newCells.put(np, new Cell(np /*, content*/));   // НЕ мутируем старую позицию
        }

        // 2) cells второй — со сдвигом dxSecond/dySecond.
        for (Cell c : second.getCells().values()) {
            Position np = new Position(
                    c.getPosition().getX() + dxSecond,
                    c.getPosition().getY() + dySecond
            );
            newCells.putIfAbsent(np, new Cell(np /*, content*/));  // не перезаписываем пересечения
        }

        // 3) Размер — bounding box ПО КЛЕТКАМ, с +1.
        int maxX = newCells.keySet().stream().mapToInt(Position::getX).max().orElse(0);
        int maxY = newCells.keySet().stream().mapToInt(Position::getY).max().orElse(0);
        int newWidth  = maxX + 1;
        int newHeight = maxY + 1;

        Room newRoom = new Room(first.getRoomId(), newWidth, newHeight, newLt);
        newRoom.setCells(newCells);
        return newRoom;

        /*//System.out.println("First left top = "+fLt);

        //System.out.println("Second left top = "+sLt);

        //System.out.println("Calculate delta from second room cells");
        //System.out.println("======================================");
        List<Cell> cells = second.getCells()
                .values()
                .stream()
                .peek(
                        cell -> {
                            Position delta = new Position(
                                    Math.abs(fLt.getX() - cell.getPosition().getRelativePosition(sLt).getX()),
                                    Math.abs(fLt.getY() - cell.getPosition().getRelativePosition(sLt).getY())
                            );
                            cell.getPosition().set(delta);
                        }).toList();
        HashMap<Position, Cell> newCells = new HashMap<>(first.getCells());

        for (Cell cell : cells) {
            newCells.put(cell.getPosition(), cell);
        }

        Position leftTopPosition = new Position(
                Math.min(first.getRoomLeftTopPosition().getX(), second.getRoomLeftTopPosition().getX()),
                Math.min(first.getRoomLeftTopPosition().getY(), second.getRoomLeftTopPosition().getY()));

        int newWidth = newCells.keySet().stream()
                .max(Comparator.comparingInt(Position::getX))
                .map(Position::getX).get() + 1;

        int newHeight = newCells.keySet().stream()
                .max(Comparator.comparingInt(Position::getY))
                .map(Position::getY).get() + 1;

        Room newRoom = new Room(
                first.getRoomId(),
                newWidth,
                newHeight,
                leftTopPosition);

        newRoom.setCells(newCells);


        *//*newRoom.getCells()
                .values()
                .forEach(cell -> cell.placeObject(
                        new Wall(new TextSprite(Character.forDigit(newRoom.getRoomId(), Character.MAX_RADIX)
                        ))
                ));*//*

        return newRoom;*/
    }

    private boolean hasIntersects(Room first, Room second) {

        if (Objects.isNull(first) || Objects.isNull(second)) {
            return false;
        }

        Set<Position> firstRoomGlobalPositions = Convert.toGlobalPositions(first);
        Set<Position> secondRoomGlobalPositions = Convert.toGlobalPositions(second);

        firstRoomGlobalPositions.retainAll(secondRoomGlobalPositions);

        return !firstRoomGlobalPositions.isEmpty();
    }


}
