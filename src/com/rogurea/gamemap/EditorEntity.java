/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.gamemap;

import com.rogurea.base.GameObject;
import com.rogurea.resources.Colors;
import com.rogurea.resources.Model;

public class EditorEntity extends GameObject {

    private static int EditorEntityCounter = 0;

    public static final GameObject EMPTY_CELL = new EditorEntity(new Model("empty_cell", Colors.BLACK, ' '));

    public EditorEntity(Model model) {
        this.tag = "editor_entity";
        this.id = EditorEntityCounter++;
        setModel(model);
    }
}
