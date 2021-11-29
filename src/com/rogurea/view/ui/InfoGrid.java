/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.view.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.items.Potion;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.Model;
import com.rogurea.view.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class InfoGrid implements IViewBlock {

    private ArrayList<IViewBlock> _viewBlocks;

    private Position _startPointXY;

    private int _leastRightX;

    private int _leastBottomY;

    private Position _pointXY;

    private Position _pointYX;

    private Position _zeroPointBlock1;

    private Position _zeroPointBlock2;

    private Position _zeroPointBlock3;

    private Model borderVWall;

    private Model borderCRCorner;

    private Model borderHWall;

    private Model borderLRCorner;

    private Model borderLDCorner;

    private Model borderLUCorner;

    private TextGraphics _infoGridGraphics;

    private HashMap<String, Integer> bonusTimerMap = new HashMap<>();

    private int bonusTimer = 0;

    public InfoGrid() {
        _viewBlocks = new ArrayList<>();
        ViewObjects.ViewBlocks.add(this);
    }

    public IViewBlock getFirstBlock(){
        return _viewBlocks.get(0);
    }

    public IViewBlock getSecondBlock(){
        return _viewBlocks.get(1);
    }

    public IViewBlock getThirdBlock(){
        return _viewBlocks.get(2);
    }

    public int getHorizontalBlockHeight(){
        return (_pointYX.y-1) - _zeroPointBlock1.y;
    }

    public int getVerticalBlockWight() {return _leastRightX - _zeroPointBlock2.x;}

    public Position get_zeroPointBlock1() {
        return _zeroPointBlock1;
    }

    public Position get_zeroPointBlock2() {
        return _zeroPointBlock2;
    }

    public Position get_zeroPointBlock3() {
        return _zeroPointBlock3;
    }

    public Position placeNewBlock(IViewBlock viewBlock, int numberOfBlock){
        _viewBlocks.add(viewBlock);
        switch (numberOfBlock){
            case 1: return _zeroPointBlock1;
            case 2: return _zeroPointBlock2;
            case 3: return _zeroPointBlock3;
        }
        return _zeroPointBlock1;
    }

    private void divideGrid(){
        int _xLenght = _leastRightX - _startPointXY.x;
        int _y = 0;
        int _yLenght = _leastBottomY;
        int calculatedXLenght = (_xLenght/5)+8;
        _pointXY = _startPointXY.getRelative(calculatedXLenght,_y);
        _zeroPointBlock1 = _startPointXY.getRelative(1,1);
        _zeroPointBlock2 = _pointXY.getRelative(1,1);
        _pointYX = _startPointXY.getRelative(calculatedXLenght, _yLenght/4*3);
        _zeroPointBlock3 = _pointYX.getRelative((-calculatedXLenght)+1, 1);
    }

    private void drawBorders(){
        for (int y = _startPointXY.y; y < _leastBottomY; y++) {
            TerminalView.putCharInTerminal(_infoGridGraphics, borderVWall.get(), _startPointXY.getRelative(0, y));
        }
        for (int x = _startPointXY.x; x < _leastRightX; x++) {
            TerminalView.putCharInTerminal(_infoGridGraphics, borderHWall.get(), _startPointXY.getRelative(x-_startPointXY.x,0));
        }
        TerminalView.putCharInTerminal(_infoGridGraphics, borderLRCorner.get(), _startPointXY);
        for(int y = _pointXY.y; y < _pointYX.y; y++){
            TerminalView.putCharInTerminal(_infoGridGraphics, borderVWall.get(), _pointXY.getRelative(0,y));
        }
        TerminalView.putCharInTerminal(_infoGridGraphics, borderLDCorner.get(), _pointXY);
        for(int x = _zeroPointBlock3.x; x < _leastRightX; x++){
            TerminalView.putCharInTerminal(_infoGridGraphics, borderHWall.get(), _zeroPointBlock3.getRelative(x-_zeroPointBlock3.x,-1));
        }
        TerminalView.putCharInTerminal(_infoGridGraphics, borderLUCorner.get(), _pointYX);
        TerminalView.putCharInTerminal(_infoGridGraphics, borderCRCorner.get(), _zeroPointBlock3.getRelative(-1,-1));
    }

    public Position get_pointYX() {
        return _pointYX;
    }

    @Override
    public void Init() {
        if(_infoGridGraphics == null) {
            try {
                _infoGridGraphics = TerminalView.terminal.newTextGraphics();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        _startPointXY = new Position(ViewObjects.mapView.size.getColumns(), 0);
        _leastRightX = TerminalView.windowWight;
        _leastBottomY = TerminalView.windowHeight;
        borderVWall = new Model("B_VWall", '║');
        borderHWall = new Model("B_HWall", '═');
        borderCRCorner = new Model("B_CRCorner",'╠');
        borderLRCorner = new Model("B_LRCorner",'╔');
        borderLDCorner = new Model("B_LDCorner",'╦');
        borderLUCorner = new Model("B_LUCorner", '╩');
        divideGrid();
        if(!_viewBlocks.isEmpty()){
            _viewBlocks.forEach(Draw::init);
        }
    }

    @Override
    public void Draw() {
        drawBorders();
        if (!_viewBlocks.isEmpty()) {
            _viewBlocks.forEach(Draw::call);
        }
        drawKeyInfo();
    }

    private void drawKeyInfo(){
        String WASD = Colors.WHITE_BRIGHT+"WASD/↑↓←→";
        String I = Colors.WHITE_BRIGHT+"i";
        String quickMenu = Colors.WHITE_BRIGHT+"1,2,3,4,5";
        String info = Colors.B_BLUE_BRIGHT+
                WASD+Colors.GREY+" - walk"+Colors.WHITE_BRIGHT+"|"
                +I+Colors.GREY+" - inventory"+Colors.WHITE_BRIGHT+"|"
                +quickMenu+Colors.GREY+" - quick equip/use"+Colors.WHITE_BRIGHT+"|";

        _infoGridGraphics.fillRectangle(new TerminalPosition(0,_leastBottomY-1), new TerminalSize(_leastRightX,1),new TextCharacter(' ').withBackgroundColor(Colors.GetTextColor(Colors.B_BLUE_BRIGHT)));
        TerminalView.drawBlockInTerminal(_infoGridGraphics, info, new Position(0, _leastBottomY-1));
    }

    Semaphore syncAdd = new Semaphore(2,true);

    public void putPotionBonusInfo(Potion potion) throws InterruptedException {
        syncAdd.acquire();
/*        if(bonusTimer >= 0){
            bonusTimer += potion.getEffectTimer();
        }*/
        int currentTime = 0;
        if(bonusTimerMap.get(potion.model.toString()) != null) {
            currentTime = bonusTimerMap.get(potion.model.toString());
        }
        bonusTimerMap.put(potion.model.toString(), currentTime+potion.getEffectTimer());
        syncAdd.release();
    }

    Semaphore syncTimer = new Semaphore(1,true);

    public void drawPotionTimer(Potion potion) throws InterruptedException {
        syncTimer.acquire();

        String mdl = potion.model.toString();
        int i = 0;
        for(; i < bonusTimerMap.size(); i++){
            if(bonusTimerMap.keySet().toArray()[i].equals(mdl)){
                break;
            }
        }

        if(bonusTimerMap.get(mdl) > 0){
            if(!Window.isOpen()) {
                TerminalView.drawBlockInTerminal(_infoGridGraphics, "" + mdl + " " + bonusTimerMap.get(mdl) + "  ", _zeroPointBlock1.getRelative(0, 10 + i));
            }
            bonusTimerMap.put(mdl, bonusTimerMap.get(mdl) - 1);
        }
        if(!Window.isOpen()) {
            Draw.flush();
        }
        TimeUnit.SECONDS.sleep(1);
        syncTimer.release();
    }

    public void clearTimers(){
        _infoGridGraphics.fillRectangle(_zeroPointBlock1.getRelative(0,10).toTerminalPosition(), new TerminalSize(5,3),' ');
        Draw.flush();
    }

    @Override
    public void Reset() {
        if(!_viewBlocks.isEmpty()){
            _viewBlocks.forEach(IViewBlock::Reset);
        }
    }
}
