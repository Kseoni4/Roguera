package com.rogurea.creatures;

import com.rogurea.gamelogic.ItemGenerator;
import com.rogurea.gamelogic.RogueraGameSystem;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.items.Weapon;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;
import com.rogurea.resources.GetRandom;

import java.util.concurrent.ThreadLocalRandom;

public class Boss extends Mob{

    private static String[] bossNames = {"Alpha","Omega","OmegaBig","Null","Zero"};

    public Boss() {
        super(100, bossNames[ThreadLocalRandom.current().nextInt(bossNames.length)]);
        this.tag += ".boss."+name;
        this.model.changeModel(GameResources.getModel(this.name).get().getCharacter());
        this.model.changeColor(Colors.MAGENTA);
        this.baseATK = Dungeon.getCurrentFloor().get().getFloorNumber();
        this.baseDEF = Dungeon.getCurrentFloor().get().getFloorNumber();
        this.HP = 100 + RogueraGameSystem.getBaseFloorProgression()*2;
        creatureInventory.add(ItemGenerator.getSpecialBossWeapon());
        creatureInventory.add(ItemGenerator.getSpecialBossArmor());
    }
}
