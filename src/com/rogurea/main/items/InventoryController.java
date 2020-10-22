package com.rogurea.main.items;

import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.InventoryMenu;
import com.rogurea.main.view.LogBlock;

public class InventoryController {

    public static void DropItem(Item dropingItem){

        if (Scans.CheckItems(Dungeon.CurrentRoom[Player.Pos.y+1][Player.Pos.x])) {
            LogBlock.Event("There already lying something ");
            return;
        }

        Player.GetFromInventory(item -> dropingItem.id == item.id);

        Dungeon.GetCurrentRoom(room -> Player.CurrentRoom == room.NumberOfRoom)
                .RoomItems.add(dropingItem);

        Dungeon.CurrentRoom[Player.Pos.y+1][Player.Pos.x] = dropingItem._model;
    }

    public static void EquipItem(Item equipingItem, String place){

        if(Player.Equip.get(place) != null){
            SwitchItems(equipingItem, place);
        }

        Player.Inventory.remove(equipingItem);

        Player.Equip.put(place, (Weapon) equipingItem);

        LogBlock.Action("equip the " + Colors.ORANGE + equipingItem.name);
    }

    private static void SwitchItems(Item item, String place){

        Item currentItem = Player.Equip.get(place);

        Player.Equip.remove(place);

        Player.Inventory.add(currentItem);

        Player.Equip.put(place, (Weapon) item);
    }

}
