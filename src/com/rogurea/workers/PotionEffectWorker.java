package com.rogurea.workers;

import com.rogurea.base.Debug;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.items.Potion;
import com.rogurea.view.Draw;
import com.rogurea.view.Window;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static com.rogurea.view.ViewObjects.infoGrid;

public class PotionEffectWorker implements Runnable{

    private final int EFFECT_SECONDS_MINIMUM_TIME = 10;

    private final Potion potion;

    private final String type;

    public PotionEffectWorker(Potion potion, String type){
        this.potion = potion;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            switch (type) {
                case "HEAL":
                    Dungeon.player.getPlayerData().setHP(Math.min(Dungeon.player.getPlayerData().getMaxHP(), Dungeon.player.getHP() + potion.getAmount()));
                    break;
                case "ATK_BUF":
                    Dungeon.player.getPlayerData().set_atkPotionBonus(Dungeon.player.getPlayerData().get_atkPotionBonus() + potion.getAmount());
                    startTimer();
                    Dungeon.player.getPlayerData().set_atkPotionBonus(Dungeon.player.getPlayerData().get_atkPotionBonus() - potion.getAmount());
                    break;
                case "DEF_BUF":
                    Dungeon.player.getPlayerData().set_defPotionBonus(Dungeon.player.getPlayerData().get_defPotionBonus() + potion.getAmount());
                    startTimer();
                    Dungeon.player.getPlayerData().set_defPotionBonus(Dungeon.player.getPlayerData().get_defPotionBonus() - potion.getAmount());
                    break;
                case "SCORE_BUF":
                    Dungeon.player.getPlayerData().addScoreMultiplier(potion.getAmount());
                    break;
            }
        } catch (InterruptedException e){
            e.getMessage();
        }

        if(!Window.isOpen()){
            Draw.call(infoGrid.getFirstBlock());
        }
        Debug.toLog("[POTION_EFFECT_WORKER] Worker of "+potion.getName()+" was ended");
    }

    private void startTimer() throws InterruptedException {
        int timer = EFFECT_SECONDS_MINIMUM_TIME;

        Draw.call(infoGrid.getThirdBlock());

        potion.setEffectTimer(timer);

        infoGrid.putPotionBonusInfo(potion);

        Debug.toLog("[POTION_EFFECT_WORKER] Worker of "+potion.getName()+" was started");

        if(!Window.isOpen()) {
            Draw.call(infoGrid.getFirstBlock());
        }

        while(timer > 0) {

            infoGrid.drawPotionTimer(potion);

            timer--;
        }
        infoGrid.clearTimers();
    }
}
