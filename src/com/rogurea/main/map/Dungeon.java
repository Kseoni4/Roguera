package com.rogurea.main.map;
import com.rogurea.main.gamelogic.Debug;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;

import java.util.*;

import static com.rogurea.Main.NewGame;
import static com.rogurea.Main.gameLoop;
import static com.rogurea.main.player.Player.PlayerModel;

public class Dungeon {

    public enum Direction {
        NEXT,
        BACK,
        FIRST
    }

    static final byte Height = 1;

    static final byte Widght = 1;

    public static byte CurrentDungeonLenght = 0;

    public static final byte DungeonLenght = 10;

    public static Room SavedRoom;

    public static ArrayList<Room> Rooms = new ArrayList<>();

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
        Dungeon.Rooms.forEach(room -> {
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

    public static void ChangeRoom(Room room) {

        Debug.log("GAME: Change room");

        if (!room.IsRoomStructureGenerate) {
            try {
                BaseGenerate.GenerateRoom(Objects.requireNonNull(room));
            } catch (NullPointerException e) {
                Debug.log(Arrays.toString(e.getStackTrace()));
                e.getStackTrace();
                MapEditor.setIntoCell(PlayerModel, 1, 1);
            } finally {
                BaseGenerate.PutPlayerInDungeon(
                        BaseGenerate.GetCenterOfRoom(room), (byte) 1,
                        Dungeon.CurrentRoom);
                }
            }
        else {
            Player.CurrentRoom = room.NumberOfRoom;
            Dungeon.CurrentRoom = room.RoomStructure;
            BaseGenerate.PutPlayerInDungeon(BaseGenerate.GetCenterOfRoom(room), (byte) 1,
                    room.RoomStructure);
            }
        Debug.log("THREADS: Restart mob threads");
        gameLoop.RestartThread();
    }

    public static void RegenRoom() {

        System.out.flush();
        BaseGenerate.GenerateRoom(Objects.requireNonNull(Dungeon.Rooms.stream().filter(
                room -> room.NumberOfRoom == Player.CurrentRoom
        ).findAny().orElse(null)));
        BaseGenerate.PutPlayerInDungeon(((byte)(Dungeon.CurrentRoom[0].length/2)), (byte) 1, Dungeon.CurrentRoom);

        gameLoop.RestartThread();
    }

    public static Room GetCurrentRoom(){
        return Rooms.stream().filter(
                room -> room.NumberOfRoom == Player.CurrentRoom
        ).findAny().orElse(null);
    }
}
