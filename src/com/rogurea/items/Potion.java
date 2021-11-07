package com.rogurea.items;

import com.rogurea.base.Debug;
import com.rogurea.gamelogic.RogueraGameSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;

import java.util.concurrent.ThreadLocalRandom;

import static com.rogurea.view.ViewObjects.infoGrid;

public class Potion extends Equipment implements Usable {

    private enum PotionType {
        HEAL (101, Colors.GREEN_BRIGHT),
        ATK_BUF (RogueraGameSystem.getPBonus(), Colors.RED_BRIGHT),
        DEF_BUF (RogueraGameSystem.getPBonus()+1, Colors.BLUE_BRIGHT),
        SCORE_BUF (RogueraGameSystem.getPScoreBonus(), Colors.VIOLET);

        private final int bound;
        private final String color;

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
        this.setSellPrice(ThreadLocalRandom.current().nextInt(1,50) + this.amount * (this.potionType.name().equals("SCORE_BUF") ? 500 : 1));
        this.rename(getName() + this.potionType.name());
        Debug.toLog("[POTION]"+getName() +" color: "+ model.getModelColor()+"#");
/*
   Debug.toLog("Create potion: \n\t" +
                "Type: "+this.potionType.name()+"\n\t"+
                "Amount: "+this.amount+"\n\t"+
                "Price: "+this.getSellPrice());*/
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
                Dungeon.player.getPlayerData().setHP(Math.min(Dungeon.player.getPlayerData().getMaxHP(),Dungeon.player.getHP() + amount)); break;
            case ATK_BUF:
                Dungeon.player.getPlayerData().set_atkPotionBonus(Dungeon.player.getPlayerData().get_atkPotionBonus() + amount); break;
            case DEF_BUF:
                Dungeon.player.getPlayerData().set_defPotionBonus(Dungeon.player.getPlayerData().get_defPotionBonus() + amount); break;
            case SCORE_BUF :
                Dungeon.player.getPlayerData().addScoreMultiplier(amount); break;
        }
        Draw.call(infoGrid.getFirstBlock());
        Draw.call(infoGrid.getThirdBlock());
    }

    public int getAmount() {
        return this.amount;
    }
}
