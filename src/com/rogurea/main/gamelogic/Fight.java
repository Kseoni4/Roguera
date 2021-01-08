package com.rogurea.main.gamelogic;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.Effects;
import static com.rogurea.main.view.ViewObjects.*;

public class Fight {

    public static void HitPlayerByMob(Mob mob){

        int dmg = Math.max((mob.getDamage() - Formula.GetPlayerDEF()), 0);

        if(Formula.IsMiss(Player.DEX)){
            logBlock.Action("miss!", mob);
            return;
        }

        Effects.PlayerHitEffect();

        Player.HP -= dmg;

        logBlock.Action(GetRandom.HitLog(dmg), mob);

        Draw.call(playerInfoBlock);
    }

    public static void HitMobByPlayer(Mob mob){
        mob.changeHP((short) Formula.GetPlayerATK());
        logBlock.Action("hit "
                + mob.Name + " for " + Formula.GetPlayerATK() + " damage by " + Player.getPlayerWeaponInHands());
        Effects.MobHitEffect(mob);

        Draw.call(playerInfoBlock);
    }
}
