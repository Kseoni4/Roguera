package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.utils.Convert;

import java.util.*;
import java.util.stream.Collectors;

public class MapGenerate {
    private final HashMap<Integer, Room> temporalRoomMap;

    public MapGenerate() {
        temporalRoomMap = new HashMap<>();
    }

    public HashMap<Integer, Room> initFloor(int roomCount) {
        //for(int i = 0; i < roomCount; i++){
        //}
        placeRoom(0, new Position(0, 0));
        placeRoom(1, new Position(4, 3));
        placeRoom(2, new Position(7, 3));
        placeRoom(3, new Position(15, 1));

        if (roomCount > 1) {
            intersectAndCombine(temporalRoomMap.get(0), temporalRoomMap.get(1));
        }

        //LinkedHashSet<Position> perimeter = findPerimeterPositions(temporalRoomMap.get(0));

        /*for (Position pos : perimeter) {
            temporalRoomMap.get(0).getCell(pos).placeObject(new Wall(new TextSprite('*')));
        }*/

        return temporalRoomMap;
    }

    private void placeRoom(int roomId, Position position) {
        Room newRoom = new Room(roomId, 10, 15, position);
        newRoom.setCells(prepareCells(newRoom));

        temporalRoomMap.put(roomId, newRoom);
    }

    private HashMap<Position, Cell> prepareCells(Room room) {
        HashMap<Position, Cell> cells = new HashMap<>();

        System.out.println("Room id: ".concat(String.valueOf(room.getRoomId())));
        System.out.println("Top left pos ".concat(room.getRoomLeftTopPosition().toString()));

        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                Position pos = new Position(x, y);
                cells.put(pos, new Cell(pos));
            }
        }
        return cells;
    }

    private boolean hasIntersects(Room first, Room second) {
        Set<Position> firstRoomGlobalPositions = Convert.toGlobalPositions(first);
        Set<Position> secondRoomGlobalPositions = Convert.toGlobalPositions(second);

        firstRoomGlobalPositions.retainAll(secondRoomGlobalPositions);

        System.out.println(firstRoomGlobalPositions);

        return !firstRoomGlobalPositions.isEmpty();
    }

    private Room intersectAndCombine(Room first, Room second) {
        if (Objects.isNull(second)) {
            return first;
        }
        if (!hasIntersects(first, second)) {
            return first;
        }

        Room combined = combine(first, second);
        temporalRoomMap.remove(first.getRoomId());
        second = temporalRoomMap.remove(second.getRoomId());
        temporalRoomMap.put(combined.getRoomId(), combined);

        return intersectAndCombine(combined, temporalRoomMap.get(second.getRoomId() + 1));
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
                first.getWidth() + second.getWidth(),
                first.getHeight() + second.getHeight(),
                leftTopPosition);

        newRoom.setCells(newCells);

        return newRoom;
    }

    private LinkedHashSet<Position> findPerimeterPositions(Room room){
        Cell currentPoint = room.getCell(new Position(1,0));

        Map<Position, Cell> cells = room.getCells();

        LinkedHashSet<Position> perimeter = new LinkedHashSet<>();

        while (!currentPoint.getPosition().equals(room.getRoomLeftTopPosition())) {

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.RIGHT)) != null &&
                    !perimeter.contains(cells.get(currentPoint.getPosition().getRelativePosition(Position.RIGHT)).getPosition())
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.RIGHT);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.BACK)) != null
                    && !perimeter.contains(cells.get(currentPoint.getPosition().getRelativePosition(Position.BACK)).getPosition())
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.BACK);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.BACK)) == null
                    && cells.get(currentPoint.getPosition().getRelativePosition(Position.RIGHT)) != null
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.RIGHT);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.RIGHT)) == null
                    && cells.get(currentPoint.getPosition().getRelativePosition(Position.BACK)) != null
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.BACK);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.BACK)) == null
                    && cells.get(currentPoint.getPosition().getRelativePosition(Position.FRONT)) != null
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.FRONT);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.FRONT)) == null
                    && cells.get(currentPoint.getPosition().getRelativePosition(Position.LEFT)) != null
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.LEFT);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.LEFT)) == null
                    && cells.get(currentPoint.getPosition().getRelativePosition(Position.FRONT)) != null
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.FRONT);
            }

            if (cells.get(currentPoint.getPosition().getRelativePosition(Position.LEFT)) == null
                    && cells.get(currentPoint.getPosition().getRelativePosition(Position.BACK)) != null
            ) {
                currentPoint = moveOn(currentPoint, cells, perimeter, Position.BACK);
            }

        }
        return perimeter;
    }

    private Cell moveOn(
            Cell cursor,
            Map<Position, Cell> cells,
            Set<Position> perimeter,
            Position relativePosition) {
        while (cells.get(cursor.getPosition().getRelativePosition(relativePosition)) != null) {
            cursor = cells.get(cursor.getPosition().getRelativePosition(relativePosition));
            System.out.println("Current point " + cursor.getPosition());
            perimeter.add(cursor.getPosition());
        }
        return cursor;
    }

/*private void createShape(HashMap<Position, Cell> cells, Room room){
        AssetPool assetPool = AssetPool.get();

        RectangleShape roomShape = RectangleShape.builder()
                .bottomLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_l")))
                .bottomRightCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_r")))
                .topLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_top_l")))
                .topRightCorner(new TextSprite(assetPool.getAsset("wall_corner_top_r")))
                .horizontalSprite(new TextSprite(assetPool.getAsset("wall_h")))
                .verticalSprite(new TextSprite(assetPool.getAsset("wall_v")))
                .width(room.getWidth()-1)
                .height(room.getHeight()-1)
                .topLeftPosition(room.getRoomLeftTopPosition())
                .build();

        Position topLeft = roomShape.getTopLeftPosition();
        Position topRight = room.getRoomLeftTopPosition().getRelativePosition(roomShape.getWidth(),0);
        Position bottomLeft = room.getRoomLeftTopPosition().getRelativePosition(0,roomShape.getHeight());
        Position bottomRight = room.getRoomLeftTopPosition().getRelativePosition(roomShape.getWidth(), roomShape.getHeight());

        for(int x = topLeft.getX(); x < topRight.getX(); x++){
            Cell cell = cells.get(new Position(x, topRight.getY()));
            cell.placeObject(new Wall(roomShape.getHorizontalSprite()));
            cell.setWall(true);
        }

        for(int x = bottomLeft.getX(); x < bottomRight.getX(); x++){
            Cell cell = cells.get(new Position(x, bottomLeft.getY()));
            cell.placeObject(new Wall(roomShape.getHorizontalSprite()));
            cell.setWall(true);
        }

        for(int y = topLeft.getY(); y < bottomLeft.getY(); y++){
            Cell cell = cells.get(new Position(topLeft.getX(), y));
            cell.placeObject(new Wall(roomShape.getVerticalSprite()));
            cell.setWall(true);
        }

        for(int y = topRight.getY(); y < bottomRight.getY(); y++){
            Cell cell = cells.get(new Position(topRight.getX(), y));
            cell.placeObject(new Wall(roomShape.getVerticalSprite()));
            cell.setWall(true);
        }

        cells.get(topLeft).replaceObject(new Wall(roomShape.getTopLeftCorner()));
        cells.get(topRight).replaceObject(new Wall(roomShape.getTopRightCorner()));
        cells.get(bottomLeft).replaceObject(new Wall(roomShape.getBottomLeftCorner()));
        cells.get(bottomRight).replaceObject(new Wall(roomShape.getBottomRightCorner()));
    }
 */
}