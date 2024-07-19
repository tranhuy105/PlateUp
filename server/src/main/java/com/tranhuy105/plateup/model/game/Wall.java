package com.tranhuy105.plateup.model.game;

import com.tranhuy105.plateup.constant.GameObjectType;

public class Wall extends GameObject {
    public Wall(int x, int y, GameObjectType type) {
        super(x, y, type, false);
    }
}
