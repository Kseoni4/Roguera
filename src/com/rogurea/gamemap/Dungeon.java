/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.base.Debug;
import com.rogurea.creatures.Creature;
import com.rogurea.items.Chest;
import com.rogurea.mapgenerate.RoomGenerate;
import com.rogurea.view.*;
import com.rogurea.player.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import static com.rogurea.view.ViewObjects.logView;

public class Dungeon {

    public static Player player;

    public static int DungeonRoomsCount = 10;

    public static int DungeonRooms = 10;

    public static Room savedRoom;

    public static ArrayList<Room> rooms = new ArrayList<>();

    public static ArrayList<Floor> floors = new ArrayList<>();

    private static int currentFloorNumber = 0;

    private static final Predicate<Room> ByFirstRoom = Room -> Room.roomNumber == 1;
    private static final Predicate<Room> ByPlayer = Room -> Room.roomNumber == player.getCurrentRoom();

    public static void generate(){
        Debug.toLog("[Generate] Start generate sequence");
        currentFloorNumber = 1;
        RoomGenerate.generateRoomSequence(0);
        RoomGenerate.generateRoomStructure(getRoom(room -> room.roomNumber == 1));
        PutPlayerOnMap(getRoom(ByFirstRoom), new Position(3,3));
        ViewObjects.mapView.setRoom(getRoom(ByFirstRoom));
        getCurrentRoom().startMobAIThreads();
    }

    public static void loadDungeonFromSave(){
        currentFloorNumber = Dungeon.player.getPlayerData().getCurrentFloor().getFloorNumber();
        floors.add(0, Dungeon.player.getPlayerData().getCurrentFloor());
        rooms = Dungeon.player.getPlayerData().getCurrentFloor().getRooms();
        Dungeon.player.Equipment.values().forEach(equipment -> equipment.model.reloadModel());
        Dungeon.player.Inventory.forEach(equipment -> equipment.model.reloadModel());
        PutPlayerOnMap(Dungeon.savedRoom, Dungeon.player.playerPosition);
        ViewObjects.mapView.setRoom(Dungeon.savedRoom);
        floors.forEach(floor -> floor.getRooms()
                .forEach(room -> {
                    room.gameObjects.forEach(gameObject -> gameObject.model.reloadModel());
                    room.initFogController();
                }));
        Dungeon.savedRoom.cells.forEach(cell -> cell.getFromCell().model.reloadModel());
        Dungeon.savedRoom.cells.stream().filter(cell -> cell.getFromCell() instanceof Chest).forEach(cell -> ((Chest) cell.getFromCell()).reload());
        Dungeon.savedRoom.cells.stream().filter(cell -> cell.getFromCell() instanceof Creature).forEach(cell -> ((Creature) cell.getFromCell()).reloadInventory());
        Dungeon.savedRoom.startMobAIThreads();
    }

    public static Room getRoom(Predicate<Room> RoomFilter){
        return rooms.stream().filter(RoomFilter).findFirst().orElse(null);
    }

    public static Room getCurrentRoom(){
        return rooms.stream().filter(ByPlayer).findFirst().orElse(null);
    }

    public static Floor getCurrentFloor(){
        return floors.stream().filter(floor -> floor.getFloorNumber() == currentFloorNumber).findFirst().orElse(null);
    }

    public static void RegenRoom() {

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
                    nextRoom.getNextDoor().cellPosition.getRelative(0,-1) : nextRoom.getTopCenterCellPosition().getRelative(0,1);
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
}