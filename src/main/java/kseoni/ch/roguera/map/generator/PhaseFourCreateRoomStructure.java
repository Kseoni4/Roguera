package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.graphics.sprites.TilesUtils;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.map.Wall;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class PhaseFourCreateRoomStructure {
    private Map<Integer, Room> rooms;
    
    private final MapGenerate mapGen;

    public PhaseFourCreateRoomStructure(MapGenerate mapGen, Map<Integer, Room> rooms) {
        this.rooms = rooms;
        this.mapGen = mapGen;
    }

    public void createRoomStructure() {
        mapGen.generateDebugLog("====Create Room Structure====");
        for (Room room : rooms.values()) {
            applyWalls(room);
        }
    }

    private void applyWalls(Room room){
        mapGen.generateDebugLog("Apply walls for room [%s]".formatted(room.getRoomId()));
        Map<Position, Cell> roomCells = room.getCells();

        LinkedHashSet<Position> edges = roomCells.values()
                .stream()
                .map(Cell::getPosition)
                .filter(p -> isEdge(p, roomCells))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        mapGen.generateDebugLog("Room [%s] edges %s".formatted(room.getRoomId(), edges));

        for (Position p : edges) {

            Cell cell = room.getCell(p);

            if(cell.getObject() instanceof Door) continue;

            int mask = 0;
            if (edges.contains(p.getRelativePosition(Position.BACK)))  mask |= 1 << 0; // N (BACK = -y)
            if (edges.contains(p.getRelativePosition(Position.RIGHT))) mask |= 1 << 1;
            if (edges.contains(p.getRelativePosition(Position.FRONT))) mask |= 1 << 2; // S (FRONT = +y)
            if (edges.contains(p.getRelativePosition(Position.LEFT)))  mask |= 1 << 3;

            char glyph = TilesUtils.getByMask(mask);

            cell.replaceObject(new Wall(new TextSprite(glyph)));
        }
    }

    private boolean isEdge(Position p, Map<Position, Cell> cells) {
        Cell n = cells.get(p.getRelativePosition(Position.BACK));
        Cell e = cells.get(p.getRelativePosition(Position.RIGHT));
        Cell s = cells.get(p.getRelativePosition(Position.FRONT));
        Cell w = cells.get(p.getRelativePosition(Position.LEFT));

        // 1) Классическая edge — есть null среди кардинальных.
        if (n == null || e == null || s == null || w == null) return true;

        // 2) Вогнутый угол — диагональный null при двух смежных cells.
        Cell ne = cells.get(p.getRelativePosition( 1, -1));
        Cell nw = cells.get(p.getRelativePosition(-1, -1));
        Cell se = cells.get(p.getRelativePosition( 1,  1));
        Cell sw = cells.get(p.getRelativePosition(-1,  1));

        if (ne == null) return true;   // N и E точно cells (проверено выше)
        if (nw == null) return true;
        if (se == null) return true;
        if (sw == null) return true;

        return false;
    }


//    private void createShape(Room room, LinkedHashSet<Position> corners) {
//        AssetPool assetPool = AssetPool.get();
//
//        RectangleShape roomShape = RectangleShape.builder()
//                .bottomLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_l")))
//                .bottomRightCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_r")))
//                .topLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_top_l")))
//                .topRightCorner(new TextSprite(assetPool.getAsset("wall_corner_top_r")))
//                .horizontalSprite(new TextSprite(assetPool.getAsset("wall_h")))
//                .verticalSprite(new TextSprite(assetPool.getAsset("wall_v")))
//                .width(room.getWidth() - 1)
//                .height(room.getHeight() - 1)
//                .topLeftPosition(room.getRoomLeftTopPosition())
//                .build();
//
//        LinkedHashSet<Position> tempCorners = new LinkedHashSet<>(corners);
//
//        while (tempCorners.size() > 1) {
//            Position p2 = tempCorners.removeLast();
//            for (Position corner : tempCorners) {
//                if (corner.getX() == p2.getX() && corner.getY() < p2.getY()) {
//                    buildShape(room.getCells(), corner, p2, Position.FRONT, new Wall(roomShape.getVerticalSprite()));
//                }
//
//                if (corner.getX() < p2.getX() && corner.getY() == p2.getY()) {
//                    buildShape(room.getCells(), corner, p2, Position.RIGHT, new Wall(roomShape.getHorizontalSprite()));
//                }
//            }
//        }
//
//        for (Position corner : corners) {
//            Cell cell = room.getCell(corner);
//            if (Objects.isNull(cell)) {
//                continue;
//            }
//            if (!cell.isWall()) {
//                Cell nearCellFirst = room.getCell(corner.getRelativePosition(Position.RIGHT));
//                Cell nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));
//
//                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond) && nearCellFirst.getObject().getTextSprite().getSpriteChar()
//                        == AssetPool.get().getAsset("wall_h")
//                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
//                        == assetPool.getAsset("wall_v")) {
//                    cell.replaceObject(new Wall(roomShape.getTopLeftCorner()));
//                    continue;
//                }
//
//                nearCellFirst = room.getCell(corner.getRelativePosition(Position.RIGHT));
//                nearCellSecond = room.getCell(corner.getRelativePosition(Position.BACK));
//
//                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
//                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
//                        == AssetPool.get().getAsset("wall_h")
//                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
//                        == assetPool.getAsset("wall_v")) {
//                    cell.replaceObject(new Wall(roomShape.getBottomLeftCorner()));
//                    continue;
//                }
//
//                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
//                nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));
//
//                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
//                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
//                        == AssetPool.get().getAsset("wall_h")
//                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
//                        == assetPool.getAsset("wall_v")) {
//                    cell.replaceObject(new Wall(roomShape.getTopRightCorner()));
//                    continue;
//                }
//
//                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
//                nearCellSecond = room.getCell(corner.getRelativePosition(Position.BACK));
//
//                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
//                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
//                        == AssetPool.get().getAsset("wall_h")
//                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
//                        == assetPool.getAsset("wall_v")) {
//                    cell.replaceObject(new Wall(roomShape.getBottomRightCorner()));
//                    continue;
//                }
//
//                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
//                nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));
//
//                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
//                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
//                        == AssetPool.get().getAsset("wall_h")
//                        && nearCellSecond.isEmpty()) {
//                    cell.replaceObject(new Wall(roomShape.getTopRightCorner()));
//                    nearCellSecond.replaceObject(new Wall(roomShape.getBottomLeftCorner()));
//                }
//
//            }
//        }
//    }
//
//    private void buildShape(HashMap<Position, Cell> cells,
//                            Position from,
//                            Position to,
//                            Position direction,
//                            Wall wallShape) {
//        Cell cell = cells.get(from.getRelativePosition(direction));
//
//        if (Objects.isNull(cell)) {
//            return;
//        }
//
//        if (Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.LEFT)))
//                && Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.RIGHT)))
//                && direction.equals(Position.FRONT)) {
//            return;
//        }
//
//        while (!cell.getPosition().equals(to)) {
//            if(!(cell.getObject() instanceof Door)) {
//                cell.replaceObject(wallShape);
//            }
//            cell = cells.get(cell.getPosition().getRelativePosition(direction));
//            if (Objects.isNull(cell)) {
//                break;
//            }
//        }
//    }
}
