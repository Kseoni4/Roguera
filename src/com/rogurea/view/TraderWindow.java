/*
 * Copyright (c) Ksenofontov N. 2020-2021.
 */

package com.rogurea.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.creatures.Boss;
import com.rogurea.gamemap.Dungeon;
import com.rogurea.gamemap.Position;
import com.rogurea.input.CursorUI;
import com.rogurea.input.Input;
import com.rogurea.items.Equipment;
import com.rogurea.items.Item;
import com.rogurea.resources.Colors;
import com.rogurea.resources.GameResources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static com.rogurea.view.ViewObjects.infoGrid;
import static com.rogurea.view.ViewObjects.logView;

public class TraderWindow extends Window {

    private final int _width = 26;

    private final int _height = 5;

    private CursorUI itemCursorUI;

    private final CursorUI menuCursorUI;

    private final String PRESS_ENTER_TO_SELL_ITEM = "PRESS ENTER TO SELL ITEM";

    private final String PRESS_ENTER_TO_CHOOSE_ITEM = "PRESS ENTER TO CHOOSE ITEM";

    private final String PRESS_ESC_TO_BACK = "PRESS ESC TO BACK";

    private final TerminalPosition _traderWindowPosition = new TerminalPosition(20,5);

    private final TerminalSize _traderWindowSize = new TerminalSize(_width + Math.abs(_width - PRESS_ENTER_TO_SELL_ITEM.length()), _height);

    private ArrayList<Item> itemCollection;

    private ArrayList<Item> playerItemCollection;

    private ArrayList<Item> traderItemCollection;

    private ArrayList<Element> itemElements;

    private ArrayList<Element> menuElements;

    private Optional<KeyStroke> keyMenu;

    private Optional<KeyStroke> keyItem;

    private final Runnable sellAction = () -> {
        Item item = playerItemCollection.get(itemCursorUI.indexOfElement);

        Dungeon.player.Inventory.remove(item);

        Dungeon.player.getPlayerData().setMoney(item.getSellPrice());

        logView.playerAction("sell the "+item.model.getModelColorName()+" for "+Colors.GOLDEN+item.getSellPrice());

        fillElements(this.sellAction);

        itemCursorUI = new CursorUI(itemElements);

        logView.putLog(Colors.GOLDEN + "TRADER: "
                + Colors.R + GameResources.getTraderAftersell()
                .get(new Random().nextInt(GameResources.getTraderAftersell().size()))
        );

        Draw.call(infoGrid.getFirstBlock());
    };

    private final Runnable buyAction = () -> {
        Item item = traderItemCollection.get(itemCursorUI.indexOfElement);
        if(Dungeon.player.getPlayerData().getMoney() >= item.getSellPrice()){
            if(Dungeon.player.putUpItem(item)) {

                traderItemCollection.remove(item);

                Dungeon.player.getPlayerData().setMoney(-item.getSellPrice());

                logView.playerAction("buy the " + item.model.getModelColorName() + " for " + Colors.GOLDEN + item.getSellPrice());

                fillElements(this.buyAction);

                itemCursorUI = new CursorUI(itemElements);

                logView.putLog(Colors.GOLDEN + "TRADER: "
                        + Colors.R + GameResources.getTraderAfterbuy()
                        .get(new Random().nextInt(GameResources.getTraderAfterbuy().size()))
                );

                Draw.call(infoGrid.getFirstBlock());

                Draw.call(infoGrid.getThirdBlock());
            }
        } else {
            logView.putLog(Colors.GOLDEN + "TRADER: "
                    + Colors.R + GameResources.getTraderNomoney()
                    .get(new Random().nextInt(GameResources.getTraderNomoney().size()))
            );
        }
    };

    public TraderWindow(ArrayList<Item> itemCollection) {
        super();
        this.traderItemCollection = itemCollection;

        this.itemCollection = this.traderItemCollection;
        String s = "";
        if(!itemCollection.isEmpty())
            s = itemCollection.stream().sorted(Comparator.comparing(Item::getName)).collect(Collectors.toList()).get(0).getName();

        makeWindow(_traderWindowPosition, _traderWindowSize.withRelative(s.length(),itemCollection.size()));

        makeMenu();

        fillElements(buyAction);

        menuCursorUI = new CursorUI(menuElements);

        itemCursorUI = new CursorUI(itemElements);
    }

    private void makeMenu(){
        this.menuElements = new ArrayList<>();
        this.menuElements.add(
                new Element("Buy", "BUY", new Position(2, 0), ()->{
                    Draw.reset(this);

                    this.itemCollection = traderItemCollection;

                    makeWindow(_traderWindowPosition, _traderWindowSize.withRelative(0,itemCollection.size()));

                    fillElements(buyAction);

                    itemCursorUI = new CursorUI(itemElements);

                    itemOptions();
                })
        );

        this.menuElements.add(
                new Element("Sell", "SELL", new Position(7, 0), ()->{
                    Draw.reset(this);
                    playerItemCollection = Dungeon.player.Inventory;
                    this.itemCollection = playerItemCollection;
                    String s = "";
                    if(!itemCollection.isEmpty())
                        s = itemCollection.stream().sorted(Comparator.comparing(Item::getName)).collect(Collectors.toList()).get(0).getName();
                    makeWindow(_traderWindowPosition, _traderWindowSize.withRelative(s.length(),itemCollection.size()));
                    fillElements(sellAction);
                    itemCursorUI = new CursorUI(itemElements);
                    itemOptions();
                })
        );
    }

    private void fillElements(Runnable action){
        itemElements = new ArrayList<>();
        //Debug.toLog("Size of collection: "+this.itemCollection.size());
        for(Item item : this.itemCollection) {
            //Debug.toLog("Put item \n\t" + item.toString() + "\ninto element");
            int stats = 0;
            if (item instanceof Equipment) {
                stats = ((Equipment) item).getStats();
            }
            //Debug.toLog("[TRADER]Put in menu potion: " +item.toString());
            itemElements.add(new Element(
                    item.getName(),
                    item.model.toString() + " "
                            + item.getName()
                            + " [+" + item.model.getModelColor() + stats + Colors.R+"] "
                            + Colors.GOLDEN+"$"+item.getSellPrice() + Colors.R,
                    new Position(1, this.itemCollection.indexOf(item) + 2),
                    action
            ));
        }
    }

    @Override
    protected void content() {

        for (Element element : menuElements) {
            putElementIntoWindow(element);
        }

        putStringIntoWindow(PRESS_ENTER_TO_CHOOSE_ITEM, new Position(0,1));

        for (Element element : itemElements){
            putElementIntoWindow(element);
        }

        try {
            setPointerToElement(menuElements.get(menuCursorUI.indexOfElement), menuCursorUI.cursorPointer);
        } catch (IndexOutOfBoundsException e){
            menuCursorUI.indexOfElement = Math.abs(menuCursorUI.indexOfElement - menuElements.size());
            setPointerToElement(menuElements.get(menuCursorUI.indexOfElement), menuCursorUI.cursorPointer);
        }

        if(!itemElements.isEmpty()) {
            try {
                setPointerToElement(itemElements.get(itemCursorUI.indexOfElement), itemCursorUI.cursorPointer);
                //Debug.toLog("Point to element: ".concat(itemElements.get(itemCursorUI.indexOfElement).ElementTitle));
            } catch (IndexOutOfBoundsException e) {
                itemCursorUI.indexOfElement = Math.abs(itemCursorUI.indexOfElement - itemElements.size());
                setPointerToElement(itemElements.get(itemCursorUI.indexOfElement), itemCursorUI.cursorPointer);
            }
        }
        else {
            putStringIntoWindow("Nothing to sell", new Position(0, 2));
        }
    }

    @Override
    protected void input() {
        if(Dungeon.getCurrentRoom().roomNumber == 10 &&  isBossAlive()){
            new Message("You must kill the boss first", new Position(10,5)).show();
            return;
        }
        logView.putLog(Colors.GOLDEN + "TRADER: "
                + Colors.R + GameResources.getTraderMeet()
                    .get(new Random().nextInt(GameResources.getTraderMeet().size()))
        );
        while(!Input.keyIsEscape((keyMenu = Input.waitForInput()).get())) {
            menuOptions();
        }
    }

    private boolean isBossAlive(){
        Boss boss = (Boss) Dungeon.getCurrentRoom().getObjectByTag("creature.mob.boss");
        return !boss.isDead();
    }


    private void menuOptions() {
        menuCursorUI.setFirstElementCursorPosition();
        menuCursorUI.SelectElementH(keyMenu.get().getKeyType());
        Draw.call(this);
    }

    private void itemOptions(){
        Draw.call(this);
        if (!itemElements.isEmpty()) {
            itemCursorUI.setFirstElementCursorPosition();
            while (!Input.keyIsEscape((keyItem = Input.waitForInput()).get()) && !itemElements.isEmpty()) {
                itemCursorUI.SelectElementV(keyItem.get().getKeyType());
                Draw.call(this);
            }
        }
    }
}
