package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KitchenBlock extends ContainerObject implements PickUpAble {
    private static final Logger logger = LoggerFactory.getLogger(KitchenBlock.class);
    public KitchenBlock(int x, int y, GameObjectType kitchenBlockType) {
        super("BLOCK", 1, kitchenBlockType, x, y);
    }

    public Carriable getCurrentItem() {
        if (items.isEmpty()) {
            return null;
        }
        return super.getItems().get(0);
    }



    @Override
    public boolean addItem(Carriable carriable) {
        Carriable curItem = getCurrentItem();
        if (items.size() >= maxItem) {
            if (curItem instanceof Plate) {
                return ((Plate) curItem).addItem(carriable);
            }
            throw new InvalidActionException("Max size reached, can not add more item to this "+ containerType + " container");
        }
        return super.items.add(carriable);
    }


    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player)) {
            Carriable currentItem = getCurrentItem();
            if (currentItem instanceof Ingredient ingredient) {
                if (ingredient.isProcessing()) {
                    throw new InvalidActionException("Currently processing this "+ingredient.getCarriable()+", please comeback later.");
                }
            }
            player.setCarryingItem(currentItem);
            super.removeItem(currentItem);
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (getCurrentItem() == null ) {
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
    public void checkAndPerformAction() {

    }
}

