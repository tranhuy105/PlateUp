package com.tranhuy105.plateup.model.game;

import com.tranhuy105.plateup.constant.GameObjectType;

public class EmptySpace extends GameObject {
    public EmptySpace(int x, int y) {
        super(x, y, GameObjectType.EMPTY_SPACE, true);
    }
}

