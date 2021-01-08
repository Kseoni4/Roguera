package com.rogurea.main.player;

import com.googlecode.lanterna.input.KeyType;
import com.rogurea.main.gamelogic.Fight;
import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.items.Gold;
import com.rogurea.main.items.Item;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.map.Room;
import com.rogurea.main.mapgenerate.BaseGenerate;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.view.Draw;

import java.util.Objects;

import static com.rogurea.main.view.ViewObjects.*;

public class PlayerMoveController {

    private static final Position pos = new Position();

    public static void MovePlayer(KeyType key) {
        switch (key) {
            case ArrowUp -> Move(Player.Pos.y + 1, Player.Pos.x);
            case ArrowLeft -> Move(Player.Pos.y, Player.Pos.x-1);
            case ArrowDown -> Move(Player.Pos.y - 1, Player.Pos.x);
            case ArrowRight -> Move(Player.Pos.y, Player.Pos.x + 1);
        }
    }
    public static void Move(int NewY, int NewX){

        char cell = MapEditor.getFromCell(NewY,NewX);

        pos.setPosition(NewY,NewX);

        if(!Scans.CheckWall(cell)
        /*|| Scans.CheckProps(cell)*/){
            return;
        }

        if(Scans.CheckExit(cell)){

            MapEditor.clearCell(Player.Pos.y, Player.Pos.x);

            Dungeon.ChangeRoom(
                    Objects.requireNonNull(BaseGenerate.GetRoom(Dungeon.Direction.NEXT)).nextRoom
            );

            logBlock.Action("enter the room");
            return;
        }

        if(Scans.CheckBack(cell))
        {
            Player.CurrentRoom--;

            MapEditor.clearCell(Player.Pos.y, Player.Pos.x);

            Dungeon.ChangeRoom(
                    Objects.requireNonNull(BaseGenerate.GetRoom(Dungeon.Direction.BACK))
            );
            return;
        }

        if(Scans.CheckItems(cell)){
            if(Player.Inventory.size() >= 10){
                logBlock.Event("Your inventory is full!");
                return;
            }

            Room rm = Dungeon.GetCurrentRoom();

            Item ItemForTake = rm.RoomItems.stream()
                    .filter(
                            item -> item.ItemPosition.equals(pos)
                    )
                    .findFirst().orElse(null);

            Dungeon.GetCurrentRoom().RoomItems.remove(ItemForTake);

            if(ItemForTake instanceof Gold){
                Player.GetGold((Gold) ItemForTake);
            }else{
                Player.PutInInventory(ItemForTake);
            }
        }

        if(Scans.CheckCreature(cell)){
            Fight.HitMobByPlayer(Dungeon.GetCurrentRoom().getMobFromRoom(pos));
            return;
        }
        MapEditor.clearCell(Player.Pos.y, Player.Pos.x);
        MapEditor.setIntoCell(Player.PlayerModel, NewY, NewX);
        Player.Pos.x = (byte) NewX;
        Player.Pos.y = (byte) NewY;
        Draw.call(gameMapBlock);
        Draw.call(playerInfoBlock);
    }
}
