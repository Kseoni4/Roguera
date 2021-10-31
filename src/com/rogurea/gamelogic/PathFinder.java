/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.gamelogic;

import com.rogurea.Roguera;
import com.rogurea.base.Debug;
import com.rogurea.gamemap.Cell;
import com.rogurea.gamemap.Position;
import com.rogurea.gamemap.Room;
import com.rogurea.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder {

    private LinkedList<Node> openList;

    private LinkedList<Node> closeList;

    private final int HORIZONTAL = 10;

    private final int DIAGONAL = 14;

    ArrayList<Position> finalPath;

    public ArrayList<Position> getPathToTarget(Room map, Position target, Position start){
        openList = new LinkedList<>();
        closeList = new LinkedList<>();
        finalPath = new ArrayList<>();
        Debug.toLog("[A*]ALGORITHM IS STARTED");
/*        Debug.toLog("[A*]Room: "+map.RoomNumber);
        Debug.toLog("[A*]Target position: "+target.toString());
        Debug.toLog("[A*]Start position: "+start.toString());*/

        openList.add(new Node(map.getCell(start),null, 0,0,0));
/*        Debug.toLog("[A*]Add start node to open list: "+openList.getFirst().nodeCell.position.toString());

        Debug.toLog("[A*]Add start node to close list");*/
        closeList.add(openList.getFirst());

        while(!openList.isEmpty() && !targetNodeIsFind(target)) {
            
            findNodeSequence(target, closeList.getLast());
        
        }

        getFinalPath(closeList.getLast());

        try {
            finalPath.add(openList.getLast().nodeCell.position);
        }catch (NoSuchElementException e){
            Debug.toLog("[A*]Can't find element");
        }

        Debug.toLog("[A*]Final path("+finalPath.size()+"): ");

        //Показать путь движения в консоли
        if(Roguera.isDebug) {
            System.out.print("start -> ");
            for (Position pos : finalPath) {
                System.out.print(pos.toString() + " -> ");
            }
            System.out.print("end");
            System.out.println();
        }

        Debug.toLog("[A*]ALGORITHM IS ENDED");
        return finalPath;
    };

    private void findNodeSequence(Position target, Node start){

        Cell[] cellsAround = start.nodeCell.getCellsAround();

       // Debug.toLog("[A*]Remove start cell from open list");
        openList.removeFirst();

       // Debug.toLog("[A*]Cells around root: ");
        /*for(Cell cell : cellsAround)
            if(cell != null)
                System.out.print(cell.position.toString()+" |");
        System.out.println();*/

        ArrayList<Cell> buffer = (ArrayList<Cell>) Arrays.stream(cellsAround).collect(Collectors.toList());

        buffer.removeIf(Objects::isNull);

        cellsAround = buffer.toArray(new Cell[0]);

        for(Cell cell : cellsAround){
            if((cell.isEmpty()
                    || cell.getFromCell().tag.equals("fog")
                    || cell.getFromCell() instanceof Player)
                    && closeList.stream().noneMatch(node -> node.nodeCell.position.equals(cell.position))){
                //Debug.toLog("[A*]Cell position "+cell.position+" is empty calculate F = G + H");
                //Debug.toLog("[A*]Cell root position "+(start.rootNode != null ? start.rootNode.nodeCell.position.toString() : "none"));

                int G = (start.nodeCell.position.x == cell.position.x || start.nodeCell.position.y == cell.position.y ? HORIZONTAL : DIAGONAL);
               // Debug.toLog("[A*]G = "+G);
                int H = manhattanLong(target, cell.position) * 10;
               // Debug.toLog("[A*]H = "+H);
                int F = G + H;
               // Debug.toLog("[A*]F = "+F);
                if(openList.stream().anyMatch(node -> node.nodeCell.position.equals(cell.position)))
                {
                    Node n = openList.stream().filter(node -> node.nodeCell.position.equals(cell.position)).findFirst().get();
                    if(n.G > G){
                        n.G = G;
                        n.rootNode = start;
                    }
                } else {
                    openList.add(new Node(cell, start, F, G, H));
                    if(targetNodeIsFind(target)){
                       // Debug.toLog("[A*]Target node is find!");
                        return;
                    }
                }
            }
        }

        //Debug.toLog("[A*]Open list F's" );
        // openList.forEach(node -> System.out.print(node.F + ", "));
        //System.out.println();

       // Debug.toLog("[A*]Find next cell with minimum F");
        try {
            Node nextNode = openList.stream().min(comparingF).get();
            /*Debug.toLog("[A*]Node F = "+nextNode.F);
            Debug.toLog("[A*]Next node position: "+nextNode.nodeCell.position);
            Debug.toLog("[A*]Next node root position: "+nextNode.rootNode.nodeCell.position);
            Debug.toLog("[A*]Add next node into close list");*/
            closeList.add(nextNode);
        } catch (NoSuchElementException e){
            Debug.toLog("[A*]Can't find element");
        }

    }

    /*       x y
        tPos(3,6)
        cPos(1,3)

      x   0123
      y 0 ....
        1 ....
        2 ....
        3 .c..
        4 ....
        5 ....
        6 ...t

        (yC != yT & xC != xT) -> yD = yT - yC, xD = xT - xC,
        yD = 3
        xD = 2

        mLong = yD + xD = 5;
     */

    private int manhattanLong(Position target, Position currentCell){
        int xT = target.x;
        int yT = target.y;

        int xC = currentCell.x;
        int yC = currentCell.y;

        int xD = 0;
        int yD = 0;

        if(xT != xC){
            xD = Math.abs(xT - xC);
        }

        if(yC != yT){
            yD = Math.abs(yT - yC);
        }

        return xD + yD;
    }

    Comparator<Node> comparingF = (Node n1, Node n2) -> {
        if(n1.F > n2.F){
            return 1;
        }
        else if(n2.F > n1.F){
            return -1;
        }
        else {
            return 0;
        }
    };

    private boolean targetNodeIsFind(Position target){
        return openList.stream().anyMatch(node -> node.nodeCell.position.equals(target));
    }

    private void getFinalPath(Node prevNode){
        if(prevNode.rootNode != null){
            getFinalPath(prevNode.rootNode);
        }
        finalPath.add(prevNode.nodeCell.position);
    }

    private static class Node {
        Cell nodeCell;
        Node rootNode;

        int F,G,H;

        public Node(Cell thisCell, Node rootNode, int F, int G, int H){
            this.nodeCell = thisCell;
            this.rootNode = rootNode;
            this.F = F;
            this.G = G;
            this.H = H;
        }
    }

}
