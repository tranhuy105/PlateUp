package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.model.game.Player;

public interface Carriable {
    CarriableType getCarriable();
    void drop(Player player, ContainerObject containerObject);
}