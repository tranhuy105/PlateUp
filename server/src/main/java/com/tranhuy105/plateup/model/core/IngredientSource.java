package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.GameObject;
import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.model.game.Player;

public class IngredientSource extends GameObject implements PickUpAble {
    private final CarriableType sourceType;

    public IngredientSource(CarriableType sourceType, GameObjectType id, int x, int y) {
        super(x, y, id, false);
        if (sourceType.equals(CarriableType.PLATE)) {
            throw new RuntimeException("FATAL: unable to create a Plate source, please using PlateStorage instead");
        }
        this.sourceType = sourceType;
    }

    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player)) {
            Carriable item = IngredientFactory.createIngredient(sourceType);
            player.setCarryingItem(item);
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (player.isCarryingItem()) {
            System.out.println("This player has already carrying an item");
            throw new InvalidActionException("You are already carrying an item");
        }
        return true;
    }
}

