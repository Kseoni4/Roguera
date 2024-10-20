package kseoni.ch.roguera.game.items;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.builders.ItemBuilder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Item extends GameObject {

    public static ItemBuilder builder(){
        return new ItemBuilder();
    }

    private int sellPrice = 1;

    public Item(String name){
        super(name);
    }

    public Item(){
        super();
    }
}
