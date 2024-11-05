package kseoni.ch.roguera.map.generator;

import com.googlecode.lanterna.TextColor;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.game.entity.Door;
import kseoni.ch.roguera.graphics.render.TGLayer;
import kseoni.ch.roguera.graphics.render.Window;
import kseoni.ch.roguera.graphics.sprites.TextSprite;
import kseoni.ch.roguera.map.Cell;
import kseoni.ch.roguera.map.Room;
import kseoni.ch.roguera.utils.Convert;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

class PhaseThreeConnectClusters {

    private Map<Integer, Room> rooms;

    private Map<Double, Set<Room>> distancesRooms = new HashMap<>();

    public PhaseThreeConnectClusters(Map<Integer, Room> rooms){
        this.rooms = rooms;
    }

    @SneakyThrows
    public void connectRooms(){

        System.out.println("Connecting rooms");

        calculateDistances();

        for(Map.Entry entry : distancesRooms.entrySet()){
            System.out.println("Rooms: "+entry.getValue());
            System.out.println("Distance between: "+entry.getKey());
        }

        while (!distancesRooms.isEmpty()) {
            double minDistance = distancesRooms.keySet().stream().min(Comparator.comparing(Double::doubleValue)).get();
            Set<Room> roomSet = distancesRooms.remove(minDistance);
            System.out.println("Closest rooms: " + roomSet);
            System.out.println("Distance: " + minDistance);

            Iterator<Room> it = roomSet.iterator();
            while (it.hasNext()) {

                Room first = it.next();
                Room second = it.next();

                System.out.println(first.getRoomCenter());
                System.out.println(second.getRoomCenter());

                Position globalCenterFirst = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter());
                Position globalCenterSecond = Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter());

                System.out.println(globalCenterFirst);
                System.out.println(globalCenterSecond);

                System.out.println(first.getRoomLeftTopPosition() + " " + first.getRoomLeftTopPosition().getRelativePosition(first.getWidth(), 0));
                System.out.println(first.getRoomLeftTopPosition() + " " + first.getRoomLeftTopPosition().getRelativePosition(0, first.getHeight()));

                if (globalCenterSecond.isInBetweenY(first.getRoomLeftTopPosition(), first.getRoomLeftTopPosition().getRelativePosition(0, first.getHeight()))) {
                    System.out.println("Between Y");
                    if (globalCenterSecond.getX() > globalCenterFirst.getX()) {
                        int x = 0;
                        Position rightEdge;

                        for (int i = first.getRoomCenter().getX(); i >= first.getWidth(); i++) {
                            rightEdge = new Position(i, first.getRoomCenter().getY());
                            if (first.getCell(rightEdge) == null) {
                                x = i - 1;
                                break;
                            }
                        }

                        rightEdge = new Position(x, first.getRoomCenter().getY());
                        System.out.println("Position right edge " + rightEdge);
                        first.getCell(rightEdge).placeObject(new Door(new TextSprite('|'), second.getRoomId()));
                    } else {
                        Position leftEdge = new Position(0, first.getRoomCenter().getY());
                        System.out.println("Position left edge " + leftEdge);
                        first.getCell(leftEdge).placeObject(new Door(new TextSprite('|'), second.getRoomId()));
                    }
                    continue;
                }

                if (globalCenterSecond.isInBetweenX(first.getRoomLeftTopPosition(), first.getRoomLeftTopPosition().getRelativePosition(first.getWidth(), 0))) {
                    System.out.println("Between X");
                    if (globalCenterSecond.getY() > globalCenterFirst.getY()) {
                        first.getCell(new Position(first.getRoomCenter().getX(), 0)).placeObject(new Door(new TextSprite('-'), second.getRoomId()));
                    } else {
                        int y = 0;

                        for (int i = first.getRoomCenter().getY(); i >= first.getHeight(); i++) {
                            if (first.getCell(new Position(first.getRoomCenter().getX(), i)) == null) {
                                y = i - 1;
                                break;
                            }
                        }

                        System.out.println(new Position(first.getRoomCenter().getX(), y));
                        first.getCell(new Position(first.getRoomCenter().getX(), y)).placeObject(new Door(new TextSprite('-'), second.getRoomId()));
                    }
                    continue;
                }

                int x = 0;
                if (globalCenterSecond.getX() > globalCenterFirst.getX()) {

                    for (int i = first.getRoomCenter().getX(); i <= first.getWidth(); i++) {
                        if (first.getCell(new Position(i, first.getRoomCenter().getY())) == null) {
                            x = i - 1;
                            break;
                        }
                    }

                } else {

                    for (int i = first.getRoomCenter().getX(); i >= 0; i--) {
                        if (first.getCell(new Position(i, first.getRoomCenter().getY())) == null) {
                            x = i + 1;
                            break;
                        }
                    }

                }
                first.getCell(new Position(x, first.getRoomCenter().getY())).placeObject(new Door(new TextSprite('|'), second.getRoomId()));
            }
        }
    }

    private void calculateDistances(){

        for(Room first : rooms.values()){
            for(Room second : rooms.values()){
                if(first != second){
                    if(!distancesRooms.containsValue(Set.of(first.getRoomId(), second.getRoomId()))){
                        System.out.println(first);
                        System.out.println(second);
                        double distance = Convert.toGlobalPosition(first.getRoomLeftTopPosition(), first.getRoomCenter())
                                .getDistance(Convert.toGlobalPosition(second.getRoomLeftTopPosition(), second.getRoomCenter()));
                        distancesRooms.put(distance, new HashSet<>(Arrays.asList(first, second)));
                    }
                }
            }
        }
    }
}
