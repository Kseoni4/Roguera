package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.Main;
import com.rogurea.main.input.Input;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;
import com.rogurea.main.resources.GameResources;

public class ExitDungeonMenu extends BasicMenu {
    public ExitDungeonMenu() {

        super(new Position(10,10),"", new TerminalSize(25,5));
    }

    @Override
    protected void MenuElements() {

        KeyStroke Key = Input.GetKey();

        MenuCursor.SelectElement(Key.getKeyType());
    }

    @Override
    protected void SetupElements() {

        int InnerX = InnerTopLeftPoint.getColumn();
        int InnerY = InnerTopLeftPoint.getRow();

        setPointer(GameResources.GetModel("PointerRight"));

        MenuElements.add(new Element("ExitButton", "Quit", new Position(InnerX+1,InnerY+2),()->{
            Main.gameLoop.isGameOver = true;
            UIHide = Disable();
        }));

        MenuElements.add(new Element("ContinueButton", "Continue journey",
                new Position(MenuElements.get(0).ElementSize + (InnerX+3), InnerY+2), () -> {
                UIHide = Disable();
                Dungeon.NextGenerate(); })
        );

        MenuElements.forEach(element -> element.setElementPointerPosition(element.ElementPosition.getRelative(-1,0)));

        MenuCursor.CursorPonter = Pointer;

        updateElements();
    }

    @Override
    protected void MenuContent() {

        PutStringOnPosition("Do you want to exit?", new Position(InnerTopLeftPoint.getColumn()+2, InnerTopLeftPoint.getRow()));

        for(Element element : MenuElements){
            PutElementOnPosition(element);
        }
        PutPointerNearElement(MenuCursor);
    }
}
