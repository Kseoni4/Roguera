package com.rogurea.main.items;

import com.rogurea.main.gamelogic.Scans;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.Colors;
import com.rogurea.main.view.Draw;

import static com.rogurea.main.view.ViewObjects.*;

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

        dropingItem.ItemPosition.setPosition(Player.Pos.y+1, Player.Pos.x);

        MapEditor.setIntoCell(dropingItem._model, Player.GetPlayerPosition().getRelative(0,1));

        Dungeon.GetCurrentRoom().RoomItems.add(dropingItem);

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

    public static void UseItem(Usable usableItem){
        usableItem.use();
    }

    private static void SwitchItems(Equipment item , String place){

        Equipment currentEquip = Player.Equip.get(place);

        Player.Equip.remove(place);

        Player.Inventory.add(currentEquip);

        Player.Equip.put(place, item);
    }
}
