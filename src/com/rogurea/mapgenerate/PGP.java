/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.mapgenerate;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.EditorEntity;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GetRandom;
import com.rogurea.resources.Model;
import com.rogurea.gamemap.Position;


import java.util.ArrayList;
import java.util.Random;

import static com.rogurea.mapgenerate.MapEditor.setIntoCell;

/* Point Generate Procedure
*  Version updated for new game architecture
*  Честно говоря, здесь написан дичайший говнокод, который каким-то чудом рисует так, как надо.
*  Переписывать больно и страшно, надо пересмотреть алгоритм с нуля с учётом возможностти новой "клеточной" архитектуры.
* */
public class PGP {

    private ArrayList<Cell> CRBuffer;

    private int LenghtOfLine;

    private final byte None = 0;

    private final byte OnlyOne = 1;

    private final EditorEntity Dot = new EditorEntity(new Model("dot", Colors.ORANGE, '.'));

    private final byte MinimalDistance = 2;

    private Position FirstPoint = new Position();

    private Position SecondPoint = new Position();

    private Position CrossPoint = new Position();

    public  Position ExitPoint = new Position();

    private MapEditor.DrawDirection CurrentDirection;

    private MapEditor.DrawDirection PreviousDirection;

    private void clearPoints(){
        FirstPoint.setToZero();
        SecondPoint.setToZero();
        ExitPoint.setToZero();
    }

    public PGP(){
        clearPoints();
    }

    public ArrayList<Cell> generateRoom(ArrayList<Cell> CurrentRoomCells){
        CRBuffer = new ArrayList<>();

        CRBuffer.addAll(CurrentRoomCells);

        Debug.toLog("[GENERATE]: Starting a main sequence of the Point Generating Procedure");

        mainSequence();

        Debug.toLog("[GENERATE]: Main sequence of the Point Generating Procedure has completed");

        return CRBuffer;
    }

    private void mainSequence(){
        firstPlace();

        while (true) {
            try {
                inductionPlace();

            } catch (ArrayIndexOutOfBoundsException e) {
                SecondPoint = new Position(FirstPoint);
                break;
            }
        }

        ExitPoint = new Position(FirstPoint);

        while (FirstPoint.y >= 0 && FirstPoint.x >= 0){
            try {
                deductionPlace();
            }catch (ArrayIndexOutOfBoundsException e){
                break;
            }
        }

        finalPlace();
    }

    private void firstPlace(){
        setIntoCell(CRBuffer, Dot, new Position(0,2));
        connectPoint(new Position(0,0), new Position(0,2), OnlyOne);
    }

    private void inductionPlace() throws ArrayIndexOutOfBoundsException{
        byte xBnd = 1, yBnd = 3;

        placer(xBnd,yBnd,false);
    }

    private void deductionPlace() throws ArrayIndexOutOfBoundsException{
        byte xBnd = 2, yBnd = 2;

        placer(xBnd, yBnd, true);
    }

    private void finalPlace(){

        int y0 = FirstPoint.y;

        if(y0 == 0){
            SecondPoint.setPosition(0,0);

            connectPoint(FirstPoint, SecondPoint, OnlyOne);

            setIntoCell(CRBuffer, MapEditor.BorderObjects.LTCorner, SecondPoint);
        }
        else{
            SecondPoint.setPosition(0,0);

            CrossPoint = searchCrossPoint();

            connectPoint(FirstPoint, CrossPoint, None);

            setIntoCell(CRBuffer, MapEditor.BorderObjects.LTCorner, SecondPoint);
        }
    }

    private void placer(byte XBound, byte YBound, boolean isReverse) throws ArrayIndexOutOfBoundsException{
        if(placePoint(XBound,YBound,isReverse)){
            CrossPoint = searchCrossPoint();

            connectPoint(FirstPoint, CrossPoint, None);
        }
    }

    private boolean placePoint(byte XBound, byte YBound, boolean isReverse) throws ArrayIndexOutOfBoundsException{

        Random rnd = GetRandom.RNGenerator;

        int n = isReverse ? -1 : 1;

        int x1 = SecondPoint.x, y1 = SecondPoint.y;

        y1 += (rnd.nextInt(YBound)+MinimalDistance) * n;

        x1 += (rnd.nextInt(XBound)+MinimalDistance);

        SecondPoint.setPosition(y1, x1);

        if(GenerateRules.IsNotOutOfBoundsRule(SecondPoint, CRBuffer)
           && GenerateRules.IsNotObstaclesRule(SecondPoint, CRBuffer)
           && GenerateRules.IsNotSameCellRule(SecondPoint, CRBuffer)
        ){
            setIntoCell(CRBuffer, Dot, SecondPoint);

            return true;
        }
        else{
            if(!GenerateRules.IsNotOutOfBoundsRule(SecondPoint, CRBuffer)){
                throw new ArrayIndexOutOfBoundsException();
            }
            return false;
        }
    }

    private Position searchCrossPoint(){
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
            xCP = x0 + xShift;
            yCP = y1 + yShift;
        }
        else{
            yShift = y1 - y0;
            xShift = x0 - x1;
            xCP = x1 + xShift;
            yCP = y0 + yShift;
        }
        CrsPntBffr = new Position(xCP, yCP);
        if(GenerateRules.IsNotOutOfBoundsRule(CrsPntBffr, CRBuffer)){
            setIntoCell(CRBuffer, new EditorEntity(new Model("zero", Colors.ORANGE, '0')), CrsPntBffr);
        }
        return CrsPntBffr;
    }

    private void connectPoint(Position firstPoint, Position secondPoint, byte n){
        byte ConnectedPoints = n;

        CurrentDirection = getDirection(firstPoint, secondPoint);

        if(GenerateRules.IsNotOutOfBoundsRule(secondPoint, CRBuffer))
            MapEditor.InsertShapeLine(CRBuffer, CurrentDirection, LenghtOfLine, firstPoint);

        if(PreviousDirection != null){
            placeCorner(PreviousDirection, CurrentDirection, firstPoint);
        }

        PreviousDirection = MapEditor.DrawDirection.valueOf(CurrentDirection.toString());

        ConnectedPoints++;

        if(ConnectedPoints < 2)
            connectPoint(secondPoint, SecondPoint, ConnectedPoints);
        else{
            FirstPoint = new Position(SecondPoint);
        }
    }

    private MapEditor.DrawDirection getDirection(Position fP, Position sP){
        int y0 = fP.y, x0 = fP.x,
            y1 = sP.y, x1 = sP.x;

        if(y1 > y0){
            LenghtOfLine = getDeltaL(y1, y0);
            return MapEditor.DrawDirection.DOWN;
        }
        else if (y0 > y1){
            LenghtOfLine = getDeltaL(y0, y1);
            return MapEditor.DrawDirection.UP;
        }
        else if (x1 > x0){
            LenghtOfLine = getDeltaL(x1, x0);
            return MapEditor.DrawDirection.RIGHT;
        }
        else if (x0 > x1){
            LenghtOfLine = getDeltaL(x0, x1);
            return MapEditor.DrawDirection.LEFT;
        }
        return CurrentDirection;
    }

    private int getDeltaL(int P1, int P2){
        return P1-P2;
    }

    private void placeCorner(MapEditor.DrawDirection pD, MapEditor.DrawDirection cD, Position fP) {
        if(pD == MapEditor.DrawDirection.DOWN && cD == MapEditor.DrawDirection.RIGHT
                || pD == MapEditor.DrawDirection.LEFT && cD == MapEditor.DrawDirection.UP)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.LBCorner, fP);

        if(pD == MapEditor.DrawDirection.RIGHT && cD == MapEditor.DrawDirection.DOWN
                || pD == MapEditor.DrawDirection.UP && cD == MapEditor.DrawDirection.LEFT)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.RTCorner, fP);

        if(pD == MapEditor.DrawDirection.UP && cD == MapEditor.DrawDirection.RIGHT
                || pD == MapEditor.DrawDirection.LEFT && cD == MapEditor.DrawDirection.DOWN)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.LTCorner, fP);

        if(pD == MapEditor.DrawDirection.DOWN && cD == MapEditor.DrawDirection.LEFT
                ||pD == MapEditor.DrawDirection.RIGHT && cD == MapEditor.DrawDirection.UP)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.RBCorner, fP);

        if(pD == MapEditor.DrawDirection.RIGHT && cD == MapEditor.DrawDirection.LEFT)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.HWall, fP);

        if(pD == MapEditor.DrawDirection.LEFT && cD == MapEditor.DrawDirection.RIGHT)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.HWall, fP);

        if(pD == MapEditor.DrawDirection.UP && cD == MapEditor.DrawDirection.DOWN)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.VWall, fP);

        if(pD == MapEditor.DrawDirection.DOWN && cD == MapEditor.DrawDirection.UP)
            setIntoCell(CRBuffer, MapEditor.BorderObjects.VWall, fP);
    }
}
