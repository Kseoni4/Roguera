/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.Main;
import com.rogurea.base.Debug;
import com.rogurea.creatures.Creature;
import com.rogurea.exceptions.NoSuchRoomException;
import com.rogurea.items.Chest;
import com.rogurea.mapgenerate.RoomGenerate;
import com.rogurea.view.*;
import com.rogurea.player.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.rogurea.view.ViewObjects.logView;

public class Dungeon {

    public static Player player;

    public static int DungeonRoomsCount = 10;

    public static int DungeonRooms = 10;

    public static Room savedRoom;

    public static ArrayList<Room> rooms = new ArrayList<>();

    public static ArrayList<Floor> floors = new ArrayList<>();

    public static int currentFloorNumber = 0;

    private static final Predicate<Room> ByFirstRoom = Room -> Room.roomNumber == 1;

    private static final Predicate<Room> ByPlayer = Room -> Room.roomNumber == player.getCurrentRoom();

    public static void generate(){
        Debug.toLog("[DUNGEON] Start generate rooms sequence");

        currentFloorNumber = 1;

        RoomGenerate.generateRoomSequence(0);

        RoomGenerate.generateRoomStructure(getRoom(room -> room.roomNumber == 1));

        PutPlayerOnMap(getRoom(ByFirstRoom),getRoom(ByFirstRoom).getTopCenterCellPosition().getRelative(0,1));

        ViewObjects.mapView.setRoom(getRoom(ByFirstRoom));

        getCurrentRoom().startMobAIThreads();
    }

    public static void reloadDungeonAfterLoad(){

        Debug.toLog("[DUNGEON] Reload instance...");

        PutPlayerOnMap(getCurrentRoom(), player.playerPosition);

        ViewObjects.mapView.setRoom(getCurrentRoom());

        getCurrentRoom().startMobAIThreads();

        /*currentFloorNumber = Dungeon.player.getPlayerData().getCurrentFloor().getFloorNumber();

        floors.add(0, Dungeon.player.getPlayerData().getCurrentFloor());

        Floor.setCounterFromLoad(currentFloorNumber);

        rooms = Dungeon.player.getPlayerData().getCurrentFloor().getRooms();

        PutPlayerOnMap(Dungeon.savedRoom, Dungeon.player.playerPosition);

        ViewObjects.mapView.setRoom(Dungeon.savedRoom);

        Dungeon.savedRoom.startMobAIThreads();*/
    }

    public static Room getRoom(Predicate<Room> RoomFilter){
        return rooms.stream().filter(RoomFilter).findFirst().orElse(null);
    }

    public static Room getCurrentRoom(){
        Optional<Room> room = rooms.stream().filter(ByPlayer).findFirst();
        return room.orElseGet(() -> rooms.stream().filter(Objects::nonNull).findFirst().get());
    }

    public static Optional<Floor> getCurrentFloor(){
        return floors.stream().filter(floor -> floor.getFloorNumber() == currentFloorNumber).findFirst();
    }

    public static void regenRoom() {

        getCurrentRoom().endMobAIThreads();

        getCurrentRoom().ClearCells();

        System.out.flush();

        RoomGenerate.generateRoomStructure(Objects.requireNonNull(Dungeon.rooms.stream().filter(
                room -> room.roomNumber == player.getCurrentRoom()
        ).findAny().orElse(null)));

        PutPlayerOnMap(Dungeon.getCurrentRoom(), new Position(3,3));

        ViewObjects.mapView.setRoom(getRoom(ByPlayer));

        getCurrentRoom().startMobAIThreads();

        TerminalView.reDrawAll(IViewBlock.empty);
        //BaseGenerate.PutPlayerInDungeon(((byte)(Dungeon.CurrentRoom[0].length/2)), (byte) 1, Dungeon.CurrentRoom);

        //gameLoop.RestartThread();
    }

    public static void PutPlayerOnMap(Room room, Position position){
        Debug.toLog("[Room "+room.roomNumber +"] put player on map");
        Debug.toLog("[Room "+room.roomNumber +"] put player on position "+position.toString());
        room.getCell(position).clear();
        room.getCell(position).putIntoCell(player);
    }

    public static void ChangeRoom(Room nextRoom) {

        Dungeon.getCurrentRoom().endMobAIThreads();

        if(nextRoom.isEndRoom){
            Debug.toLog(nextRoom.roomNumber + " is final room!");
        }

        Draw.reset(ViewObjects.mapView);

        Dungeon.getCurrentRoom().getCell(Dungeon.player.playerPosition).removeFromCell();

        //Debug.log("GAME: Change room");

        logView.playerAction("entered the room ".concat(String.valueOf(player.getCurrentRoom())));

        if (nextRoom.perimeter.isEmpty()) {
            try {
                RoomGenerate.generateRoomStructure(Objects.requireNonNull(nextRoom));
            } catch (NullPointerException e) {
                //Debug.log(Arrays.toString(e.getStackTrace()));
                e.getStackTrace();
                //MapEditor.setIntoCell(player, 1, 1);
            } finally {
                player.setCurrentRoom((byte) nextRoom.roomNumber);
                PutPlayerOnMap(nextRoom, nextRoom.getBackDoor().cellPosition.getRelative(0,1));
                logView.playerAction("have nothing to see here...");
            }
        }
        else {
            Position pos = player.getCurrentRoom() > nextRoom.roomNumber ?
                    nextRoom.getNextDoor().cellPosition.getRelative(0,-1) : nextRoom.getBackDoor().cellPosition.getRelative(0,1);
            player.setCurrentRoom((byte) nextRoom.roomNumber);
            PutPlayerOnMap(nextRoom, pos);
        }
        ViewObjects.mapView.setRoom(getRoom(ByPlayer));

        Draw.call(ViewObjects.mapView);

        Draw.call(ViewObjects.infoGrid.getFirstBlock());

        nextRoom.startMobAIThreads();
    }

    public static void changeFloor(){
        Dungeon.getCurrentRoom().endMobAIThreads();

        Dungeon.getCurrentRoom().getCell(Dungeon.player.playerPosition).removeFromCell();

        Draw.clear();

        currentFloorNumber++;

        RoomGenerate.generateRoomSequence(currentFloorNumber-1);

        RoomGenerate.generateRoomStructure(getRoom(room -> room.roomNumber == 1));

        player.setCurrentRoom((byte) getRoom(ByFirstRoom).roomNumber);

        PutPlayerOnMap(getRoom(ByFirstRoom), getRoom(ByFirstRoom).getTopCenterCellPosition().getRelative(0,1));

        ViewObjects.mapView.setRoom(getRoom(ByPlayer));

        TerminalView.reDrawAll(IViewBlock.empty);

        logView.playerAction("entered the next floor!");

        getRoom(ByFirstRoom).startMobAIThreads();
    }

    public static void resetVariables(){
        currentFloorNumber = 0;
        DungeonRooms = 10;
        DungeonRoomsCount = 10;
        rooms = new ArrayList<>();
        floors = new ArrayList<>();
    }
}
