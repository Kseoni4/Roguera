package com.rogurea.dev.items;

import com.rogurea.dev.base.Debug;
import com.rogurea.dev.gamemap.Dungeon;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;

import java.util.concurrent.ThreadLocalRandom;

public class Potion extends Equipment implements Usable {

    private enum PotionType {
        HEAL (101, Colors.GREEN_BRIGHT),
        ATK_BUF (31, Colors.RED_BRIGHT),
        DEF_BUF (31, Colors.BLUE_BRIGHT),
        SCORE_BUF (6, Colors.VIOLET);

        private int bound;
        private String color;

        PotionType(int bound, String color){
            this.bound = bound;
            this.color = color;
        }
    }

    private int amount;

    private PotionType potionType;

    @Override
    public Integer getStats() {
        return amount;
    }

    public Potion(String name, Model model) {
        super(name, model, "buffer");
        constructPotionType();
        this.tag += ".potion."+this.potionType.name().toLowerCase();
        this.setSellPrice(ThreadLocalRandom.current().nextInt(1,50) + this.amount * (this.potionType.name().equals("SCORE_BUF") ? 100 : 1));
        Debug.toLog("Create potion: \n\t" +
                "Type: "+this.potionType.name()+"\n\t"+
                "Amount: "+this.amount+"\n\t"+
                "Price: "+this.getSellPrice());
    }

    private void constructPotionType(){
        int l = PotionType.values().length;
        this.potionType = PotionType.values()[ThreadLocalRandom.current().nextInt(l)];
        this.amount = ThreadLocalRandom.current().nextInt(1, this.potionType.bound);
        this.model.changeColor(this.potionType.color);
    }

    @Override
    public void use() {
        switch (this.potionType){
            case HEAL:
                Dungeon.player.getPlayerData().setHP(Math.min(100,Dungeon.player.getHP() + amount)); break;
            case ATK_BUF:
                Dungeon.player.getPlayerData().set_atk(Dungeon.player.getPlayerData().get_baseAtk() + amount); break;
            case DEF_BUF:
                Dungeon.player.getPlayerData().set_def(Dungeon.player.getPlayerData().get_def() + amount); break;
            case SCORE_BUF :
                Dungeon.player.getPlayerData().addScoreMultiplier(amount); break;
        }
    }

    public int getAmount() {
        return this.amount;
    }
}
