package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;
import com.tranhuy105.plateup.network.GameSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerTable extends ContainerObject implements PickUpAble{
    private static final Logger logger = LoggerFactory.getLogger(CustomerTable.class);
    public CustomerTable(int x, int y) {
        super("CUSTOMER_TABLE", 1, GameObjectType.CUSTOMER_TABLE, x, y);
    }

    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player)) {
            Carriable currentItem = items.get(0);
            player.setCarryingItem(currentItem);
            super.removeItem(currentItem);
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (items.isEmpty()) {
            logger.info("This block don't hold any item to interact");
            throw new InvalidActionException("This block don't hold any item to pick up");
        }

        if (player.isCarryingItem()) {
            logger.info("This user already carrying an item");
            throw new InvalidActionException("You are already carrying an item");
        }

        return true;
    }

    @Override
    public boolean addItem(Carriable carriable) {
        if (!(carriable instanceof Plate plate)) {
            logger.info("Customer Only accept food serve in a plate!");
            throw new InvalidActionException("Customer Only accept food serve in a plate!");
        }

        if (!plate.hasDish()) {
            logger.info("Please serve a Dish in the Plate first before bring it to customer!");
            throw new InvalidActionException("Please serve a Dish in the Plate first before bring it to customer!");
        }

        if (!plate.getDish().isCook()) {
            logger.info("You can't serve raw food to your customer ☠️, cook it first!");
            throw new InvalidActionException("You can't serve raw food to your customer ☠️, cook it first!");
        }

        plate.serveDish(this);
        GameSocketServer.getInstance().broadcastTimingEvent("Thank you for your service!", this);
        return true;
    }
}
