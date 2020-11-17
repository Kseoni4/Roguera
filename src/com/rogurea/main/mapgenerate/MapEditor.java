package com.rogurea.main.mapgenerate;

import com.googlecode.lanterna.Symbols;
import com.rogurea.main.creatures.Mob;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.map.Room;
import com.rogurea.main.gamelogic.Scans;

import java.util.Random;

public class MapEditor {

    enum DrawDirection{
        UP {
            @Override
            public char getCell() {
                return GameResources.VWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                return Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER;
            }
        },
        DOWN{
            @Override
            public char getCell() {
                return GameResources.VWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                if(IsReverse)
                    return Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
                else
                    return Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER;
            }
        },
        LEFT{
            @Override
            public char getCell() {
                return GameResources.HWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                if(IsReverse)
                    return Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
                else
                    return Symbols.DOUBLE_LINE_TOP_LEFT_CORNER;
            }
        },
        RIGHT{
            @Override
            public char getCell() {
                return GameResources.HWall;
            }

            @Override
            public char getCorner(boolean IsReverse) {
                if(IsReverse)
                    return Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER;
                else
                    return Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
            }
        },
        SAME{
            @Override
            public char getCell() {
                return PointGenerateProcedure.previousDirection.getCell();
            }

            @Override
            public char getCorner(boolean IsReverse) {
                return PointGenerateProcedure.previousDirection.getCorner(IsReverse);
            }
        };
        public abstract char getCell();
        public abstract char getCorner(boolean IsReverse);
    }

    public static final char EmptyCell = GameResources.EmptyCell;

    private static char[] LineBuffer;

    private static final Random rnd = new Random();

    public static char[] DrawLine(DrawDirection drawDirection, int Length){

        LineBuffer = new char[Length];

        for(int i = 0;i < Length; i++){
            LineBuffer[i] = drawDirection.getCell();
        }
        return LineBuffer;
    }

    public static char[][] DrawSquare(int square){
        char[][] squareBuffer = new char[square][square];

        int y = 0;
        int x = 0;

        FillSpaceWithEmpty(squareBuffer);

        DrawLine(DrawDirection.UP, square);

        for(char c : LineBuffer){
            squareBuffer[y][0] = c;
            squareBuffer[y][square-1] = c;
            y++;
        }

        DrawLine(DrawDirection.RIGHT, square);

        for(char c : LineBuffer){
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

        DrawLine(DrawDirection.UP, h);

        for(char c : LineBuffer){
            RectagnleBuffer[y][0] = c;
            RectagnleBuffer[y][w-1] = c;
            y++;
        }

        DrawLine(DrawDirection.RIGHT, w);

        for(char c : LineBuffer){
            RectagnleBuffer[0][x] = c;
            RectagnleBuffer[h-1][x] = c;
            x++;
        }

        PlaceCorners(RectagnleBuffer);

        return RectagnleBuffer;
    }

    static void PlaceCorners(char[][] ShapeBuffer){
        ShapeBuffer[0][0] = Symbols.DOUBLE_LINE_TOP_LEFT_CORNER;
        ShapeBuffer[ShapeBuffer.length-1][ShapeBuffer[0].length-1] = Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER;
        ShapeBuffer[0][ShapeBuffer[0].length-1] = Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
        ShapeBuffer[ShapeBuffer.length-1][0] = Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER;
    }

    public static void FillSpaceWithEmpty(char[][] ShapeBuffer){
        for (int i = 0; i < ShapeBuffer.length; i++) {
            for (int j = 0; j < ShapeBuffer[0].length; j++) {
                if(Scans.CheckWall(ShapeBuffer[i][j]))
                ShapeBuffer[i][j] = EmptyCell;
            }
        }
    }

    public static void FillAllSpaceWithEmpty(char[][] ShapeBuffer){
        for (int i = 0; i < ShapeBuffer.length; i++) {
            for (int j = 0; j < ShapeBuffer[0].length; j++) {
                    ShapeBuffer[i][j] = EmptyCell;
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
            case SAME -> InsertShapeLine(CurrentRoom, PointGenerateProcedure.previousDirection, Length, X_a, Y_a);
        }
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

    static void InsertLine(char[][] CurrentRoom, char c, int x, int y){
        CurrentRoom[y][x] = c;
    }

    static void PlaceDoors(Room room, char[][] CurrentRoom){

        char cell = CurrentRoom[PointGenerateProcedure.ye][PointGenerateProcedure.xe];

        char NextDoor = GameResources.GetModel("NextDoor");
        char BackDoor = GameResources.GetModel("BackDoor");

        if(!room.IsEndRoom) {
            if (Scans.CheckCorner(cell))
                CurrentRoom[PointGenerateProcedure.ye][PointGenerateProcedure.xe] = NextDoor;
            else{
                int x_shift = FindWall(CurrentRoom);
                CurrentRoom[PointGenerateProcedure.ye][PointGenerateProcedure.xe+x_shift] = NextDoor;
            }
        }
        if(room.NumberOfRoom > 1)
            CurrentRoom[0][CurrentRoom[0].length/2] = BackDoor;
    }

    private static int FindWall(char[][] CurrentRoom){
        char cell = CurrentRoom[PointGenerateProcedure.ye][PointGenerateProcedure.xe];

        int l = (cell == GameResources.RBCorner || cell == GameResources.RTCorner ? -1 : 1);

        int x_shift = 0;

        while(!Scans.CheckWall(cell) && !Scans.CheckCorner(cell)){
            x_shift++;
            cell = CurrentRoom[PointGenerateProcedure.ye][PointGenerateProcedure.xe+(x_shift*=l)];
        }

        return x_shift;
    }

    public static void setIntoCell(char cell, int y, int x){
        Dungeon.CurrentRoom[y][x] = cell;
    }

    public static void setIntoCell(char cell, Position position){
        setIntoCell(cell, position.y, position.x);
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

    static void PlaceSubShapes(BaseGenerate.RoomSize roomSize, char[][] CurrentRoom){

        Random random = new Random();

        int offsetX = 0;

        int offsetY = 0;

        int RandomXY = 2;

        System.out.println("Room size: " + roomSize);

        for(int i = 0; i < CheckSize(roomSize); i++) {

            int sq = random.nextInt(2)+2;

            char[][] shape = DrawSquare(sq);

            System.out.println("Square: " + sq);

            System.out.println(
                    "i: " + i + "\n" +
                            "\t offsetX: " + offsetX
                            + " offsetY: " + offsetY

            );

            System.out.println("SumOFX: " + (RandomXY + Math.max(offsetX,shape[0].length)));
            System.out.println("SumOFY: " + (RandomXY + Math.max(offsetY,shape.length)));

            if(OutOfBounds(RandomXY, offsetX, offsetY,
                    CurrentRoom[0].length, CurrentRoom.length,
                    shape[0].length, shape.length)){
                offsetX = 0;
            }

            System.out.println("Random XY: " + RandomXY);
            try {
                InsertShapeFlat(CurrentRoom, shape,
                        RandomXY + offsetX,
                        RandomXY + offsetY);
            }
            catch (ArrayIndexOutOfBoundsException e){
                break;
            }
            offsetX += shape[0].length + 2;

            if((i+1) % 4 == 0) {
                offsetX = 0;
                offsetY += shape.length + 1;
                continue;
            }
            offsetY += 0;
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

    static void PlaceMobs(Room room, char[][] CurrentRoom){

        for(Mob mob : room.RoomCreatures){
            int y = rnd.nextInt(PointGenerateProcedure.ye)+1;

            int x = Math.max(rnd.nextInt(PointGenerateProcedure.xe) +
                    (PointGenerateProcedure.xe/2 * (rnd.nextInt(2) == 1 ? 1 : -1)),1);

            while(!Scans.CheckWall(CurrentRoom[y][x])){

                x = Math.max(rnd.nextInt(PointGenerateProcedure.xe) +
                        (PointGenerateProcedure.xe/2 * (rnd.nextInt(2) == 1 ? 1 : -1)),1);
            }
            if(!Scans.CheckCreature(CurrentRoom[y][x])) {
                CurrentRoom[y][x] = mob.getCreatureSymbol();
                mob.setMobPosition(y,x);
                Dungeon.CurrentRoomCreatures.add(mob);
            }
        }
    }
}