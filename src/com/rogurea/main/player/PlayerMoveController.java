package com.rogurea.main.player;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.GameLoop;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Weapon;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Room;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.view.LogBlock;

import java.util.Objects;

public class PlayerMoveController {

    public static void MovePlayer(KeyType key) {
        switch (key) {
            case ArrowUp -> Move(Player.Pos.y + 1, Player.Pos.x);
            case ArrowLeft -> Move(Player.Pos.y, Player.Pos.x-1);
            case ArrowDown -> Move(Player.Pos.y - 1, Player.Pos.x);
            case ArrowRight -> Move(Player.Pos.y, Player.Pos.x + 1);
        }
    }
    public static void Move(int y, int x){

        char cell = Dungeon.CurrentRoom[y][x];

        if(!Scans.CheckWall(cell)
         || Scans.CheckCreature(cell)
        || Scans.CheckProps(cell)){
            return;
        }

        if(Scans.CheckExit(cell)){

            Dungeon.CurrentRoom[Player.Pos.y][Player.Pos.x] = MapEditor.EmptyCell;
            GameLoop.ChangeRoom(
                    Objects.requireNonNull(BaseGenerate.GetRoom(Dungeon.Direction.NEXT)).nextRoom
            );
            LogBlock.Action("enter the room");
            return;
        }

        if(Scans.CheckBack(cell))
        {
            Player.CurrentRoom--;

            Dungeon.CurrentRoom[Player.Pos.y][Player.Pos.x] = MapEditor.EmptyCell;

            GameLoop.ChangeRoom(
                    Objects.requireNonNull(BaseGenerate.GetRoom(Dungeon.Direction.BACK))
            );
            return;
        }

        if(Scans.CheckItems(cell)){
            if(Player.Inventory.size() >= 10){
                LogBlock.Event("Your inventory is full!");
                return;
            }
            Room rm = Objects.requireNonNull(Dungeon.Rooms.stream()
                    .filter(
                            room -> room.NumberOfRoom == Player.CurrentRoom
                    )
                    .findAny()
                    .orElse(null));
            Weapon wp = (Weapon) rm.RoomItems.stream()
                    .filter(
                            item -> item._model == cell
                    )
                    .findFirst().orElse(null);
            rm.RoomItems.remove(wp);
            Player.PutInInventory(wp);
        }
        Dungeon.CurrentRoom[Player.Pos.y][Player.Pos.x] = MapEditor.EmptyCell;
        Dungeon.CurrentRoom[y][x] = Player.PlayerModel;
        Player.Pos.x = x;
        Player.Pos.y = y;
    }
}
