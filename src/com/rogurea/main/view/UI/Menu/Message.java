package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.rogurea.main.input.Input;
import com.rogurea.main.map.Dungeon;
import com.rogurea.main.map.Position;

public class Message extends AbstractWindow{

    private final String TextOfMessage;

    public Message(String textOfMessage, Position position){
        super(position, new TerminalSize(textOfMessage.length()+3,4));
        this.TextOfMessage = textOfMessage;
        this.WindowID = Dungeon.GetCurrentRoom().NumberOfRoom;
        Show();
    }

    @Override
    protected void Content() {
        PutStringOnPosition(TextOfMessage, InnerTopLeftPoint);
        PutStringOnPosition("OK", InnerTopLeftPoint.withRelative(TextOfMessage.length()/2,2));
    }

    @Override
    protected void Input() {
        KeyStroke key = Input.GetKey();
    }
}
