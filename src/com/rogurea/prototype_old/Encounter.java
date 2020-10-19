package com.rogurea.prototype_old;

import java.util.List;

public class Encounter {

    static Mob[] targets;
    static Mob target;

    static void SetLength(int length){
        targets = new Mob[length];
    }

    static void InsertTarget(int i, Mob mob){
        targets[i] = mob;
    }

    static void SetTarget(){
        target = targets[Input.WaitFromPlayer()-1];
    }

    static void Attack(Mob target, Player player){

        Weapon weapon = ((Weapon) player.Inventory.get(0));
        target.changeHP(weapon.getDamage());

        System.out.printf("\t %s get %d damage from player's %s \n",
                target.Name,
                (weapon.getDamage() - target.getArmor()),
                weapon.name);
    }

    static void MobAttack(List<Creature> mobs, Player player){
        for(Creature mob : mobs){
            player.changeHP(mob.getDamage());
            System.out.printf("\t Player get %d damage from %s \n", Math.max((mob.getDamage() - player.CountArmor()),0), mob.Name);
        }
    }


    static void RemoveCreature(Creature target, Room r){
        if(target.HP <= 0){
            System.out.printf("\t You kill %s!\n", target.Name);
            r.RoomCreatures.remove(target);
        }
    }
}
