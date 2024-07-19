package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;
import com.tranhuy105.plateup.network.GameSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Stove extends ContainerObject implements PickUpAble{
    public HotDog hotdog;
    private static final Logger logger = LoggerFactory.getLogger(Stove.class);
    public Stove(int x, int y) {
        super("STOVE", 1, GameObjectType.STOVE, x, y);
    }

    @Override
    public boolean addItem(Carriable carriable) {
        if (hotdog != null) {
            throw new InvalidActionException("Please collect your hot dog first");
        }
        List<CarriableType> curItemsType = items.stream().map(Carriable::getCarriable).toList();
        CarriableType addingItemType = carriable.getCarriable();
        if (addingItemType.equals(CarriableType.BREAD) || addingItemType.equals(CarriableType.SAUSAGE)) {
            if (!curItemsType.contains(addingItemType)) {
                if (items.isEmpty()) {
                    items.add(carriable);
                } else {
                    cook();
                }
                return true;
            } else {
                throw new InvalidActionException("This stove already have "+ addingItemType);
            }
        }
        throw new InvalidActionException("Stove only accept bread and sausage");
    }

    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player)) {
            if (hotdog != null) {
                player.setCarryingItem(hotdog);
                this.hotdog = null;
                items.clear();
                return;
            }
            Carriable currentItem = items.get(0);
            player.setCarryingItem(currentItem);
            removeItem(currentItem);
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (hotdog == null && items.isEmpty()) {
            logger.info("This stove doesn't hold any items at the moment");
            throw new InvalidActionException("This stove doesn't hold any items at the moment");
        }

        if (player.isCarryingItem()) {
            logger.info("This player is already carrying an item");
            throw new InvalidActionException("You are already carrying an item!");
        }

        return true;
    }

    public void cook() {
        logger.info("Hot Dog is completed 😎");
        this.hotdog = new HotDog();
        this.items.clear();
        this.items.add(hotdog);
        GameSocketServer.getInstance().broadcastTimingEvent("Hot Dog is completed 😎", this);
    }
}