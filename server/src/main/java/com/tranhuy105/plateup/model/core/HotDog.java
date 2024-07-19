package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.constant.DishType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;

public class HotDog extends Dish implements Carriable{
    public HotDog() {
        super(true, 0L, DishType.HOTDOG);
    }

    @Override
    public CarriableType getCarriable() {
        return CarriableType.HOTDOG;
    }

    @Override
    public void drop(Player player, ContainerObject containerObject) {
        if (player.isCarryingItem()) {
            if (containerObject.addItem(this)){
                player.setCarryingItem(null);
            } else {
                System.out.println("Fail to add item to "+ containerObject.containerType);
                throw new InvalidActionException("Fail to drop item, reason unknown");
            }
        } else {
            System.out.println("This player is not carrying any item!");
            throw new InvalidActionException("You do not carrying any item to drop!");
        }
    }
}
