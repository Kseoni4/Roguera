package kseoni.ch.roguera.map;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.utils.Convert;

import java.util.*;
import java.util.stream.Collectors;

public class MapGenerate_ {
    private final HashMap<Integer, Room> temporalRoomMap;

    private Map<Position, Cell> roomCells;


    public MapGenerate_() {
        temporalRoomMap = new HashMap<>();
    }

    public HashMap<Integer, Room> initFloor(int roomCount) {
        placeRoom(0, new Position(18, 2));
        placeRoom(1, new Position(15, 1));
        placeRoom(2, new Position(5, 3));
        placeRoom(3, new Position(19, 1));
        placeRoom(4, new Position(6, 3));
        placeRoom(5, new Position(32, 2));
        placeRoom(6, new Position(27, 2));
/*        for (int i = 0; i < roomCount; i++) {
            placeRoom(i, Position.getRandomPosition(30, 10));
        }*/

        Set<Room> rooms;

        if (roomCount > 1) {
            rooms = overlappingRooms();
            System.out.println(rooms);
            List<Set<Room>> clusters = clusterizingRooms(rooms);
            System.out.println("Clusters count: "+clusters.size());
            System.out.println("Clusters:");
            int i = 0;
            for (Set<Room> cluster : clusters) {
                System.out.print("\tCluster ["+i+"]: ");
                System.out.println(cluster);
                i++;
            }
            for (Set<Room> cluster : clusters) {
                if(cluster.size() < 2){
                    continue;
                }
                Iterator<Room> roomIterator = cluster.iterator();
                Room firstRoom = roomIterator.next();
                Room secondRoom = roomIterator.next();
                Room combined = intersectAndCombine(roomIterator, firstRoom, secondRoom);
                temporalRoomMap.put(combined.getRoomId(), combined);
            }
        }

        for(Room room: temporalRoomMap.values()){
            LinkedHashSet<Position> corners = findCorners(room);
            createShape(room, corners);
        }

        return temporalRoomMap;
    }

    private void placeRoom(int roomId, Position position) {
        Random rnd = new Random();

        Room newRoom = new Room(roomId, rnd.nextInt(5,  15),
                rnd.nextInt(5, 15), position);
        newRoom.setCells(prepareCells(newRoom));

        newRoom.getCell(new Position(1,1)).placeObject(new Wall(new TextSprite(Character.forDigit(newRoom.getRoomId(), Character.MAX_RADIX))));

        temporalRoomMap.put(roomId, newRoom);
    }

    private Set<Room> overlappingRooms(){
        Set<Room> rooms = new LinkedHashSet<>();
        for(Room first : temporalRoomMap.values()) {
            for (int i = 0; i < temporalRoomMap.values().size(); i++) {
                Room second = temporalRoomMap.get(i);
                if (hasIntersects(first, second)) {
                    rooms.add(second);
                    rooms.add(first);
                }
            }
        }
        return rooms;
    }

    private List<Set<Room>> clusterizingRooms(Set<Room> overlappingRooms){
        List<Set<Room>> clusters = new ArrayList<>();
        Set<Room> visited = new HashSet<>();

        Set<Room> rooms = overlappingRooms;

        for (Room room : rooms) {
            if (!visited.contains(room)) {
                Set<Room> cluster = new HashSet<>();
                Queue<Room> toVisit = new LinkedList<>();
                toVisit.add(room);

                while (!toVisit.isEmpty()) {
                    Room current = toVisit.poll();
                    if (visited.add(current)) {
                        current.getCell(Position.FRONT.getRelativePosition(3,0)).placeObject(new Wall(new TextSprite(Character.forDigit(clusters.size(), Character.MAX_RADIX),null, TextColor.ANSI.CYAN)));
                        cluster.add(current);
                        double threshold = calculateDynamicThreshold(current, rooms);
                        rooms.stream()
                                .filter(r -> !visited.contains(r) && distance(current, r) <= 5)
                                .forEach(toVisit::add);
                    }
                }
                clusters.add(cluster);
            }
        }
        return clusters;
    }

    private double distance(Room a, Room b) {
        Position coordsA = a.getRoomLeftTopPosition();
        Position coordsB = b.getRoomLeftTopPosition();
        return Math.sqrt(Math.pow(coordsA.getX() - coordsB.getX(), 2) + Math.pow(coordsA.getY() - coordsB.getY(), 2));
    }

    private double calculateDynamicThreshold(Room room, Set<Room> rooms) {
        // Пример динамического порога, учитывающего размеры комнаты
        int width = room.getWidth();
        int height = room.getHeight();

        double areaFactor = Math.sqrt(width * height);

        double averageDistance = rooms.stream()
                .filter(r -> r != room)
                .mapToDouble(r -> distance(room, r))
                .average()
                .orElse(Double.MAX_VALUE);

        long neighborCount = rooms.stream()
                .filter(r -> r != room && distance(room, r) <= 10) // Example fixed radius for density calculation
                .count();
        double densityFactor = (double) 1 / (1 + neighborCount);

        return areaFactor * 1.5 + averageDistance * 0.5 + densityFactor * 100;
    }

    private HashMap<Position, Cell> prepareCells(Room room) {
        HashMap<Position, Cell> cells = new HashMap<>();

        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                Position pos = new Position(x, y);
                cells.put(pos, new Cell(pos));
            }
        }
        return cells;
    }

    private boolean hasIntersects(Room first, Room second) {

        if(Objects.isNull(first) || Objects.isNull(second)){
            return false;
        }

        Set<Position> firstRoomGlobalPositions = Convert.toGlobalPositions(first);
        Set<Position> secondRoomGlobalPositions = Convert.toGlobalPositions(second);

        firstRoomGlobalPositions.retainAll(secondRoomGlobalPositions);

        return !firstRoomGlobalPositions.isEmpty();
    }

    private Room intersectAndCombine(Iterator<Room> rooms, Room first, Room second) {
        if(!rooms.hasNext()){
            return combine(first, second);
        }

        if (Objects.isNull(second)) {
            return first;
        }

        if(!hasIntersects(first, second)){
            return intersectAndCombine(rooms, first, rooms.next());
        }

/*      while (!hasIntersects(first, second)) {
            if(rooms.iterator().hasNext()) {
                Room room = rooms.iterator().next();
                rooms.remove(room);
                second = intersectAndCombine(rooms, first, room);
            } else {
                break;
            }
        }
        */
        //rooms.remove();
        //rooms.remove();
        temporalRoomMap.remove(first.getRoomId());
        temporalRoomMap.remove(second.getRoomId());

        if(!rooms.hasNext()){
            return combine(first, second);
        }

        //Room combined = combine(first, second)

        return intersectAndCombine(rooms, second, rooms.next());

        /*return intersectAndCombine(rooms, combined, rooms.iterator().next());*/
    }

    private Room combine(Room first, Room second) {

        Position fLt = first.getRoomLeftTopPosition();
        Position sLt = second.getRoomLeftTopPosition();

        List<Cell> cells = second.getCells()
                .values()
                .stream()
                .peek(
                        cell -> {
                            Position delta = new Position(
                                    Math.abs(fLt.getX() - cell.getPosition().getRelativePosition(sLt).getX()),
                                    Math.abs(fLt.getY() - cell.getPosition().getRelativePosition(sLt).getY()));
                            cell.getPosition().set(delta);
                        }).toList();
        HashMap<Position, Cell> newCells = new HashMap<>(first.getCells());

        for (Cell cell : cells) {
            newCells.put(cell.getPosition(), cell);
        }
        Position leftTopPosition = new Position(
                Math.min(first.getRoomLeftTopPosition().getX(), second.getRoomLeftTopPosition().getX()),
                Math.min(first.getRoomLeftTopPosition().getY(), second.getRoomLeftTopPosition().getY()));

        Room newRoom = new Room(
                first.getRoomId(),
                newCells.keySet().stream().max(Comparator.comparing(Position::getX)).map(Position::getX).get(),
                newCells.keySet().stream().max(Comparator.comparing(Position::getY)).map(Position::getY).get(),
                leftTopPosition);

        newRoom.setCells(newCells);

        return newRoom;
    }

    private LinkedHashSet<Position> findCorners(Room room){
        roomCells = room.getCells();

        LinkedHashSet<Position> corners = roomCells.values()
                .stream()
                    .filter(
                        cell ->  Arrays.stream(cell.getCellsAround(room)).filter(Objects::isNull).count() >= 4
                            || Arrays.stream(cell.getCellsAround(room)).filter(Objects::isNull).count() == 1
        ).map(Cell::getPosition).sorted(
                Comparator
                        .comparing(Position::getX)
                        .thenComparing(Position::getY)
                )
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return corners;
    }
    private void createShape(Room room, LinkedHashSet<Position> corners) {
        AssetPool assetPool = AssetPool.get();

        RectangleShape roomShape = RectangleShape.builder()
                .bottomLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_l")))
                .bottomRightCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_r")))
                .topLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_top_l")))
                .topRightCorner(new TextSprite(assetPool.getAsset("wall_corner_top_r")))
                .horizontalSprite(new TextSprite(assetPool.getAsset("wall_h")))
                .verticalSprite(new TextSprite(assetPool.getAsset("wall_v")))
                .width(room.getWidth() - 1)
                .height(room.getHeight() - 1)
                .topLeftPosition(room.getRoomLeftTopPosition())
                .build();

        LinkedHashSet<Position> tempCorners = new LinkedHashSet<>(corners);

        while (tempCorners.size() > 1){
            Position p2 = tempCorners.removeLast();
           for(Position corner : tempCorners){
               if(corner.getX() == p2.getX() && corner.getY() < p2.getY()){
                   buildShape(room.getCells(), corner, p2, Position.FRONT, new Wall(roomShape.getVerticalSprite()));
               }

               if(corner.getX() < p2.getX() && corner.getY() == p2.getY()){
                   buildShape(room.getCells(), corner, p2, Position.RIGHT, new Wall(roomShape.getHorizontalSprite()));
               }
           }
        }

        for (Position corner : corners){
            Cell cell = room.getCell(corner);
            if(Objects.isNull(cell)){
                continue;
            }
            if(!cell.isWall()){
                Cell nearCellFirst = room.getCell(corner.getRelativePosition(Position.RIGHT));
                Cell nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));

                if(Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond) && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")){
                    cell.replaceObject(new Wall(roomShape.getTopLeftCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.RIGHT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.BACK));

                if(Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")){
                    cell.replaceObject(new Wall(roomShape.getBottomLeftCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));

                if(Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")){
                    cell.replaceObject(new Wall(roomShape.getTopRightCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.BACK));

                if(Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")){
                    cell.replaceObject(new Wall(roomShape.getBottomRightCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));

                if(Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                    && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.isEmpty()){
                    cell.replaceObject(new Wall(roomShape.getTopRightCorner()));
                    nearCellSecond.replaceObject(new Wall(roomShape.getBottomLeftCorner()));
                }

            }
        }

    }

    private void buildShape(HashMap<Position, Cell> cells,
                            Position from,
                            Position to,
                            Position direction,
                            Wall wallShape){
        Cell cell = cells.get(from.getRelativePosition(direction));

        if(Objects.isNull(cell)){
            return;
        }

        if(Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.LEFT)))
           && Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.RIGHT)))
            && direction.equals(Position.FRONT)) {
            return;
        }

        while (!cell.getPosition().equals(to)){
            cell.replaceObject(wallShape);
            cell = cells.get(cell.getPosition().getRelativePosition(direction));
            if(Objects.isNull(cell)){
                break;
            }
        }
    }
}