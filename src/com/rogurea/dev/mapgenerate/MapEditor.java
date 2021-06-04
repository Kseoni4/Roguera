package com.rogurea.dev.mapgenerate;

import com.rogurea.dev.base.Entity;
import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.gamemap.Border;
import com.rogurea.dev.gamemap.Cell;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.dev.gamemap.Scan;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.gamemap.Position;
import com.rogurea.dev.gamemap.Room;
import com.rogurea.dev.resources.GameResources;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MapEditor {

    private static Room currentRoomForEdit;

    public static void SetRoomForEdit(Room room){
        currentRoomForEdit = room;
    }

    enum DrawDirection{
        UP {
            @Override
            public GameObject getCell() {
                return BorderObjects.VWall;
            }

            @Override
            public GameObject getCorner(boolean IsReverse) {
                return BorderObjects.RBCorner;
            }
        },
        DOWN{
            @Override
            public GameObject getCell() {
                return BorderObjects.VWall;
            }

            @Override
            public GameObject getCorner(boolean IsReverse) {
                if(IsReverse)
                    return BorderObjects.RTCorner;
                else
                    return BorderObjects.LBCorner;
            }
        },
        LEFT{
            @Override
            public GameObject getCell() {
                return BorderObjects.HWall;
            }

            @Override
            public GameObject getCorner(boolean IsReverse) {
                if(IsReverse)
                    return BorderObjects.RTCorner;
                else
                    return BorderObjects.LTCorner;
            }
        },
        RIGHT{
            @Override
            public GameObject getCell() {
                return BorderObjects.HWall;
            }

            @Override
            public GameObject getCorner(boolean IsReverse) {
                if(IsReverse)
                    return BorderObjects.LBCorner;
                else
                    return BorderObjects.RTCorner;
            }
        };
        public abstract GameObject getCell();
        public abstract GameObject getCorner(boolean IsReverse);
    }

    public static final char EmptyCell = GameResources.EmptyCell;

    private static final Random rnd = new Random();

    public static GameObject[] DrawLine(DrawDirection drawDirection, int Length){

        GameObject[] lineBuffer = new GameObject[Length];

        for(int i = 0;i < Length; i++){
            lineBuffer[i] = drawDirection.getCell();
        }
        return lineBuffer;
    }

     /*public static char[][] DrawSquare(int square){
        char[][] squareBuffer = new char[square][square];

        int y = 0;
        int x = 0;

        FillSpaceWithEmpty(squareBuffer);

        char[] lineBuffer = DrawLine(DrawDirection.UP, square);

        for(char c : lineBuffer){
            squareBuffer[y][0] = c;
            squareBuffer[y][square-1] = c;
            y++;
        }

        lineBuffer = DrawLine(DrawDirection.RIGHT, square);

        for(char c : lineBuffer){
            squareBuffer[0][x] = c;
            squareBuffer[square-1][x] = c;
            x++;
        }

        PlaceCorners(squareBuffer);

        return squareBuffer;
    }*/

    /*public static char[][] DrawRectangle(int h, int w){
        char[][] RectagnleBuffer = new char[h][w];

        int y = 0, x = 0;

        FillSpaceWithEmpty(RectagnleBuffer);

        char[] lineBuffer = DrawLine(DrawDirection.UP, h);

        for(char c : lineBuffer){
            RectagnleBuffer[y][0] = c;
            RectagnleBuffer[y][w-1] = c;
            y++;
        }

        lineBuffer = DrawLine(DrawDirection.RIGHT, w);

        for(char c : lineBuffer){
            RectagnleBuffer[0][x] = c;
            RectagnleBuffer[h-1][x] = c;
            x++;
        }

        PlaceCorners(RectagnleBuffer);

        return RectagnleBuffer;
    }*/

    /*static void PlaceCorners(char[][] ShapeBuffer){
        setIntoCell(ShapeBuffer, LTCorner, new Position(0,0));
        setIntoCell(ShapeBuffer, RBCorner, new Position(ShapeBuffer[0].length-1,ShapeBuffer.length-1));
        setIntoCell(ShapeBuffer, RTCorner, new Position(ShapeBuffer[0].length-1,0));
        setIntoCell(ShapeBuffer, LBCorner, new Position(0,ShapeBuffer.length-1));
    }*/

    /*public static void FillSpaceWithEmpty(char[][] ShapeBuffer){
        for (int i = 0; i < ShapeBuffer.length; i++) {
            for (int j = 0; j < ShapeBuffer[0].length; j++) {
                if(Scans.CheckWall(ShapeBuffer[i][j]))
                    setIntoCell(ShapeBuffer, EmptyCell, new Position(j,i));
            }
        }
    }

    public static void FillAllSpaceWithEmpty(char[][] ShapeBuffer){
        for (int i = 0; i < ShapeBuffer.length; i++) {
            for (int j = 0; j < ShapeBuffer[0].length; j++) {
                setIntoCell(ShapeBuffer, EmptyCell, new Position(j,i));
            }
        }
    }*/

    static void InsertShapeLine(ArrayList<Cell> CurrentRoomCells, DrawDirection drawDirection, int Length, int X_a, int Y_a) {

        GameObject[] shape = DrawLine(drawDirection, Length);

        switch (drawDirection){
            case UP: {
                for(GameObject gObj : shape){
                    InsertLine(CurrentRoomCells, gObj, X_a, Y_a);
                    Y_a--;
                }
                break;
            }
            case DOWN: {
                for(GameObject gObj : shape){
                    InsertLine(CurrentRoomCells, gObj, X_a, Y_a);
                    Y_a++;
                }
                break;
            }
            case LEFT: {
                for(GameObject gObj : shape){
                    InsertLine(CurrentRoomCells, gObj, X_a, Y_a);
                    X_a--;
                }
                break;
            }
            case RIGHT: {
                for(GameObject gObj : shape){
                    InsertLine(CurrentRoomCells, gObj, X_a, Y_a);
                    X_a++;
                }
                break;
            }
        }
    }

    static void InsertShapeLine(ArrayList<Cell> CurrentRoomCells, DrawDirection drawDirection, int Length, Position FromPosition){
        InsertShapeLine(CurrentRoomCells, drawDirection, Length, FromPosition.x, FromPosition.y);
    }

    /*static void InsertShapeFlat(char[][] CurrentRoom, char[][] shape, int X_a, int Y_a){

        int Y_b = shape.length;

        int X_b = shape[0].length;

        int i, j;

        for(i = 0;i < Y_b; i++){
            for(j = 0;j < X_b; j++){

                if(!Scans.CheckWall(CurrentRoom[Y_a+i][X_a+j]))
                    continue;
                CurrentRoom[Y_a+i][X_a+j] = shape[i][j];
            }
        }
    }*/

/*    static void InsertShapeFlat(char[][] CurrentRoom, char[][] shape, Position FromPosition) {
        InsertShapeFlat(CurrentRoom, shape, FromPosition.x, FromPosition.y);
    }*/

    static void InsertLine(ArrayList<Cell> CurrentRoomCells, GameObject gObj, int x, int y){
        setIntoCell(CurrentRoomCells, gObj, new Position(x,y));
    }

    static void PlaceDoors(Room room, ArrayList<Cell> CurrentRoomCells, Position ExitPoint){

        Cell cell = CurrentRoomCells.stream().filter(cell1 -> cell1.position.equals(ExitPoint)).findFirst().orElse(new Cell(new Position(1,0)));

        cell.unsetWall();

/*        Entity NextDoor = new Entity(new Model("next_door", Colors.ORANGE, GameResources.GetModel("NextDoor")), ()->{
            Dungeon.ChangeRoom(room.);
        })

        char BackDoor = GameResources.GetModel("BackDoor");*/

        //char ExitDoor = GameResources.GetModel("DungeonExit");

        if(!room.isEndRoom) {
            if (!Scan.CheckWall(cell) && !Scan.CheckCorner(cell)) {
                setIntoCell(room, cell, ExitPoint);
                setIntoCell(room.getNextDoor(), ExitPoint);
            }
            else{
                int x_shift = FindWall(room.getCell(ExitPoint));
                currentRoomForEdit.getCell(ExitPoint.getRelative(x_shift,0)).unsetWall();
                setIntoCell(room.getNextDoor(), ExitPoint.getRelative(x_shift,0));
            }
        }
        /*else{
            if (Scans.CheckCorner(cell))
                setIntoCell(CurrentRoom, ExitDoor, ExitPoint);
            else{
                int x_shift = FindWall(CurrentRoom, ExitPoint);
                setIntoCell(CurrentRoom, ExitDoor, ExitPoint.getRelative(x_shift,0));
            }
        }*/
        if(room.RoomNumber > 1) {
            setIntoCell(room.getCells(), room.getBackDoor(), room.getTopCenterCellPosition());
            room.getCell(room.getTopCenterCellPosition()).unsetWall();
        }
            //CurrentRoom[0][CurrentRoom[0].length/2] = BackDoor;
        /*if(room.RoomNumber == (Dungeon.DungeonRoomsCount-Dungeon.DungeonRooms)+1)
            CurrentRoom[0][CurrentRoom[0].length/2] = GameResources.GetModel("SWall");*/
    }

    private static int FindWall(Cell cell){

        int x_shift = 1;

        if(Scan.CheckCorner(cell)){

            int shift = GetShiftDirection(cell);

            Position shiftedPos = cell.position.getRelative(x_shift*=shift,0);

            Cell nextCell = currentRoomForEdit.getCell(shiftedPos);
            while(!Scan.CheckWall(nextCell) && Scan.CheckCorner(nextCell)){
                shift = GetShiftDirection(cell);
                nextCell = currentRoomForEdit.getCell(shiftedPos.getRelative(x_shift*=shift, 0));
                x_shift++;
            }
            return x_shift;
        }
        return 0;
    }

    private static int GetShiftDirection(Cell cell){
        return Scan.CheckCorner(cell, "RB") || Scan.CheckCorner(cell, "RT") ? -1 : 1;
    }


    /*static void PlaceDoors(Room_old roomOld, char[][] CurrentRoom, Position ExitPoint){

        int ExitPointX = ExitPoint.x, ExitPointY = ExitPoint.y;

        char cell = CurrentRoom[ExitPointY][ExitPointX];

        char NextDoor = GameResources.GetModel("NextDoor");

        char BackDoor = GameResources.GetModel("BackDoor");

        char ExitDoor = GameResources.GetModel("DungeonExit");

        if(!roomOld.IsEndRoom) {
            if (Scans.CheckCorner(cell))
                setIntoCell(CurrentRoom, NextDoor, ExitPoint);
            else{
                int x_shift = FindWall(CurrentRoom, ExitPoint);
                setIntoCell(CurrentRoom, NextDoor, ExitPoint.getRelative(x_shift,0));
            }
        }
        else{
            if (Scans.CheckCorner(cell))
                setIntoCell(CurrentRoom, ExitDoor, ExitPoint);
            else{
                int x_shift = FindWall(CurrentRoom, ExitPoint);
                setIntoCell(CurrentRoom, ExitDoor, ExitPoint.getRelative(x_shift,0));
            }
        }
        if(roomOld.NumberOfRoom > 1)
            CurrentRoom[0][CurrentRoom[0].length/2] = BackDoor;
        if(roomOld.NumberOfRoom == (Dungeon.CurrentDungeonLenght-Dungeon.DungeonLenght)+1)
            CurrentRoom[0][CurrentRoom[0].length/2] = GameResources.GetModel("SWall");
    }*/


    /*private static int FindWall(char[][] CurrentRoom, Position ExitPoint){
        int x = ExitPoint.x, y = ExitPoint.y;

        char cell = CurrentRoom[y][x];

        int l = (cell == RBCorner || cell == RTCorner ? -1 : 1);

        int x_shift = 0;

        while(!Scans.CheckWall(cell) && !Scans.CheckCorner(cell)){
            x_shift++;
            cell = CurrentRoom[y][x+(x_shift*=l)];
        }

        return x_shift;
    }*/

    public static void setIntoCell(GameObject gameObject, int y, int x){
        currentRoomForEdit.getCell(x,y).putIntoCell(gameObject);
    }

    public static void setIntoCell(GameObject gameObject, Position position){
        setIntoCell(gameObject, position.y, position.x);
    }

    public static void setIntoCell(Room CurrentRoom, Cell cell, Position position){
        try {
            CurrentRoom.replaceCell(cell, position);
        } catch (ArrayIndexOutOfBoundsException e){
            Debug.log("ERROR: generation was failed, index " +position.toString()+" is outer of bounds");
        }
    }

    public static void setIntoCell(ArrayList<Cell> bufferCells, GameObject gameObject, Position position){
        Objects.requireNonNull(
                bufferCells.stream()
                            .filter(cell -> cell.position.equals(position))
                            .findFirst()
                            .orElse(null))
                .putIntoCell(gameObject);
    }

    /*public static char getFromCell(int y, int x){
        return Dungeon.CurrentRoom[y][x];
    }

    public static char getFromCell(Position position){
        return getFromCell(position.y, position.x);
    }*/

   /* public static void clearCell(int y, int x){
        Dungeon.CurrentRoom[y][x] = MapEditor.EmptyCell;
    }

    public static void clearCell(Position position){
        clearCell(position.y, position.x);
    }

    static void PlaceProp(char prop, char[][] map, int y, int x){
            map[y][x] = prop;
    }*/

    /*static void PlaceFurniture(char[][] CurrentRoom, char[][] FurnitureMap){
        for(int y = 0; y < FurnitureMap.length; y++){
            System.arraycopy(FurnitureMap[y], 0, CurrentRoom[3 + y], 4, FurnitureMap[0].length);
        }
    }*/

    static int CheckSize(RoomGenerate.RoomSize roomSize) {
        switch (roomSize) {
            case MIDDLE: {
                return 6;
            }
            case BIG: {
                return 16;
            }
        }
        return 0;
    }

    /*static boolean OutOfBounds(int XY, int OFX, int OFY, int CRX, int CRY, int SX, int SY){
        return (XY + Math.max(OFX, SX) + SX >= Math.min(CRX-1, CRY)
             || XY + Math.max(OFY, SY) + SY >= Math.min(CRX, CRY-1));
    }

    static int DoRandomXY(int x, int y, Random random){
        return random.nextInt(
                Math.min(x, y)
        );
    }*/

    /*static void PlaceMobs(Room_old roomOld, char[][] CurrentRoom, ArrayList<Position> SpawnPositions){

        for(Mob mob : roomOld.RoomCreatures){

            boolean NotPlaced = true;

            while(NotPlaced) {
                Position SpawnPosition = SpawnPositions.get(rnd.nextInt(SpawnPositions.size()));

                int y = SpawnPosition.y, x = SpawnPosition.x;

                if (!Scans.CheckCreature(CurrentRoom[y][x])) {
                    if (Scans.CheckWall(CurrentRoom[y][x])) {
                        CurrentRoom[y][x] = mob.getCreatureSymbol();
                        mob.setMobPosition(y, x);
                        NotPlaced = false;
                    }
                }
            }
        }
    }*/

    /*public static GameObject getBorderObject(String name){
        try {
            return (GameObject) BorderObjects.class.getField(name).get(GameObject.class);
        } catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
            return null;
        }

    }*/

    public static class BorderObjects{
        static GameObject VWall = new Border(new Model("VWall", Border.color, GameResources.GetModel("Vwall")));

        static GameObject HWall = new Border(new Model("HWall", Border.color, GameResources.GetModel("Hwall")));

        static GameObject RBCorner = new Border(new Model("RBCorner", Border.color, GameResources.GetModel("RBCorner")));

        static GameObject RTCorner = new Border(new Model("RTCorner", Border.color, GameResources.GetModel("RTCorner")));

        static GameObject LBCorner = new Border(new Model("LBCorner", Border.color, GameResources.GetModel("LBCorner")));

        static GameObject LTCorner = new Border(new Model("LTCorner", Border.color, GameResources.GetModel("LTCorner")));
    }
}