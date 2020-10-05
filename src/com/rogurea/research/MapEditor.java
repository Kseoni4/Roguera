package com.rogurea.research;

import com.googlecode.lanterna.Symbols;

import java.util.Random;

public class MapEditor {

    static enum DrawDirection{
        UP {
            @Override
            public char getCell() {
                return Symbols.DOUBLE_LINE_VERTICAL;
            }
        },
        DOWN{
            @Override
            public char getCell() {
                return Symbols.DOUBLE_LINE_VERTICAL;
            }
        },
        LEFT{
            @Override
            public char getCell() {
                return Symbols.DOUBLE_LINE_HORIZONTAL;
            }
        },
        RIGHT{
            @Override
            public char getCell() {
                return Symbols.DOUBLE_LINE_HORIZONTAL;
            }
        };
        public abstract char getCell();
    }

    static char EmptyCell = ' ';

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

    public static char[][] DrawSubdivideBorders(int sizeA, int sizeB) {
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
    }

    static void PlaceCorners(char[][] ShapeBuffer){
        ShapeBuffer[0][0] = Symbols.DOUBLE_LINE_TOP_LEFT_CORNER;
        ShapeBuffer[ShapeBuffer.length-1][ShapeBuffer[0].length-1] = Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER;
        ShapeBuffer[0][ShapeBuffer[0].length-1] = Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER;
        ShapeBuffer[ShapeBuffer.length-1][0] = Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER;
    }

    static void FillSpaceWithEmpty(char[][] ShapeBuffer){
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
        }
    }

    static void InsertShapeFlat(char[][] CurrentRoom, char[][] shape, int X_a, int Y_a){

        int Y_b = shape.length;

        int X_b = shape[0].length;

        int i, j;

        System.out.println("Y_b: " + Y_b + " X_b: " + X_b);
        for(i = 0;i < Y_b; i++){
            for(j = 0;j < X_b; j++){
                System.out.println(
                        "Y_a: " + Y_a + " X_a:" + X_a + "\n"
                        +"\tY_a+i: " + (Y_a+i) + " X_a+j: " + (X_a+j));
                CurrentRoom[Y_a+i][X_a+j] = shape[i][j];
            }
        }
    }

    static void SubdivideRoom(char[][] CurrentRoom, int X_a, int Y_a){
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
    }

    static void InsertLine(char[][] CurrentRoom, char c, int x, int y){
        CurrentRoom[y][x] = c;
    }

    static void PlaceDoors(R_Room room, char[][] CurrentRoom){
        if(!room.IsEndRoom)
            CurrentRoom[CurrentRoom.length-1][CurrentRoom[0].length/2] = Symbols.ARROW_DOWN;
        if(room.NumberOfRoom > 1)
            CurrentRoom[0][CurrentRoom[0].length/2] = Symbols.ARROW_UP;
    }

    static void PlaceSubShapes(R_Generate.RoomSize roomSize, char[][] CurrentRoom){

        Random random = new Random();

        int offsetX = 0;
        int offsetY = 0;

        int RandomXY = 4;

        for(int i = 0; i < CheckSize(roomSize); i++) {
            char[][] shape = DrawSquare(3);

            System.out.println(
                    "i: " + i + "\n" +
                            "\t offsetX: " + offsetX
                            + " offsetY: " + offsetY

            );
            while (OutOfBounds(RandomXY, offsetX, offsetY,
                    CurrentRoom[0].length, CurrentRoom.length,
                    shape[0].length, shape.length)) {

                RandomXY = DoRandomXY(CurrentRoom[0].length,
                        CurrentRoom.length,
                        random);

                System.out.println("SumX: " + RandomXY+Math.max(offsetX,shape[0].length));
            }

                System.out.println("Random XY: " + RandomXY);
                InsertShapeFlat(CurrentRoom, shape,
                        RandomXY + offsetX,
                        RandomXY + offsetY
                );
            offsetX += shape[0].length;
            offsetY += shape.length;
        }
    }

    static int CheckSize(R_Generate.RoomSize roomSize) {
        switch (roomSize) {
            case MIDDLE -> {
                return 3;
            }
        }
        return 0;
    }

    static boolean OutOfBounds(int XY, int OFX, int OFY, int CRX, int CRY, int SX, int SY){
        return (XY + Math.max(OFX, SX) + SX >= Math.min(CRX-1, CRY-1)
             || XY + Math.max(OFY, SY) + SY >= Math.min(CRX-1, CRY-1));
    }

    static int DoRandomXY(int x, int y, Random random){
        int r = random.nextInt(
                Math.min(x, y)
        );
        System.out.println("min: "+ Math.min(x,y) + " " + "Random: " + r);

        return r;
    }

}