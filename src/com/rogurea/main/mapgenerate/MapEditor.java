package com.rogurea.main.mapgenerate;

import com.googlecode.lanterna.Symbols;
import com.rogurea.main.props.Prop;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.map.Room;
import com.rogurea.main.gamelogic.Scans;

import java.util.ArrayList;
import java.util.Random;

public class MapEditor {

    static enum DrawDirection{
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
                return GenerateRules.previousDirection.getCell();
            }

            @Override
            public char getCorner(boolean IsReverse) {
                return GenerateRules.previousDirection.getCorner(IsReverse);
            }
        };
        public abstract char getCell();
        public abstract char getCorner(boolean IsReverse);
    }

    public static char EmptyCell = GameResources.EmptyCell;

    private static char[] LineBuffer;

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
            case SAME -> {
                InsertShapeLine(CurrentRoom, GenerateRules.previousDirection, Length, X_a, Y_a);
            }
        }
    }

    static void InsertShapeFlat(char[][] CurrentRoom, char[][] shape, int X_a, int Y_a){

        int Y_b = shape.length;

        int X_b = shape[0].length;

        int i, j;

        /*System.out.println("Y_b: " + Y_b + " X_b: " + X_b);*/

        for(i = 0;i < Y_b; i++){
            for(j = 0;j < X_b; j++){

/*                System.out.println(
                        "Y_a: " + Y_a + " X_a:" + X_a + "\n"
                        +"\tY_a+i: " + (Y_a+i) + " X_a+j: " + (X_a+j));*/

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

        char cell = CurrentRoom[GenerateRules.ye][GenerateRules.xe];

        if(!room.IsEndRoom) {
            if (Scans.CheckCorner(cell))
                CurrentRoom[GenerateRules.ye][GenerateRules.xe] = GameResources.NextRoom;
            else{
                int x_shift = FindWall(CurrentRoom);
                CurrentRoom[GenerateRules.ye][GenerateRules.xe+x_shift] = GameResources.NextRoom;
            }
        }
            /*while(!isPlaced) {

                if ((CurrentRoom[GenerateRules.ye][GenerateRules.xe] == GameResources.HWall)) {

                    isPlaced = true;
                    break;
                }

                if(GenerateRules.xe-1 >= 0)
                    GenerateRules.xe--;
                else if(GenerateRules.xe+1 < CurrentRoom[0].length)
                    GenerateRules.xe++;
            }
*/
        if(room.NumberOfRoom > 1)
            CurrentRoom[0][CurrentRoom[0].length/2] = GameResources.BackRoom;
    }

    private static int FindWall(char[][] CurrentRoom){
        char cell = CurrentRoom[GenerateRules.ye][GenerateRules.xe];

        int l = (cell == GameResources.RBCorner || cell == GameResources.RTCorner ? -1 : 1);

        int x_shift = 0;

        while(!Scans.CheckWall(cell) && !Scans.CheckCorner(cell)){
            x_shift++;
            cell = CurrentRoom[GenerateRules.ye][GenerateRules.xe+(x_shift*=l)];
        }

        return x_shift;
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
        //System.out.println("min: "+ Math.min(x,y) + " " + "Random: " + r);

        return random.nextInt(
                Math.min(x, y)
        );
    }

}


/*    public static char[][] DrawSubdivideBorders(int sizeA, int sizeB) {
        char[][] SubdivideBuffer = new char[sizeA][sizeB];

        FillSpaceWithEmpty(SubdivideBuffer);

        for(int x = 0; x < sizeB; x++){
            SubdivideBuffer[0][x] = Symbols.BLOCK_SPARSE;
            SubdivideBuffer[sizeA-1][x] = Symbols.BLOCK_SPARSE;
        }
        for(int y = 0; y < sizeA; y++) {
            SubdivideBuffer[y][0] = Symbols.BLOCK_SPARSE;
            SubdivideBuffer[y][sizeB - 1] = Symbols.BLOCK_SPARSE;
        }
        return SubdivideBuffer;
    }*/

/*    static void SubdivideRoom(char[][] CurrentRoom, int X_a, int Y_a){
        char[][] SubdivideShape = DrawSubdivideBorders(5,7);
        InsertShapeFlat(CurrentRoom, SubdivideShape, X_a, Y_a);

        for(int i = 0; i < SubdivideShape.length; i++) {
            for (int j = 0; j < SubdivideShape[0].length; j++) {
                if (CurrentRoom[Y_a+i][X_a+j] == Symbols.BLOCK_SPARSE)
                    CurrentRoom[Y_a+i][X_a+j] = Symbols.DOUBLE_LINE_HORIZONTAL;
            }
            if (CurrentRoom[Y_a+i][0] == Symbols.BLOCK_SPARSE)
                CurrentRoom[Y_a+i][0] = Symbols.DOUBLE_LINE_VERTICAL;
        }
    }*/