package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.GameObject;
import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.model.game.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
public class PlateSource extends GameObject implements PickUpAble {
    private static final Logger logger = LoggerFactory.getLogger(PlateSource.class);
    private final List<Plate> items = new ArrayList<>();

    public PlateSource(int maxCount, int x, int y) {
        super(x, y, GameObjectType.PLATE_SOURCE, false);
        for (int i = 0; i < maxCount; i++) {
            items.add(new Plate());
        }
    }

    public List<Plate> getItems() {
        return items;
    }

    public int remainingPlateCount() {
        return items.size();
    }

    @Override
    public void pickUpBy(Player player) {
        if (isPickUpAbleBy(player))
            player.setCarryingItem(getAnItemFromPlateSource());
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (player.isCarryingItem()) {
            logger.info("This player is already holding an item");
            throw new InvalidActionException("You are already carrying an item");
        }
        if (items.isEmpty()) {
            logger.info("There are no more plates left");
            throw new InvalidActionException("There are no more plates left!");
        }
        return true;
    }

    private Plate getAnItemFromPlateSource() {
        return items.remove(0);
    }
}
