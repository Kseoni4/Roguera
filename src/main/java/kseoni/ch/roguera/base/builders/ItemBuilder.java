package kseoni.ch.roguera.base.builders;

import kseoni.ch.roguera.base.Position;
import kseoni.ch.roguera.items.Item;
import kseoni.ch.roguera.rendering.TextSprite;

public class ItemBuilder {

    private final Item blankItem;

    public ItemBuilder(){
        blankItem = new Item();
    }

    public ItemBuilder sellPrice(int value){
        blankItem.setSellPrice(value);
        return this;
    }

    public ItemBuilder name(String name){
        blankItem.setName(name);
        return this;
    }

    public ItemBuilder position(Position position){
        blankItem.setPosition(position);
        return this;
    }

    public ItemBuilder sprite(TextSprite sprite){
        blankItem.setTextSprite(sprite);
        return this;
    }

    public Item build(){
        return blankItem;
    }

}
