package kseoni.ch.roguera.map;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.graphics.sprites.AssetPool;
import kseoni.ch.roguera.graphics.sprites.RectangleShape;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.graphics.ui.MapDrawer;
import lombok.Getter;

import java.util.HashMap;

public class Room {

    @Getter
    private final int roomId;

    @Getter
    private final int width;

    @Getter
    private final int height;

    @Getter
    private HashMap<Position, Cell> cells;

    public Room(int roomId, int width, int height){
        this.roomId = roomId;
        this.width = width;
        this.height = height;
        prepareCells();
        createShape();
        // System.out.println(cells);
    }

    public Cell getCell(Position position){
        return cells.get(position);
    }

    private void prepareCells(){
        this.cells = new HashMap<>();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Position pos = new Position(x,y);
                cells.put(pos, new Cell(pos));
            }
        }
    }

    private void createShape(){
        AssetPool assetPool = AssetPool.get();

        RectangleShape roomShape = RectangleShape.builder()
                .bottomLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_l")))
                .bottomRightCorner(new TextSprite(assetPool.getAsset("wall_corner_bottom_r")))
                .topLeftCorner(new TextSprite(assetPool.getAsset("wall_corner_top_l")))
                .topRightCorner(new TextSprite(assetPool.getAsset("wall_corner_top_r")))
                .horizontalSprite(new TextSprite(assetPool.getAsset("wall_h")))
                .verticalSprite(new TextSprite(assetPool.getAsset("wall_v")))
                .width(width-1)
                .height(height-1)
                .topLeftPosition(Position.ZERO)
                .build();

        Position topLeft = roomShape.getTopLeftPosition();
        Position topRight = new Position(roomShape.getWidth(), 0);
        Position bottomLeft = new Position(0, roomShape.getHeight());
        Position bottomRight = new Position(roomShape.getWidth(), roomShape.getHeight());

        for(int x = topLeft.getX(); x < topRight.getX(); x++){
            Cell cell = cells.get(new Position(x, topRight.getY()));
            cell.replaceObject(new Wall(roomShape.getHorizontalSprite()));
            cell.setWall(true);
        }

        for(int x = bottomLeft.getX(); x < bottomRight.getX(); x++){
            Cell cell = cells.get(new Position(x, bottomLeft.getY()));
            cell.replaceObject(new Wall(roomShape.getHorizontalSprite()));
            cell.setWall(true);
        }

        for(int y = topLeft.getY(); y < bottomLeft.getY(); y++){
            Cell cell = cells.get(new Position(topLeft.getX(), y));
            cell.replaceObject(new Wall(roomShape.getVerticalSprite()));
            cell.setWall(true);
        }

        for(int y = topRight.getY(); y < bottomRight.getY(); y++){
            Cell cell = cells.get(new Position(topRight.getX(), y));
            cell.replaceObject(new Wall(roomShape.getVerticalSprite()));
            cell.setWall(true);
        }
        cells.get(topLeft).replaceObject(new Wall(roomShape.getTopLeftCorner()));
        cells.get(topRight).replaceObject(new Wall(roomShape.getTopRightCorner()));
        cells.get(bottomLeft).replaceObject(new Wall(roomShape.getBottomLeftCorner()));
        cells.get(bottomRight).replaceObject(new Wall(roomShape.getBottomRightCorner()));
    }
}
