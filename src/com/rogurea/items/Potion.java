package com.rogurea.items;

import com.rogurea.base.Debug;
import com.rogurea.gamelogic.RogueraGameSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GetRandom;
import com.rogurea.resources.Model;
import com.rogurea.view.Draw;
import com.rogurea.workers.PotionEffectWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static com.rogurea.view.ViewObjects.infoGrid;

public class Potion extends Equipment implements Usable {

    public static ExecutorService effectWrks = Executors.newCachedThreadPool();

    private enum PotionType {
        HEAL (101, Colors.GREEN_PASTEL),
        ATK_BUF (RogueraGameSystem.getPBonus(), Colors.RED_PASTEL),
        DEF_BUF (RogueraGameSystem.getPBonus()+1, Colors.BLUE_PASTEL),
        SCORE_BUF (RogueraGameSystem.getPScoreBonus(), Colors.VIOLET_PASTEL);

        private final int bound;
        private final String color;

        PotionType(int bound, String color){
            this.bound = bound;
            this.color = color;
        }
    }

    private int amount;

    private PotionType potionType;

    private int effectTimer = 0;

    public void setPotionType(PotionType potionType) {
        this.potionType = potionType;
    }

    public int getEffectTimer() {
        return effectTimer;
    }

    public void setEffectTimer(int effectTimer) {
        this.effectTimer = effectTimer;
    }

    public PotionType getPotionType() {
        return this.potionType;
    }

    @Override
    public Integer getStats() {
        return amount;
    }

    public Potion(String name, Model model) {
        super(name, model, "buffer");
        constructPotionType();
        this.tag += ".potion."+this.potionType.name().toLowerCase();
        this.setSellPrice(
                (int) (RogueraGameSystem.getPBonus()*Math.pow(Dungeon.getCurrentFloor().get().getFloorNumber(),Math.E)
                                        + (this.amount * (this.potionType.name().equals("SCORE_BUF") ? 150 : ThreadLocalRandom.current().nextInt(2,5)
                        ))));
        this.rename(getName() + " " +this.potionType.name().substring(0,3));
    }

    private void constructPotionType(){
        int l = PotionType.values().length;
        this.potionType = PotionType.values()[GetRandom.getRandomPotionTypeIndex()];
        int min = (int) Math.round(RogueraGameSystem.getPBonus()*Math.sqrt(Math.E/5)-1);
        int max = this.potionType.bound * Dungeon.getCurrentFloor().get().getFloorNumber();
/*        Debug.toLog("[POTION] Minimum amount = " + min);
        Debug.toLog("[POTION] Maximum amount = " + max);*/
        this.amount = ThreadLocalRandom.current().nextInt(min, max);
        this.model.changeColor(this.potionType.color);
    }

    @Override
    public void use() {
        effectWrks.execute(new PotionEffectWorker(this, potionType.name()));
    }

    public int getAmount() {
        return this.amount;
    }
}
