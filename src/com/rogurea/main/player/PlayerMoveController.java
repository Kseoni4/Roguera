package com.rogurea.main.player;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.GameLoop;
import com.rogurea.main.gamelogic.Fight;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Gold;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
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

        char cell = MapEditor.getFromCell(y,x);

        Position pos = new Position();

        pos.setPosition(y,x);

        if(!Scans.CheckWall(cell)
/*
         || Scans.CheckCreature(cell)
*/
        || Scans.CheckProps(cell)){
            return;
        }

        if(Scans.CheckExit(cell)){

            MapEditor.clearCell(Player.Pos.y, Player.Pos.x);

            GameLoop.ChangeRoom(
                    Objects.requireNonNull(BaseGenerate.GetRoom(Dungeon.Direction.NEXT)).nextRoom
            );

            LogBlock.Action("enter the room");
            return;
        }

        if(Scans.CheckBack(cell))
        {
            Player.CurrentRoom--;

            MapEditor.clearCell(Player.Pos.y, Player.Pos.x);

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

            Item wp = rm.RoomItems.stream()
                    .filter(
                            item -> item._model == cell
                    )
                    .findFirst().orElse(null);

            rm.RoomItems.remove(wp);

            Player.PutInInventory(wp);
        }

        if(cell == '$'){

            Room rm = Objects.requireNonNull(Dungeon.Rooms.stream()
                    .filter(
                            room -> room.NumberOfRoom == Player.CurrentRoom
                    )
                    .findAny()
                    .orElse(null));

            Gold gold = ((Gold) rm.RoomItems.stream()
                    .filter(
                            item -> item._model == cell
                    ).findFirst().orElse(null));

            rm.RoomItems.remove(gold);

            Player.GetGold(gold);
        }

        if(Scans.CheckCreature(cell)){
            System.out.println(pos.y + " " + pos.x);
            Fight.HitMob(Dungeon.GetCurrentRoom().getMobFromRoom(pos));
            return;
        }
        MapEditor.clearCell(Player.Pos.y, Player.Pos.x);
        MapEditor.setIntoCell(Player.PlayerModel, y, x);
        Player.Pos.x = x;
        Player.Pos.y = y;
    }
}
