package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.model.game.Player;

public interface PickUpAble {
    void pickUpBy(Player player);
    boolean isPickUpAbleBy(Player player);
}