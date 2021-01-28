package com.rogurea.main.mapgenerate;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.map.Room;
import com.rogurea.main.gamelogic.Scans;

import java.util.ArrayList;
import java.util.Random;

public class MapEditor {

    static char VWall = GameResources.GetModel("Vwall");

    static char HWall = GameResources.GetModel("Hwall");

    static char RBCorner = GameResources.GetModel("RBCorner");

    static char RTCorner = GameResources.GetModel("RTCorner");

    static char LBCorner = GameResources.GetModel("LBCorner");

    static char LTCorner = GameResources.GetModel("LTCorner");


    enum DrawDirection{
        UP {
            @Override
            public char getCell() {
                return VWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                return RBCorner;
            }
        },
        DOWN{
            @Override
            public char getCell() {
                return VWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                if(IsReverse)
                    return RTCorner;
                else
                    return LBCorner;
            }
        },
        LEFT{
            @Override
            public char getCell() {
                return HWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                if(IsReverse)
                    return RTCorner;
                else
                    return LTCorner;
            }
        },
        RIGHT{
            @Override
            public char getCell() {
                return HWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                if(IsReverse)
                    return LBCorner;
                else
                    return RTCorner;
            }
        };
        public abstract char getCell();
        public abstract char getCorner(boolean IsReverse);
    }

    public static final char EmptyCell = GameResources.EmptyCell;

    private static final Random rnd = new Random();

    public static char[] DrawLine(DrawDirection drawDirection, int Length){

        char[] lineBuffer = new char[Length];

        for(int i = 0;i < Length; i++){
            lineBuffer[i] = drawDirection.getCell();
        }
        return lineBuffer;
    }

     public static char[][] DrawSquare(int square){
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
    }

    public static char[][] DrawRectangle(int h, int w){
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
    }

    static void PlaceCorners(char[][] ShapeBuffer){
        setIntoCell(ShapeBuffer, LTCorner, new Position(0,0));
        setIntoCell(ShapeBuffer, RBCorner, new Position(ShapeBuffer[0].length-1,ShapeBuffer.length-1));
        setIntoCell(ShapeBuffer, RTCorner, new Position(ShapeBuffer[0].length-1,0));
        setIntoCell(ShapeBuffer, LBCorner, new Position(0,ShapeBuffer.length-1));
    }

    public static void FillSpaceWithEmpty(char[][] ShapeBuffer){
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
    }

    static void InsertShapeLine(char[][] CurrentRoom, DrawDirection drawDirection, int Length, int X_a, int Y_a) {

        char[] shape = DrawLine(drawDirection, Length);

        switch (drawDirection){
            case UP -> {
                for(char c : shape){
                    InsertLine(CurrentRoom, c, X_a, Y_a);
                    Y_a--;
                }
            }
            case DOWN -> {
                for(char c : shape){
                    InsertLine(CurrentRoom, c, X_a, Y_a);
                    Y_a++;
                }
            }
            case LEFT -> {
                for(char c : shape){
                    InsertLine(CurrentRoom, c, X_a, Y_a);
                    X_a--;
                }
            }
            case RIGHT -> {
                for(char c : shape){
                    InsertLine(CurrentRoom, c, X_a, Y_a);
                    X_a++;
                }
            }
        }
    }

    static void InsertShapeLine(char[][] CurrentRoom, DrawDirection drawDirection, int Length, Position FromPosition){
        InsertShapeLine(CurrentRoom, drawDirection, Length, FromPosition.x, FromPosition.y);
    }

    static void InsertShapeFlat(char[][] CurrentRoom, char[][] shape, int X_a, int Y_a){

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
    }

    static void InsertShapeFlat(char[][] CurrentRoom, char[][] shape, Position FromPosition) {
        InsertShapeFlat(CurrentRoom, shape, FromPosition.x, FromPosition.y);
    }

    static void InsertLine(char[][] CurrentRoom, char c, int x, int y){
        setIntoCell(CurrentRoom, c, new Position(x,y));
    }

    static void PlaceDoors(Room room, char[][] CurrentRoom, Position ExitPoint){

        int ExitPointX = ExitPoint.x, ExitPointY = ExitPoint.y;

        char cell = CurrentRoom[ExitPointY][ExitPointX];

        char NextDoor = GameResources.GetModel("NextDoor");

        char BackDoor = GameResources.GetModel("BackDoor");

        char ExitDoor = GameResources.GetModel("DungeonExit");

        if(!room.IsEndRoom) {
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
        if(room.NumberOfRoom > 1)
            CurrentRoom[0][CurrentRoom[0].length/2] = BackDoor;
        if(room.NumberOfRoom == (Dungeon.CurrentDungeonLenght-Dungeon.DungeonLenght)+1)
            CurrentRoom[0][CurrentRoom[0].length/2] = GameResources.SWall;
    }


    private static int FindWall(char[][] CurrentRoom, Position ExitPoint){
        int x = ExitPoint.x, y = ExitPoint.y;

        char cell = CurrentRoom[y][x];

        int l = (cell == RBCorner || cell == RTCorner ? -1 : 1);

        int x_shift = 0;

        while(!Scans.CheckWall(cell) && !Scans.CheckCorner(cell)){
            x_shift++;
            cell = CurrentRoom[y][x+(x_shift*=l)];
        }

        return x_shift;
    }

    public static void setIntoCell(char cell, int y, int x){
        Dungeon.CurrentRoom[y][x] = cell;
    }

    public static void setIntoCell(char cell, Position position){
        setIntoCell(cell, position.y, position.x);
    }

    public static void setIntoCell(char[][] CurrentRoom, char cell, Position position){
        try {
            CurrentRoom[position.y][position.x] = cell;
        } catch (ArrayIndexOutOfBoundsException e){
            Debug.log("ERROR: generation was failed, index " +position.toString()+" is outer of bounds");
        }
    }

    public static char getFromCell(int y, int x){
        return Dungeon.CurrentRoom[y][x];
    }

    public static char getFromCell(Position position){
        return getFromCell(position.y, position.x);
    }

    public static void clearCell(int y, int x){
        Dungeon.CurrentRoom[y][x] = MapEditor.EmptyCell;
    }

    public static void clearCell(Position position){
        clearCell(position.y, position.x);
    }

    static void PlaceProp(char prop, char[][] map, int y, int x){
            map[y][x] = prop;
    }

    static void PlaceFurniture(char[][] CurrentRoom, char[][] FurnitureMap){
        for(int y = 0; y < FurnitureMap.length; y++){
            System.arraycopy(FurnitureMap[y], 0, CurrentRoom[3 + y], 4, FurnitureMap[0].length);
        }
    }

    static int CheckSize(BaseGenerate.RoomSize roomSize) {
        switch (roomSize) {
            case MIDDLE -> {
                return 6;
            }
            case BIG -> {
                return 16;
            }
        }
        return 0;
    }

    static boolean OutOfBounds(int XY, int OFX, int OFY, int CRX, int CRY, int SX, int SY){
        return (XY + Math.max(OFX, SX) + SX >= Math.min(CRX-1, CRY)
             || XY + Math.max(OFY, SY) + SY >= Math.min(CRX, CRY-1));
    }

    static int DoRandomXY(int x, int y, Random random){
        return random.nextInt(
                Math.min(x, y)
        );
    }

    static void PlaceMobs(Room room, char[][] CurrentRoom, ArrayList<Position> SpawnPositions){

        for(Mob mob : room.RoomCreatures){

            Position SpawnPosition = SpawnPositions.get(rnd.nextInt(SpawnPositions.size()));

            int y = SpawnPosition.y, x = SpawnPosition.x;

            if(!Scans.CheckCreature(CurrentRoom[y][x])) {
                if(Scans.CheckWall(CurrentRoom[y][x])) {
                    CurrentRoom[y][x] = mob.getCreatureSymbol();
                    mob.setMobPosition(y, x);
                }
            }
        }
    }
}