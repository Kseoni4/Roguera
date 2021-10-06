/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.mapgenerate.RoomGenerate;
import com.rogurea.dev.view.*;
import com.rogurea.dev.player.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

import static com.rogurea.dev.view.ViewObjects.logView;

public class Dungeon {

    public static Player player;

    public static int DungeonRoomsCount = 0;

    public static int DungeonRooms = 10;

    public static Room savedRoom;

    public static ArrayList<Room> rooms = new ArrayList<>();

    private static final Predicate<Room> ByFirstRoom = Room -> Room.RoomNumber == 1;
    private static final Predicate<Room> ByPlayer = Room -> Room.RoomNumber == player.getCurrentRoom();

    public static void Generate(){
        player = new Player();
        RoomGenerate.GenerateRoomSequence(DungeonRooms);
        RoomGenerate.GenerateRoomStructure(GetRoom(room -> room.RoomNumber == 1));
        PutPlayerOnMap(GetRoom(ByFirstRoom), new Position(3,3));
        ViewObjects.mapView.setRoom(GetRoom(ByFirstRoom));
    }

    public static Room GetRoom(Predicate<Room> RoomFilter){
        return rooms.stream().filter(RoomFilter).findFirst().orElse(null);
    }

    public static Room getCurrentRoom(){
        return rooms.stream().filter(ByPlayer).findFirst().orElse(null);
    }

    public static void RegenRoom() {

        System.out.flush();
        RoomGenerate.GenerateRoomStructure(Objects.requireNonNull(Dungeon.rooms.stream().filter(
                room -> room.RoomNumber == player.getCurrentRoom()
        ).findAny().orElse(null)));
        PutPlayerOnMap(Dungeon.getCurrentRoom(), new Position(3,3));

        ViewObjects.mapView.setRoom(GetRoom(ByPlayer));

        TerminalView.reDrawAll(IViewBlock.empty);
        //BaseGenerate.PutPlayerInDungeon(((byte)(Dungeon.CurrentRoom[0].length/2)), (byte) 1, Dungeon.CurrentRoom);

        //gameLoop.RestartThread();
    }

    public static void PutPlayerOnMap(Room room, Position position){
        room.getCell(position).clear();
        room.getCell(position).putIntoCell(player);
    }

    public static void ChangeRoom(Room nextRoom) {

        if(nextRoom.isEndRoom){
            Debug.toLog(nextRoom.RoomNumber + " is final room!");
        }

        Draw.reset(ViewObjects.mapView);

        Dungeon.getCurrentRoom().getCell(Dungeon.player.playerPosition).removeFromCell();

        //Debug.log("GAME: Change room");

        logView.playerAction("entered the room ".concat(String.valueOf(player.getCurrentRoom())));

        if (nextRoom.Perimeter.isEmpty()) {
            try {
                RoomGenerate.GenerateRoomStructure(Objects.requireNonNull(nextRoom));
            } catch (NullPointerException e) {
                //Debug.log(Arrays.toString(e.getStackTrace()));
                e.getStackTrace();
                //MapEditor.setIntoCell(player, 1, 1);
            } finally {
                player.setCurrentRoom((byte) nextRoom.RoomNumber);
                PutPlayerOnMap(nextRoom, nextRoom.getBackDoor().cellPosition.getRelative(0,1));
                logView.playerAction("have nothing to see here...");
            }
        }
        else {
            Position pos = player.getCurrentRoom() > nextRoom.RoomNumber ?
                    nextRoom.getNextDoor().cellPosition.getRelative(0,-1) : nextRoom.getTopCenterCellPosition().getRelative(0,1);
            player.setCurrentRoom((byte) nextRoom.RoomNumber);
            PutPlayerOnMap(nextRoom, pos);
        }
        ViewObjects.mapView.setRoom(GetRoom(ByPlayer));

        Draw.call(ViewObjects.mapView);
        Draw.call(ViewObjects.infoGrid.getFirstBlock());

        //Draw.call(ViewObjects.mapView);
        //Debug.log("THREADS: Restart mob threads");
        //gameLoop.RestartThread();
    }

    /*public enum Direction {
        NEXT,
        BACK,
        FIRST
    }

    static final byte Height = 1;

    static final byte Widght = 1;

    public static byte CurrentDungeonLenght = 0;

    public static final byte DungeonLenght = 10;

    public static Room_old savedRoomOld;

    public static ArrayList<Room_old> roomOlds = new ArrayList<>();

    public static char[][] CurrentRoom = new char[Height][Widght];

    public static void Generate() {

        BaseGenerate.GenerateDungeon(DungeonLenght);
        if(NewGame) {
            BaseGenerate.GenerateRoom(Objects.requireNonNull(BaseGenerate.GetRoom(Direction.FIRST)));
            BaseGenerate.PutPlayerInDungeon((byte) (CurrentRoom[0].length / 2), (byte) 1, Dungeon.CurrentRoom);
        }
    }

    public static void NextGenerate(){
        CleanRooms();

        gameLoop.RestartThread();

        BaseGenerate.GenerateDungeon(DungeonLenght);

        Player.CurrentRoom++;

        BaseGenerate.GenerateRoom(BaseGenerate.GetFromSet(room -> room.NumberOfRoom == ((CurrentDungeonLenght-DungeonLenght)+1)));

        BaseGenerate.PutPlayerInDungeon((byte) (CurrentRoom[0].length / 2), (byte) 1, Dungeon.CurrentRoom);
    }

    private static void CleanRooms(){
        Debug.log("GENERATE: Clean previous rooms");
        Dungeon.roomOlds.forEach(room -> {
            Debug.log("GENERATE: Clean room " + room.NumberOfRoom);
            Debug.log("CLEAN: Removing all creatures");
            room.RoomCreatures.removeAll(room.RoomCreatures);
            Debug.log("CLEAN: Removing structures");
            room.RoomStructure = null;
            Debug.log("CLEAN: Room items");
            room.RoomItems.removeAll(room.RoomItems);
        });
        Debug.log("GENERATE: Rooms cleaned");
    }

    public static void ChangeRoom(Room_old roomOld) {

        Debug.log("GAME: Change room");

        if (!roomOld.IsRoomStructureGenerate) {
            try {
                BaseGenerate.GenerateRoom(Objects.requireNonNull(roomOld));
            } catch (NullPointerException e) {
                Debug.log(Arrays.toString(e.getStackTrace()));
                e.getStackTrace();
                MapEditor.setIntoCell(PlayerModel, 1, 1);
            } finally {
                BaseGenerate.PutPlayerInDungeon(
                        BaseGenerate.GetCenterOfRoom(roomOld), (byte) 1,
                        Dungeon.CurrentRoom);
                }
            }
        else {
            Player.CurrentRoom = roomOld.NumberOfRoom;
            Dungeon.CurrentRoom = roomOld.RoomStructure;
            BaseGenerate.PutPlayerInDungeon(BaseGenerate.GetCenterOfRoom(roomOld), (byte) 1,
                    roomOld.RoomStructure);
            }
        Debug.log("THREADS: Restart mob threads");
        gameLoop.RestartThread();
    }



    public static Room_old GetCurrentRoom(){
        return roomOlds.stream().filter(
                room -> room.NumberOfRoom == Player.CurrentRoom
        ).findAny().orElse(null);
    }*/
}
