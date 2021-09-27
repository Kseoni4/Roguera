/*
 * Copyright (c) Kseno 2021.
 */

package com.rogurea.dev.gamemap;

import com.rogurea.dev.base.GameObject;
import com.rogurea.dev.resources.Colors;
import com.rogurea.dev.resources.Model;

public class EditorEntity extends GameObject {

    private static int EditorEntityCounter = 0;

    public static final GameObject EMPTY_CELL = new EditorEntity(new Model("empty_cell", Colors.BLACK, ' '));

    public EditorEntity(Model model) {
        this.tag = "editor_entity";
        this.id = EditorEntityCounter++;
        setModel(model);
    }
}
