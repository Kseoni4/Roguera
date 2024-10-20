package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.items.Item;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapGenerate {
    private final HashMap<Integer, Room> temporalRoomMap;

    public MapGenerate(){
        temporalRoomMap = new HashMap<>();
    }

    public HashMap<Integer, Room> initFloor(int roomCount){
        placeRoom(0, Position.ZERO);
        placeRoom(1, Position.getRandomPosition(12, 5));
        for(int i = 0; i < temporalRoomMap.size()-1; i++){
            removeIntersects(temporalRoomMap.get(i), temporalRoomMap.get(i+1));
        }
        return temporalRoomMap;
    }

    private void placeRoom(int roomId, Position position){
        Room newRoom = new Room(roomId, 10, 15, position);
        newRoom.setCells(prepareCells(newRoom));

        temporalRoomMap.put(roomId, newRoom);
    }

    private HashMap<Position, Cell> prepareCells(Room room){
        HashMap<Position, Cell> cells = new HashMap<>();

        System.out.println("Room id: ".concat(String.valueOf(room.getRoomId())));
        System.out.println("Top left pos ".concat(room.getRoomLeftTopPosition().toString()));

        for(int x = room.getRoomLeftTopPosition().getX(); x < room.getWidth()+room.getRoomLeftTopPosition().getX(); x++){
            for(int y = room.getRoomLeftTopPosition().getY(); y < room.getHeight()+room.getRoomLeftTopPosition().getY(); y++){
                Position pos = new Position(x,y);
                cells.put(pos, new Cell(pos));
            }
        }
        createShape(cells, room);

        return cells;
    }

    private void createShape(HashMap<Position, Cell> cells, Room room){
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

    private void removeIntersects(Room roomOne, Room roomTwo){
        Set<Position> positionsRoomOne = roomOne.getCells().keySet();
        Set<Position> positionsRoomTwo = roomTwo.getCells().keySet();
        Set<Position> intersects = new HashSet<>(positionsRoomOne);
        intersects.retainAll(positionsRoomTwo);

        if(intersects.isEmpty()){
            return;
        }

        for(Position position : intersects){
            findIntersect(roomOne, position);
            findIntersect(roomTwo, position);
        }

        roomOne.getCells().putAll(roomTwo.getCells());

        roomOne.setWidth(roomOne.getCells().values()
                .stream()
                .map(Cell::getPosition)
                .map(Position::getX)
                .max(Comparator.comparingInt(Integer::intValue))
                .get());
        roomOne.setHeight(roomOne.getCells().values()
                .stream()
                .map(Cell::getPosition)
                .map(Position::getY)
                .max(Comparator.comparingInt(Integer::intValue))
                .get());
        System.out.println("Intersects removed: "+intersects);
    }

    private void findIntersect(Room otherRoom, Position position) {
        Cell cell = otherRoom.getCells().get(position);
        if (cell.isWall()) {
            System.out.println("Find intersect on position ".concat(cell.getPosition().toString()));
            cell.replaceObject(GameObject.getEmpty());
            cell.setWall(false);
        }
    }
}
