package com.rogurea.main.mapgenerate;

import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.map.Position;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.resources.GetRandom;

import java.util.Random;

import static com.rogurea.main.mapgenerate.MapEditor.setIntoCell;

/* Point Generate Procedure*/
public class PGP {

    private char[][] CRBuffer;

    private int LenghtOfLine;

    private final byte None = 0;

    private final byte OnlyOne = 1;

    private final char Dot = '.';

    private final byte MinimalDistance = 2;

    private Position FirstPoint = new Position();

    private Position SecondPoint = new Position();

    private Position CrossPoint = new Position();

    public  Position ExitPoint = new Position();

    private MapEditor.DrawDirection CurrentDirection;

    private MapEditor.DrawDirection PreviousDirection;

    private void ClearPoints(){
        FirstPoint.setToZero();
        SecondPoint.setToZero();
        ExitPoint.setToZero();
    }

    public PGP(){
        ClearPoints();
    }

    public char[][] GenerateRoom(char[][] CurrentRoom){
        CRBuffer = CurrentRoom;
        System.out.flush();
        System.out.printf(
                "Room size \n" +
                        "\t yMax = %d\n" +
                        "\t xMax = %d\n",
                CRBuffer.length,
                CRBuffer[0].length
        );

        Debug.log("GENERATE: Starting a main sequence of the Point Generating Procedure");

        MainSequence();

        Debug.log("GENERATE: Main sequence of the Point Generating Procedure has completed");

        return CRBuffer;
    }

    private void MainSequence(){
        FirstPlace();

        System.out.printf("FirstPoint:\n" +
                "\t x0 = %d\n" +
                "\t y0 = %d\n",
                FirstPoint.x, FirstPoint.y);

        //Debug.log("PGP: Induction Place ");

        System.out.println("[Induction Place]");
        while (true) {
            try {
                InductionPlace();
                System.out.printf("FirstPoint:\n" +
                                "\t x0 = %d\n" +
                                "\t y0 = %d\n",
                        FirstPoint.x, FirstPoint.y);
            } catch (ArrayIndexOutOfBoundsException e) {
                SecondPoint = new Position(FirstPoint);
                break;
            }
        }

        //Debug.log("PGP: Induction place ended ");

        ExitPoint = new Position(FirstPoint);
        System.out.printf("ExitPoint: x:%d y:%d\n", ExitPoint.x, ExitPoint.y);

        //Debug.log("PGP: Exit Point: " + ExitPoint.toString());

        //Debug.log("PGP: Deduction Place");

        System.out.println("[Deduction Place]");
        while (FirstPoint.y >= 0 && FirstPoint.x >= 0){
            try {
                DeductionPlace();
                System.out.printf("FirstPoint:\n" +
                                "\t x0 = %d\n" +
                                "\t y0 = %d\n",
                        FirstPoint.x, FirstPoint.y);
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }
        //Debug.log("PGP: Deduction Place ended");

        FinalPlace();
    }

    private void FirstPlace(){
        setIntoCell(CRBuffer, Dot, new Position(0,2));
        ConnectPoint(new Position(0,0), new Position(0,2), OnlyOne);
    }

    private void InductionPlace() throws ArrayIndexOutOfBoundsException{
        byte xBnd = 1, yBnd = 5;

        Placer(xBnd,yBnd,false);
    }

    private void DeductionPlace() throws ArrayIndexOutOfBoundsException{
        byte xBnd = 1, yBnd = 2;

        Placer(xBnd, yBnd, true);
    }

    private void FinalPlace(){

        System.out.println("[Final Place]");

        Debug.log("PGP: Final Place");

        int y0 = FirstPoint.y;

        if(y0 == 0){
            System.out.println("y0 = 0");
            SecondPoint.setPosition(0,0);

            ConnectPoint(FirstPoint, SecondPoint, OnlyOne);

            setIntoCell(CRBuffer, GameResources.GetModel("LTCorner"), SecondPoint);
        }
        else{
            System.out.println("y0 = " + y0);
            SecondPoint.setPosition(0,0);

            CrossPoint = SearchCrossPoint();

            ConnectPoint(FirstPoint, CrossPoint, None);

            setIntoCell(CRBuffer, GameResources.GetModel("LTCorner"), SecondPoint);
        }
    }

    private void Placer(byte XBound, byte YBound, boolean isReverse) throws ArrayIndexOutOfBoundsException{
        if(PlacePoint(XBound,YBound,isReverse)){
            CrossPoint = SearchCrossPoint();

            System.out.printf("CrossPoint set in:\n" +
                    "\t xCP = %d\n" +
                    "\t yCP = %d\n",
                    CrossPoint.x, CrossPoint.y);
            ConnectPoint(FirstPoint, CrossPoint, None);
        }
    }

    private boolean PlacePoint(byte XBound, byte YBound, boolean isReverse) throws ArrayIndexOutOfBoundsException{

        Random rnd = GetRandom.RNGenerator;

        int n = isReverse ? -1 : 1;

        int x1 = SecondPoint.x, y1 = SecondPoint.y;

        y1 += (rnd.nextInt(YBound)+MinimalDistance) * n;

        x1 += (rnd.nextInt(XBound)+MinimalDistance);

        SecondPoint.setPosition(y1, x1);

        System.out.printf("Trying to place point into\n" +
                        "\t x1 = %d\n" +
                        "\t y1 = %d\n",
                SecondPoint.x, SecondPoint.y);

        if(GenerateRules.IsNotOutOfBoundsRule(SecondPoint, CRBuffer)
           && GenerateRules.IsNotObstaclesRule(SecondPoint, CRBuffer)
           && GenerateRules.IsNotSameCellRule(SecondPoint, CRBuffer)
        ){
            setIntoCell(CRBuffer, Dot, SecondPoint);

            System.out.printf("Placed new point into\n" +
                    "\t x1 = %d\n" +
                    "\t y1 = %d\n",
                    SecondPoint.x, SecondPoint.y);

            return true;
        }
        else{
            if(!GenerateRules.IsNotOutOfBoundsRule(SecondPoint, CRBuffer)){
                throw new ArrayIndexOutOfBoundsException();
            }
            return false;
        }
    }

    private Position SearchCrossPoint(){
        int y0 = FirstPoint.y, x0 = FirstPoint.x,
            y1 = SecondPoint.y, x1 = SecondPoint.x;

        Position CrsPntBffr;

        int xShift, yShift, xCP, yCP;

        if(y1 > x1){
            yShift = y1 - y0;
            xShift = x0 - x1;
            xCP = x1 + xShift;
            yCP = y0 + yShift;
        }
        else if(x1 > y1){
            yShift = y0 - y1;
            xShift = x1 - x0;
            yCP = y1 + yShift;
            xCP = x0 + xShift;
        }
        else{
            yShift = y1 - y0;
            xShift = x0 - x1;
            xCP = x1 + xShift;
            yCP = y0 + yShift;
        }
        CrsPntBffr = new Position(xCP, yCP);
        if(GenerateRules.IsNotOutOfBoundsRule(CrsPntBffr, CRBuffer)){
            setIntoCell(CRBuffer, '0', CrsPntBffr);
        }
        return CrsPntBffr;
    }

    private void ConnectPoint(Position firstPoint, Position secondPoint, byte n){
        byte ConnectedPoints = n;

        System.out.printf("Connecting points" +
                        "\n\t from:\n" +
                            "\t\t x0 = %d\n" +
                            "\t\t y0 = %d\n" +
                        "\t to:\n" +
                            "\t\t x1 = %d\n" +
                            "\t\t y1 = %d\n",
                        firstPoint.x, firstPoint.y,
                        secondPoint.x, secondPoint.y
        );

        CurrentDirection = GetDirection(firstPoint, secondPoint);

        System.out.printf("pD = %s\n", PreviousDirection);
        System.out.printf("cD = %s\n", CurrentDirection);

        if(GenerateRules.IsNotOutOfBoundsRule(secondPoint, CRBuffer))
            MapEditor.InsertShapeLine(CRBuffer, CurrentDirection, LenghtOfLine, firstPoint);

        if(PreviousDirection != null){
            PlaceCorner(PreviousDirection, CurrentDirection, firstPoint);
            System.out.printf("Corner placing in:\n" +
                    "\t x1 = %d\n" +
                    "\t y1 = %d\n",
                    firstPoint.x, firstPoint.y
            );
        }

        PreviousDirection = MapEditor.DrawDirection.valueOf(CurrentDirection.toString());

        ConnectedPoints++;

        if(ConnectedPoints < 2)
            ConnectPoint(secondPoint, SecondPoint, ConnectedPoints);
        else{
            FirstPoint = new Position(SecondPoint);
        }
    }

    private MapEditor.DrawDirection GetDirection(Position fP, Position sP){
        int y0 = fP.y, x0 = fP.x,
            y1 = sP.y, x1 = sP.x;

        if(y1 > y0){
            LenghtOfLine = GetDeltaL(y1, y0);
            return MapEditor.DrawDirection.DOWN;
        }
        else if (y0 > y1){
            LenghtOfLine = GetDeltaL(y0, y1);
            return MapEditor.DrawDirection.UP;
        }
        else if (x1 > x0){
            LenghtOfLine = GetDeltaL(x1, x0);
            return MapEditor.DrawDirection.RIGHT;
        }
        else if (x0 > x1){
            LenghtOfLine = GetDeltaL(x0, x1);
            return MapEditor.DrawDirection.LEFT;
        }
        return CurrentDirection;
    }

    private int GetDeltaL(int P1, int P2){
        return P1-P2;
    }

    private void PlaceCorner(MapEditor.DrawDirection pD, MapEditor.DrawDirection cD, Position fP) {
        if(pD == MapEditor.DrawDirection.DOWN && cD == MapEditor.DrawDirection.RIGHT
                || pD == MapEditor.DrawDirection.LEFT && cD == MapEditor.DrawDirection.UP)
            setIntoCell(CRBuffer, GameResources.GetModel("LBCorner"), fP);

        if(pD == MapEditor.DrawDirection.RIGHT && cD == MapEditor.DrawDirection.DOWN
                || pD == MapEditor.DrawDirection.UP && cD == MapEditor.DrawDirection.LEFT)
            setIntoCell(CRBuffer, GameResources.GetModel("RTCorner"), fP);

        if(pD == MapEditor.DrawDirection.UP && cD == MapEditor.DrawDirection.RIGHT
                || pD == MapEditor.DrawDirection.LEFT && cD == MapEditor.DrawDirection.DOWN)
            setIntoCell(CRBuffer, GameResources.GetModel("LTCorner"), fP);

        if(pD == MapEditor.DrawDirection.DOWN && cD == MapEditor.DrawDirection.LEFT
                ||pD == MapEditor.DrawDirection.RIGHT && cD == MapEditor.DrawDirection.UP)
            setIntoCell(CRBuffer, GameResources.GetModel("RBCorner"), fP);

        if(pD == MapEditor.DrawDirection.RIGHT && cD == MapEditor.DrawDirection.LEFT)
            setIntoCell(CRBuffer, GameResources.GetModel("Hwall"), fP);

        if(pD == MapEditor.DrawDirection.LEFT && cD == MapEditor.DrawDirection.RIGHT)
            setIntoCell(CRBuffer, GameResources.GetModel("Hwall"), fP);

        if(pD == MapEditor.DrawDirection.UP && cD == MapEditor.DrawDirection.DOWN)
            setIntoCell(CRBuffer, GameResources.GetModel("Vwall"), fP);

        if(pD == MapEditor.DrawDirection.DOWN && cD == MapEditor.DrawDirection.UP)
            setIntoCell(CRBuffer, GameResources.GetModel("Vwall"), fP);
    }
}
