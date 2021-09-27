/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.main.view.UI.Menu;

import com.googlecode.lanterna.TerminalSize;
import com.rogurea.main.map.Position;

public class MenuWithTabs extends MenuWithContext {
    public MenuWithTabs(Position topLeftBasePoint, String menuTitle, TerminalSize menuSize) {
        super(topLeftBasePoint, menuTitle, menuSize);
    }
}
