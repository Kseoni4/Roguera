package com.rogurea.main.items;

import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.Draw;

import static com.rogurea.main.resources.ViewObject.*;

public class InventoryController {

    public static String getPlace(Item item){
        if(item != null){
            if (item.getClass() == Weapon.class)
                return "FirstWeapon";
            if (item.getClass() == Armor.class)
                return "Armor";
        }
        return "";
    }

    public static void DropItem(Item dropingItem){

        if (Scans.CheckItems(Dungeon.CurrentRoom[Player.Pos.y+1][Player.Pos.x])) {
            logBlock.Event("There already lying something ");
            return;
        }

        Player.GetFromInventory(item -> dropingItem.id == item.id);

        Dungeon.GetCurrentRoom().RoomItems.add(dropingItem);

        Dungeon.CurrentRoom[Player.Pos.y+1][Player.Pos.x] = dropingItem._model;

        Draw.call(gameMapBlock);
    }

    public static void EquipItem(Equipment equipingItem, String place){

        if(Player.Equip.get(place) != null){
            SwitchItems(equipingItem, place);
        }

        Player.Inventory.remove(equipingItem);

        Player.Equip.put(place, equipingItem);

        logBlock.Action("equip the " + Colors.ORANGE + equipingItem.name);

        Draw.call(playerInfoBlock);
    }

    private static void SwitchItems(Equipment item , String place){

        Equipment currentEquip = Player.Equip.get(place);

        Player.Equip.remove(place);

        Player.Inventory.add(currentEquip);

        Player.Equip.put(place, item);
    }
}
