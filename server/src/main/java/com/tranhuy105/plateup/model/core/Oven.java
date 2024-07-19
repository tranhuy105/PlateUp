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

public class Oven extends ContainerObject implements PickUpAble {
    private final ScheduledExecutorService executors;
    private boolean cooking;
    private static final Logger logger = LoggerFactory.getLogger(Oven.class);

    public Oven(int x, int y) {
        super("OVEN", 1, GameObjectType.OVEN, x,y);
        this.executors = Executors.newSingleThreadScheduledExecutor();
        this.cooking = false;
    }

    @Override
    public boolean addItem(Carriable carriable) {
        if (cooking) {
            logger.info("Oven is currently cooking. Please wait until it's done.");
            throw new InvalidActionException("Oven is currently cooking. Please wait until it's done.");
        }

        if (carriable instanceof Plate plate) {
            if (items.size() < maxItem) {
                if (plate.getDish() instanceof Pizza pizza && !pizza.isCook()) {
                    items.add(carriable);
                    cook(plate, pizza);
                    return true;
                } else {
                    logger.info("Oven only accepts plates with uncooked pizzas!");
                    throw new InvalidActionException("Oven only accepts plates with uncooked pizzas!");
                }
            } else {
                logger.info("Max size reached, cannot add more items to this " + containerType);
                throw new InvalidActionException("Max size reached, cannot add more items to this " + containerType);
            }
        } else {
            logger.info("Oven only accepts plates!");
            throw new InvalidActionException("Oven only accepts plates!");
        }
    }

    private void cook(Plate plate, Pizza pizza) {
        cooking = true;
        executors.schedule(() -> {
            pizza.setCook(true);
            String message = "Pizza is completed 🍕";
            logger.info(message);
            cooking = false;
            GameSocketServer.getInstance().broadcastTimingEvent(message, this);
        }, pizza.getCookTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player)) {
            Carriable currentItem = items.get(0);
            player.setCarryingItem(currentItem);
            items.clear();
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (cooking) {
            logger.info("Oven is currently cooking. Please wait until it's done.");
            throw new InvalidActionException("Oven is currently cooking. Please wait until it's done.");
        }

        if (items.isEmpty()) {
            logger.info("This oven doesn't hold any items at the moment");
            throw new InvalidActionException("This oven doesn't hold any items at the moment");
        }

        if (player.isCarryingItem()) {
            logger.info("This player is already carrying an item");
            throw new InvalidActionException("This player is already carrying an item");
        }

        return true;
    }

    private void shutDownExecutor() {
        if (executors != null && !executors.isShutdown()) {
            executors.shutdown();
        }
    }
}

