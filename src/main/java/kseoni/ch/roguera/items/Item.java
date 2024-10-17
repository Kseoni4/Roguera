package kseoni.ch.roguera.items;

import kseoni.ch.roguera.base.GameObject;
import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.base.builders.ItemBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
