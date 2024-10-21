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

    private Map<Position, Cell> roomCells;


    public MapGenerate() {
        temporalRoomMap = new HashMap<>();
    }

    public HashMap<Integer, Room> initFloor(int roomCount) {
        placeRoom(0, new Position(0, 0));
        placeRoom(1, new Position(4, 3));
        placeRoom(2, new Position(7, 3));
        placeRoom(3, new Position(15, 1));
        placeRoom(4, new Position(18, 2));
        placeRoom(5, new Position(24, 5));

        if (roomCount > 1) {
            intersectAndCombine(temporalRoomMap.get(0), temporalRoomMap.get(1));
        }
        LinkedHashSet<Position> corners = findCorners(temporalRoomMap.get(0));
        createShape(temporalRoomMap.get(0), corners);

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

        System.out.println("Find corners = "+corners);

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

               System.out.println("Get corners "+ corner +" -> "+p2);

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
        System.out.println("Get relative ="+direction);
        System.out.println("Get from = "+from);
        Cell cell = cells.get(from.getRelativePosition(direction));

        if(Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.LEFT)))
           && Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.RIGHT)))
            && direction.equals(Position.FRONT)) {
            return;
        }

        while (!cell.getPosition().equals(to)){
            cell.replaceObject(wallShape);
            cell = cells.get(cell.getPosition().getRelativePosition(direction));
        }
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