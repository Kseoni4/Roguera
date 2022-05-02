package com.rogurea.mapgenerate;

import com.rogurea.base.Debug;
import com.rogurea.base.GameObject;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.EditorEntity;
import com.rogurea.gamemap.Position;
import com.rogurea.gamemap.Room;
import com.rogurea.input.Input;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;
import com.rogurea.view.ViewObjects;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static com.rogurea.mapgenerate.MapEditor.*;

/**
 * Попытка переписать алгоритм генерации структуры комнаты по точкам
 * В этот раз каждая точка связана с другой, как эдакая нода в LinkedList
 * Разработка оставлена до вероятной версии игры 0.3.1.
 */
public class PGPv2 {

    private Point firstPoint = new Point(new Position());

    private Point nextPoint = new Point(new Position()).prevPoint = firstPoint;

    private final EditorEntity Dot = new EditorEntity(new Model("dot", Colors.ORANGE, '.'));

    private final ArrayList<Point> linkedPoints = new ArrayList<>();

    ArrayList<Cell> roomCells = new ArrayList<>();

    private int boundX;

    private int boundY;

    private boolean isUTD = true;

    public PGPv2(Room room){
        roomCells = room.getCells();
    }

    public PGPv2(){
        Room testRoom = new Room(0,20,20, RoomGenerate.RoomSize.MIDDLE);
        roomCells = testRoom.getCells();
        boundX = testRoom.width;
        boundY = testRoom.height;
        SetRoomForEdit(testRoom);
        ViewObjects.mapView.setRoom(testRoom);
    }

    public void generateRoomStructure(){

        Draw.clear();

        System.out.println();
        System.out.println();
        System.out.println();

        Debug.toLog("[PGPv2]=== NEW GENERATE ===");
        Debug.toLog("[PGPv2] Bound X: "+boundX+" Bound Y: "+boundY);

        placeFirstPoints();

        upToDown();

        isUTD = false;

        downToUp();

        firstPoint.nextPoint = linkedPoints.get(0);

        Debug.toLog("[PGPv2] === LIST OF LINKED POINTS ===");

        linkedPoints.forEach(point -> Debug.toLog("[PGPv2][Point] "+point.position  + " --> nextPoint --> " + (point.nextPoint != null ? point.nextPoint.position : "none")));

        Debug.toLog("[PGPv2] === CONNECTING POINTS ===");

        connectPoints();


    }

    private void placeFirstPoints(){
        Debug.toLog("[PGPv2] === PLACE FIRST POINTS === ");

        placeDotIntoPoint(firstPoint);

        firstPoint.directionFromPrevPoint = getRandomDirection();

        linkedPoints.add(firstPoint);
    }

    private void upToDown(){
        Debug.toLog("[PGPv2] === UP TO DOWN ===");
        do {
            firstPoint = placePoint();
            Debug.toLog("[PGPv2][UTD] new first point: "+firstPoint.position);
        } while (firstPoint.position.y < boundY-3);
    }

    private void downToUp(){
        Debug.toLog("[PGPv2] === DOWN TO UP ===");
        do {
            firstPoint = placePoint();
            Debug.toLog("[PGPv2][DTU] new first point: "+firstPoint.position);
        } while (firstPoint.position.y > 2 && firstPoint.position.x > 1);
    }

    private Point placePoint(){
        nextPoint = getNextPointByRandom();

        nextPoint.prevPoint = firstPoint;

        firstPoint.nextPoint = nextPoint;

        linkedPoints.add(nextPoint);

        placeDotIntoPoint(nextPoint);

        Debug.toLog("[PGPv2][Lenght] get length between firstPoint " + firstPoint.position + " and nextPoint " + nextPoint.position + " = " + getLenghtBetweenPoints(nextPoint));

        return nextPoint;
    }

    private Point getNextPointByRandom(){

        int x0 = firstPoint.position.x;

        int y0 = firstPoint.position.y;

        Point tempPoint = new Point();

        do {
            if(!isOutOfBounds(tempPoint.position) && (tempPoint.position.y >= boundY-2 || tempPoint.position.x >= boundX-3)){
                break;
            }

            tempPoint.position = new Position(x0,y0);

            tempPoint.directionFromPrevPoint = getRandomDirection();

            Debug.toLog("[PGPv2][getNPBR] get direction: " + tempPoint.directionFromPrevPoint.name());

            tempPoint.position = tempPoint.position.getRelative(getPositionByDirection(tempPoint.directionFromPrevPoint));

            Debug.toLog("[PGPv2][getNPBR] get position: " + tempPoint.position);

        } while (pointIsNotPassRules(tempPoint));

        tempPoint.objectOnPoint = tempPoint.directionFromPrevPoint.getCell();

        return tempPoint;
    }

    private DrawDirection getRandomDirection(){
        return DrawDirection.values()[ThreadLocalRandom.current().nextInt(DrawDirection.values().length)];
    }

    private Position getPositionByDirection(DrawDirection drawDirection){

        int offset;
        int x0 = firstPoint.position.x;

        int y0 = firstPoint.position.y;

        int minX, minY;

        minX = minY = 3;

        int maxX, maxY;

        maxX = 5;
        maxY = 5;

        Debug.toLog("[PGPv2][GetPosByDir] firstPoint: "+firstPoint.position);

        switch (drawDirection){
            case DOWN: {
                if(isUTD) {
                    offset = ThreadLocalRandom.current().nextInt(minY, maxY);
                    return new Position(0, offset);
                } break;
            }
            case RIGHT: {
                    offset = ThreadLocalRandom.current().nextInt(minX, maxX);
                    return new Position(offset, 0);

            }
            case LEFT: {
                if(!isUTD) {
                    offset = ThreadLocalRandom.current().nextInt(minX, maxX);
                    return new Position(-offset, 0);
                } break;
            }
            case UP: {
                if(!isUTD) {
                    offset = ThreadLocalRandom.current().nextInt(minY, maxY);
                    return new Position(0, -offset);
                }break;
            }
        }
        return new Position(0,0);
    }

    private boolean pointIsNotPassRules(Point point){
        return isOutOfBounds(point.position) || isSamePoint(point.position) || isPassHalfOfLenght(point.position);
    }

    private boolean isOutOfBounds(Position position){
        return this.roomCells.stream().noneMatch(cell -> cell.position.equals(position));
    }

    private boolean isSamePoint(Position position){
        return this.linkedPoints.stream().anyMatch(point -> point.position.equals(position));
    }

    private boolean isPassHalfOfLenght(Position position){
        return !isUTD && position.x < boundX/2;
    }

    private void placeDotIntoPoint(Point point){
        setIntoCell(Dot, point.position);
    }

    private void connectPoints(){
        linkedPoints.forEach(point ->{ InsertShapeLine(this.roomCells, point.directionFromPrevPoint, getLenghtBetweenPoints(point), (point.prevPoint != null ? point.prevPoint.position : new Position()));
            Input.waitForInput();
            Draw.call(ViewObjects.mapView);
        });
    }

    private void placeCorner(Point point) {
        if (point.prevPoint == null || point.nextPoint == null) {
            return;
        }

        if (point.directionFromPrevPoint.equals(point.prevPoint.directionFromPrevPoint) && point.directionFromPrevPoint.equals(point.nextPoint.directionFromPrevPoint)) {
            return;
        }
        boolean isReverse = false;

        if (!point.directionFromPrevPoint.equals(point.nextPoint.directionFromPrevPoint)) {
            isReverse = point.directionFromPrevPoint.equals(DrawDirection.LEFT);
            setIntoCell(point.directionFromPrevPoint.getCorner(isReverse), point.position);
        } else if(point.prevPoint.objectOnPoint == BorderObjects.HWall && point.nextPoint.objectOnPoint == BorderObjects.HWall){
            if(point.objectOnPoint == BorderObjects.VWall){
                setIntoCell(BorderObjects.BCenter, point.position);
            }
        }
    }




    private int getLenghtBetweenPoints(Point point){
        Point pPrev = point.prevPoint;
        Point pNext = point.nextPoint;

        int lenghtX = (pPrev != null ? Math.abs(point.position.x - pPrev.position.x) : 0);
        int lenghtY = (pPrev != null ? Math.abs(point.position.y - pPrev.position.y) : 0);

        if(lenghtX > 0 && lenghtY == 0){
            return lenghtX;
        } else {
            return lenghtY;
        }
    }

   private class Point {
        Position position = new Position();

        Point prevPoint = null;

        Point nextPoint = null;

        DrawDirection directionFromPrevPoint;

        GameObject objectOnPoint;

        public Point(Position pointPosition){
            this.position = pointPosition;
        }

        public Point(){}
    }
}

