package com.rogurea.main.map;

import com.rogurea.main.creatures.NPC;
import com.rogurea.main.items.Item;
import com.rogurea.main.mapgenerate.MapEditor;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;
import com.rogurea.main.view.UI.Menu.Message;

import java.io.Serializable;

import static com.rogurea.main.view.ViewObjects.shopMenu;

public class Shop implements Serializable {

    private int ID;

    private static int IDCounter = 1;

    private final int DefaultGoldAmount = 100;

    //private ArrayList<Item> Goods;

    private char[][] ShopStructure;

    public NPC shopOwner;

    public Position ShopPosition;

    public Shop(Position shopPosition){
        this.ID = IDCounter;

        IDCounter++;

        this.shopOwner = new NPC("Trader", DefaultGoldAmount);

        this.shopOwner.setNPCAction(()-> {
            if(Dungeon.GetCurrentRoom().RoomCreatures.size() > 0){
                new Message("You need to kill all mobs in the room at first", new Position(10,10));
            }else {
                shopMenu.setRoomShop(this);
                shopMenu.show();
            }
        });
        this.ShopPosition = shopPosition;

        ShopStructure = DrawShopStructure();

        this.shopOwner.setMobPosition(ShopPosition.x+1, ShopPosition.y+1);
    }

    public Shop(int x, int y){
        this(new Position(x,y));
    }

    public char[][] getShopStructure(){
        return this.ShopStructure;
    }

    public int SellItem(int playerItemId){
        Item sellingItem = Player.GetFromInventory(item -> item.id == playerItemId);
        return sellingItem.SellPrice;
    }

    private char[][] DrawShopStructure(){
        char[][] bufferedStructure = MapEditor.DrawRectangle(3,3);

        bufferedStructure[0][1] = GameResources.SWall;
        bufferedStructure[1][2] = GameResources.EmptyCell;
        bufferedStructure[1][1] = shopOwner.getCreatureSymbol();
        bufferedStructure[2][1] = GameResources.SWall;

        return bufferedStructure;
    }
}
