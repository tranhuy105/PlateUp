package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;
import com.tranhuy105.plateup.network.GameSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DishWasher extends ContainerObject implements PickUpAble{
    private static final Logger logger = LoggerFactory.getLogger(DishWasher.class);
    private final ScheduledExecutorService executors;
    private boolean isWashing;
    public DishWasher(int x, int y) {
        super("DISH_WASHER", 1, GameObjectType.DISH_WASHER, x, y);
        executors = Executors.newSingleThreadScheduledExecutor();
        isWashing = false;
    }

    public Carriable getCurrentItem() {
        if (items.isEmpty()) {
            return null;
        }
        return super.getItems().get(0);
    }

    @Override
    public boolean addItem(Carriable item) {
        if (isWashing) {
            throw new InvalidActionException("Currently Washing A Dish, Please try again");
        }

        if (items.size() >= maxItem) {
            logger.info("Max size reached, can not add more item to this "+ containerType);
            throw new InvalidActionException("Max size reached, can not add more item to this "+ containerType);
        }
        if (!(item instanceof Plate plate)) {
            throw new InvalidActionException("Only accept Plate");
        }

        if (!plate.isDirt()) {
            throw new InvalidActionException("Only accept dirt plate");
        }

        items.add(plate);
        wash(plate);
        return true;
    }

    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player)){
            Carriable currentItem = getCurrentItem();
            player.setCarryingItem(currentItem);
            removeItem(currentItem);
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (isWashing) {
            throw new InvalidActionException("Currently washing, please wait a moment!");
        }

        if (player.isCarryingItem()) {
            throw new InvalidActionException("You are already carrying an item");
        }

        if (items.isEmpty()) {
            throw new InvalidActionException("This container don't have any item!");
        }

        return true;
    }

    private void wash(Plate plate) {
        isWashing = true;
        executors.schedule(() -> {
            plate.setIsDirt(false);
            isWashing = false;
            String message = "Your Dish is now clean ♻️";
            logger.info(message);
            GameSocketServer.getInstance().broadcastTimingEvent(message, this);
        }, 4000L, TimeUnit.MILLISECONDS);
    }

    public boolean isWashing() {
        return isWashing;
    }
}
