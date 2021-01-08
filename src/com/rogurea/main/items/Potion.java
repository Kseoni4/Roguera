package com.rogurea.main.items;

import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GameResources;

public class Potion extends Item implements Usable {

    public enum PotionType {
        HEAL {
            public String getType(){
                return "Healing potion";
            }
        },
        BUF_ATK {
            @Override
            public String getType() {
                return "Attack buff potion";
            }
        },
        BUF_DEF {
            @Override
            public String getType() {
                return "Defence buff potion";
            }
        };
        public int points;
        public abstract String getType();
    }

    public int PotionLevel = 1;

    private final PotionType potionType;

    public Potion(int SellPrice, char model, PotionType potionType) {
        super(potionType.getType(), SellPrice, model);

        this.potionType = potionType;
        potionType.points = Formula.GetPotionPointsByType(potionType, PotionLevel);
    }

    public String GetPotionType(){
        return potionType.getType();
    }

    public int GetPotionPointsEffect(){
        return potionType.points;
    }

    @Override
    public String getMaterialColor() {
        switch (this.potionType){
            case HEAL -> {
                return GameResources.MaterialColor.get("HEAL");
            }
            case BUF_ATK -> {
                return GameResources.MaterialColor.get("BUF_ATK");
            }
            case BUF_DEF -> {
                return GameResources.MaterialColor.get("BUF_DEF");
            }
        }
        return super.getMaterialColor();
    }

    @Override
    public void use() {
        getEffectByType();
    }

    private void getEffectByType(){
        switch (potionType){
            case HEAL    -> Player.HP   = (short) Math.min(potionType.points + Player.HP, 100);
            case BUF_ATK -> Player.ATK  += potionType.points;
            case BUF_DEF -> Player.DEF  += potionType.points;
        }
        Player.playerStatistics.DrinkedPotions += 1;
    }
}
