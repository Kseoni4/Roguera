package com.rogurea.main.mapgenerate;

import com.googlecode.lanterna.Symbols;
import com.rogurea.main.resources.GameResources;

import java.util.Random;

public class PointGenerateProcedure {

    private static final Random random = new Random();

    private static int  y0, x0, //Координаты первой точки
                        xp, yp, //Координаты второй точки
                        y_, x_, //Координаты пересечения линий от первых двух точек
                        p, l;   // Номер точки и длинна линии

    public static int xe, ye; //Кординаты точки выхода

    private static MapEditor.DrawDirection currentDirection;

    public static MapEditor.DrawDirection previousDirection;

    public static void GenerationShapeByPoints(char[][] CurrentRoom){
        ClearCoords();
        MainSequence(CurrentRoom);
    }

    private static void ClearCoords(){
        y0 = 0; x0 = 0;
        xp = 0; yp = 0;
        y_ = 0; x_ = 0;
        l = 0; p = 0;
    }

    private static void MainSequence(char[][] CurrentRoom){
        {
            StartPlace(CurrentRoom);
        }
        while(y0 < CurrentRoom.length && x0 < CurrentRoom[0].length/2){
            InductionPlace(CurrentRoom);
            if(yp >= CurrentRoom.length || xp >= CurrentRoom[0].length){
                break;
            }
        }

        ye = y0; xe = x0;
        while(y0 >= 0 && x0 >= 0){
            DeductionPlace(CurrentRoom);
            if(xp < 0 || yp < 0){
                break;
            }
        }
        FinalPlace(CurrentRoom);
    }

    private static void StartPlace(char[][] CurrentRoom){
        CurrentRoom[2][0] = '.';
        ConnectPoints(CurrentRoom, y0,x0, 2,0);
    }

    private static void InductionPlace(char[][] CurrentRoom){
        PlaceAndConnectXY(CurrentRoom,5,3,false);
    }

    private static void DeductionPlace(char[][] CurrentRoom){
        PlaceAndConnectXY(CurrentRoom,3,1,true);
    }

    private static void PlaceAndConnectXY(char[][] CurrentRoom, int Ybound, int Xbound, boolean IsReverse){
        if(PlacePoint(CurrentRoom, Ybound, Xbound, IsReverse)) {
            SearchXYPoint(CurrentRoom, y0, x0, yp, xp);
            ConnectPoints(CurrentRoom, y0, x0, y_, x_);
            ConnectPoints(CurrentRoom, y0, x0, yp, xp);
        }
    }

    private static void FinalPlace(char[][] CurrentRoom){
        if (y0 == 0) {
            ConnectPoints(CurrentRoom, y0, x0, y_, x_);
            PlaceCorner(CurrentRoom, y0, x0);
        }
        else{
            SearchXYPoint(CurrentRoom, y0, x0, 0, 0);
            ConnectPoints(CurrentRoom, y0, x0, y_, x_);
            ConnectPoints(CurrentRoom, y0, x0, 0, 0);
            CurrentRoom[0][0] = Symbols.DOUBLE_LINE_TOP_LEFT_CORNER;
        }
    }

    private static boolean PlacePoint(char[][] CurrentRoom, int Ybound, int Xbound, boolean IsReverse){

        int n = 1;

        if(IsReverse){
            n = -1;
        }

        yp += (random.nextInt(Ybound)+3) * n;
        if(xp < CurrentRoom[0].length)
            xp += (random.nextInt(Xbound)+3);
        if(
                IsNotOutOfBoundsRule(yp,xp, CurrentRoom)
                && IsNotObstaclesRule(yp,xp, CurrentRoom)
                && IsNotSameCellRule(yp,xp, CurrentRoom)
        )
        {
            CurrentRoom[yp][xp] = '.';
            return true;
        }
        else{
            return false;
        }
    }

    private static void SearchXYPoint(char[][] CurrentRoom, int y0, int x0, int yp, int xp){
        int xp_s;
        int yp_s;

        if(yp>xp){
            yp_s = yp - y0;
            xp_s = x0 - xp;
            y_ = y0+yp_s;
            x_ = xp+xp_s;
            if(IsNotOutOfBoundsRule(y_, x_, CurrentRoom)){
                CurrentRoom[y_][x_] = String.valueOf(p).toCharArray()[0];
            }
        }
        else if (xp>yp){
            yp_s = y0 - yp;
            xp_s = xp - x0;
            y_ = yp+yp_s;
            x_ = x0+xp_s;
            if(IsNotOutOfBoundsRule(y_, x_, CurrentRoom)) {
                CurrentRoom[y_][x_] = String.valueOf(p).toCharArray()[0];
            }
        }
        else {
            yp_s = yp - y0;
            xp_s = xp - x0;
            y_ = y0 + yp_s;
            x_ = xp - xp_s;
            if(IsNotOutOfBoundsRule(y_, x_, CurrentRoom)) {
                CurrentRoom[y_][x_] = String.valueOf(p).toCharArray()[0];
            }
        }
    }

    private static void ConnectPoints(char[][] CurrentRoom, int y0, int x0, int y1, int x1){

        currentDirection = SetDirection(y1, x1);

        if(IsNotOutOfBoundsRule(y1, x1, CurrentRoom))
            MapEditor.InsertShapeLine(CurrentRoom, currentDirection, l, x0, y0);

        if(previousDirection != null){
            PlaceCorner(CurrentRoom, y0, x0);
        }

        previousDirection = currentDirection;

        PointGenerateProcedure.y0 = y1;
        PointGenerateProcedure.x0 = x1;

    }

    private static MapEditor.DrawDirection SetDirection(int y, int x) {

        if(y > y0){
            currentDirection = MapEditor.DrawDirection.DOWN;
            l=y-y0;
        }
        else if(y0 > y){
            currentDirection = MapEditor.DrawDirection.UP;
            l=y0-y;
        }
        else if(x > x0){
            currentDirection = MapEditor.DrawDirection.RIGHT;
            l=x-x0;
        }
        else if (x0 > x){
            currentDirection = MapEditor.DrawDirection.LEFT;
            l=x0-x;
        }
        return currentDirection;
    }

    private static void PlaceCorner(char[][] CurrentRoom, int y1, int x1) {
        if(previousDirection == MapEditor.DrawDirection.DOWN
        && currentDirection == MapEditor.DrawDirection.RIGHT
        || previousDirection == MapEditor.DrawDirection.LEFT
        && currentDirection == MapEditor.DrawDirection.UP)
            CurrentRoom[y1][x1] = GameResources.LBCorner;

        if(previousDirection == MapEditor.DrawDirection.RIGHT
        && currentDirection == MapEditor.DrawDirection.DOWN
        || previousDirection == MapEditor.DrawDirection.UP
        && currentDirection == MapEditor.DrawDirection.LEFT)
            CurrentRoom[y1][x1] = GameResources.RTCorner;

        if(previousDirection == MapEditor.DrawDirection.UP
        && currentDirection == MapEditor.DrawDirection.RIGHT
        || previousDirection == MapEditor.DrawDirection.LEFT
        && currentDirection == MapEditor.DrawDirection.DOWN)
            CurrentRoom[y1][x1] = GameResources.LTCorner;

        if(previousDirection == MapEditor.DrawDirection.DOWN
        && currentDirection == MapEditor.DrawDirection.LEFT
        ||previousDirection == MapEditor.DrawDirection.RIGHT
        && currentDirection == MapEditor.DrawDirection.UP)
            CurrentRoom[y1][x1] = GameResources.RBCorner;

        if(previousDirection == MapEditor.DrawDirection.RIGHT
                && currentDirection == MapEditor.DrawDirection.LEFT)
            CurrentRoom[y1][x1] = GameResources.HWall;

        if(previousDirection == MapEditor.DrawDirection.LEFT
                && currentDirection == MapEditor.DrawDirection.RIGHT)
            CurrentRoom[y1][x1] = GameResources.HWall;

        if(previousDirection == MapEditor.DrawDirection.UP
                && currentDirection == MapEditor.DrawDirection.DOWN)
            CurrentRoom[y1][x1] = GameResources.VWall;

        if(previousDirection == MapEditor.DrawDirection.DOWN
                && currentDirection == MapEditor.DrawDirection.UP)
            CurrentRoom[y1][x1] = GameResources.VWall;

    }

    /* Правила для размещения точек */
    private static boolean IsNotOutOfBoundsRule(int yp, int xp, char[][] CurrentRoom){
        return (yp < CurrentRoom.length && xp < CurrentRoom[0].length)
                && (yp >= 0 && xp >= 0);
    }

    private static boolean IsNotObstaclesRule(int y, int x, char[][] CurrentRoom){
        for(int i = 0; i < CurrentRoom.length-y; i++){
            if(CheckObstacles(CurrentRoom[y+i][x]))
                return false;
        }
        for(int i = 0; i < CurrentRoom[0].length-x; i++){
            if(CheckObstacles(CurrentRoom[y][x+i]))
                return false;
        }
        for(int i = 0; i > (CurrentRoom.length-y)*-1 && y+i > 0; i--){
            if(CheckObstacles(CurrentRoom[y+i][x]))
                return false;
        }
        for(int i = 0 ; i > (CurrentRoom[0].length-x)*-1 && x+i > 0; i--){
            if(CheckObstacles(CurrentRoom[y][x+i]))
                return false;
        }
        return true;
    }

    private static boolean CheckObstacles(char cell){
        return /*cell == '.' ||*/ cell == Symbols.DOUBLE_LINE_HORIZONTAL || cell == Symbols.DOUBLE_LINE_VERTICAL;
    }

    private static boolean IsNotSameCellRule(int y, int x, char[][] CurrentRoom){
        return CurrentRoom[y][x] != '.';
    }

}

