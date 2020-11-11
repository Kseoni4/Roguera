package com.rogurea.main.gamelogic;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.gamelogic.rgs.Formula;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.Draw;
import com.rogurea.main.view.viewblocks.LogBlock;

import static com.rogurea.Main.gameLoop;
import static com.rogurea.main.resources.ViewObject.logBlock;
import static com.rogurea.main.resources.ViewObject.playerInfoBlock;

public class Fight {

    public static void HitPlayer(Mob mob){
        int dmg = Math.max((mob.getDamage() - Formula.GetPlayerDEF()), 0);

        if(Formula.IsMiss(Player.DEX)){
            logBlock.Action("miss!", mob);
            return;
        }
        Player.HP -= dmg;

        logBlock.Action(GetRandom.HitLog(dmg), mob);

        Draw.call(playerInfoBlock);
    }

    public static void HitMob(Mob mob){
        mob.changeHP((short) Formula.GetPlayerATK());
        logBlock.Action("hit "
                + mob.Name + " for " + Player.getDamage() + " damage!");
        Draw.call(playerInfoBlock);
    }
}
