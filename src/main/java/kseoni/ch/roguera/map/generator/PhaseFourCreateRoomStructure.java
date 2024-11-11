package kseoni.ch.roguera.map.generator;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.map.Wall;

import java.util.*;
import java.util.stream.Collectors;

public class PhaseFourCreateRoomStructure {
    private Map<Integer, Room> rooms;

    public PhaseFourCreateRoomStructure(Map<Integer, Room> rooms) {
        this.rooms = rooms;
    }

    public void createRoomStructure() {
        System.out.println("====Create Room Structure====");
        for (Room room : rooms.values()) {
            LinkedHashSet<Position> corners = findCorners(room);
            createShape(room, corners);
        }
    }


    private LinkedHashSet<Position> findCorners(Room room) {
        Map<Position, Cell> roomCells = room.getCells();

        LinkedHashSet<Position> corners = roomCells.values()
                .stream()
                .filter(
                        cell -> Arrays.stream(cell.getCellsAround(room)).filter(Objects::isNull).count() >= 4
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

        while (tempCorners.size() > 1) {
            Position p2 = tempCorners.removeLast();
            for (Position corner : tempCorners) {
                if (corner.getX() == p2.getX() && corner.getY() < p2.getY()) {
                    buildShape(room.getCells(), corner, p2, Position.FRONT, new Wall(roomShape.getVerticalSprite()));
                }

                if (corner.getX() < p2.getX() && corner.getY() == p2.getY()) {
                    buildShape(room.getCells(), corner, p2, Position.RIGHT, new Wall(roomShape.getHorizontalSprite()));
                }
            }
        }

        for (Position corner : corners) {
            Cell cell = room.getCell(corner);
            if (Objects.isNull(cell)) {
                continue;
            }
            if (!cell.isWall()) {
                Cell nearCellFirst = room.getCell(corner.getRelativePosition(Position.RIGHT));
                Cell nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));

                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond) && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")) {
                    cell.replaceObject(new Wall(roomShape.getTopLeftCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.RIGHT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.BACK));

                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")) {
                    cell.replaceObject(new Wall(roomShape.getBottomLeftCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));

                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")) {
                    cell.replaceObject(new Wall(roomShape.getTopRightCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.BACK));

                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.getObject().getTextSprite().getSpriteChar()
                        == assetPool.getAsset("wall_v")) {
                    cell.replaceObject(new Wall(roomShape.getBottomRightCorner()));
                    continue;
                }

                nearCellFirst = room.getCell(corner.getRelativePosition(Position.LEFT));
                nearCellSecond = room.getCell(corner.getRelativePosition(Position.FRONT));

                if (Objects.nonNull(nearCellFirst) && Objects.nonNull(nearCellSecond)
                        && nearCellFirst.getObject().getTextSprite().getSpriteChar()
                        == AssetPool.get().getAsset("wall_h")
                        && nearCellSecond.isEmpty()) {
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
                            Wall wallShape) {
        Cell cell = cells.get(from.getRelativePosition(direction));

        if (Objects.isNull(cell)) {
            return;
        }

        if (Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.LEFT)))
                && Objects.nonNull(cells.get(cell.getPosition().getRelativePosition(Position.RIGHT)))
                && direction.equals(Position.FRONT)) {
            return;
        }

        while (!cell.getPosition().equals(to)) {
            if(!(cell.getObject() instanceof Door)) {
                cell.replaceObject(wallShape);
            }
            cell = cells.get(cell.getPosition().getRelativePosition(direction));
            if (Objects.isNull(cell)) {
                break;
            }
        }
    }
}
