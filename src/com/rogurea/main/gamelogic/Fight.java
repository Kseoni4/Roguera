package com.rogurea.main.gamelogic;

import com.rogurea.main.creatures.Mob;
import com.rogurea.main.player.Player;
import com.rogurea.main.resources.GetRandom;
import com.rogurea.main.view.LogBlock;

public class Fight {

    public static void HitPlayer(Mob mob){
        int dmg = (mob.getDamage() - Player.getArmor());

        Player.HP -= (Math.max(dmg, 0));

        LogBlock.Action(GetRandom.HitLog(dmg), mob);

        /*LogBlock.Action("hit " + Colors.GREEN_BRIGHT +
                "you" + Colors.R
                + " for " + Colors.RED_BRIGHT
                + (Math.max(dmg,0))
                + " damage!", mob);*/
    }

    public static void HitMob(Mob mob){
        System.out.println("HitMob");
        System.out.println("Mob id: " + mob.id);
        mob.changeHP(
                Player.getDamage()
        );
        LogBlock.Action("hit "
                + mob.Name + " for " + Player.getDamage() + " damage!");
    }

}
