package com.rogurea.main;

import java.util.ArrayList;

public class DungeonShop {

    private int TraderMoney;

    private final ArrayList<Item> Goods = new ArrayList<>();

    public void getGoods(Item item){
        this.Goods.add(item);
    }

    public int getTraderMoney(){
        return this.TraderMoney;
    }

    public void changeTraderMoney(int PlayerMoney){
        TraderMoney += PlayerMoney;
    }

    public Item sellGood(int money, int idItem){
        Item itemForSell;

        itemForSell = Goods.stream()
                .filter(item -> item.id == idItem)
                .findAny()
                .orElse(null);

        assert itemForSell != null;

            if(money == itemForSell.SellPrice) {
                changeTraderMoney(money);
                Goods.remove(itemForSell);
                return itemForSell;
            }
            else
                return null;
    }

    public DungeonShop(){
        TraderMoney = 100;
    }
}
