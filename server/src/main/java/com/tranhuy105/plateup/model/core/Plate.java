package com.tranhuy105.plateup.model.core;


import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

// đặt một carriable vào, ok
// lấy 1 carriable ra,
// có thể bị pickUp bới player, ok
// có thể bị drop vào Receivable, ok
public class Plate extends ContainerObject implements Carriable, PickUpAble {
    private static final Logger logger = LoggerFactory.getLogger(Plate.class);
    private Dish dish;
    private boolean isDirt = false;

    public Plate() {
        super("PLATE", 1, GameObjectType.EMPTY_SPACE, -1, -1);
    }

    // 1 đĩa chỉ max chứa dc 1 carriable
    public Carriable getCurrentItem() {
        return items.isEmpty() ? null : items.get(0);
    }

    public boolean hasDish() {
        return dish != null;
    }

    public void serveDish(CustomerTable table) {
        setIsDirt(true);
        this.dish = null;
        this.items.clear();
        table.items.add(this);
    }

    public boolean isDirt() {
        return isDirt;
    }

    public void setIsDirt(boolean dirt) {
        isDirt = dirt;
    }

    @Override
    public CarriableType getCarriable() {
        return CarriableType.PLATE;
    }

    @Override
    public void pickUpBy(Player player) {
        if (player.isCarryingItem()) {
            logger.info("This player has already carrying an item");
            throw new InvalidActionException("You are already carrying an item");
        }
        player.setCarryingItem(this);
        logger.info("Player has picked up "+ getCarriable().toString());
    }

    @Override
    public boolean addItem(Carriable carriable) {
        if (hasDish()) {
            logger.info("This plate already serve a dish");
            throw new InvalidActionException("This plate already serve a dish");
        }

        if (isDirt()) {
            logger.info("This Plate is dirt, please wash it first! 🤢");
            throw new InvalidActionException("This Plate is dirt, please wash it first! 🤢");
        }

        if (isInAllowedPlateItem(carriable)) {
            if (carriable instanceof HotDog) {
                this.dish = (HotDog) carriable;
            } else {
                if (getCurrentItem() != null) {
                    items.clear();
                    this.dish = new Pizza(false);
                } else {
                    return this.items.add(carriable);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void drop(Player player, ContainerObject containerObject) {
        if (containerObject instanceof Plate) {
            logger.info("Can't drop a plate on another plate");
            throw new InvalidActionException("Can't drop a plate on another plate");
        }
        if (player.isCarryingItem()) {
            if (containerObject.addItem(this)){
                player.setCarryingItem(null);
            } else {
                logger.info("Fail to add item to "+ containerObject.containerType);
                throw new InvalidActionException("Fail to drop item, container full");
            }
        } else {
            logger.info("This player is not carrying any item!");
            throw new InvalidActionException("You do not carrying any item to drop!");
        }
    }

    // interact với plate sẽ lấy item hiện tại ra
    public void pickUpPlateItemBy(Player player) {
        if (isPickUpAbleBy(player)) {
            Carriable currentItem = getCurrentItem();
            player.setCarryingItem(currentItem);
            super.removeItem(currentItem);
        }
    }

    @Override
    public boolean isPickUpAbleBy(Player player) {
        if (hasDish()) {
            logger.info("This plate is already serve a dish");
            return false;
        }
        if (player.isCarryingItem()) {
            logger.info("This player has already carrying an item");
            return false;
        }
        if (super.getItems().isEmpty()) {
            logger.info("Can't pick up item from this plate since it's empty");
            return false;
        }

        return true;
    }

    public Dish getDish() {
        return dish;
    }

    private boolean isInAllowedPlateItem(Carriable item) {
        if (item instanceof Ingredient) {
            return isAllowedIngredient((Ingredient) item);
        }
        if (item instanceof Plate) {
            logger.info("Can't add a plate to another plate");
            return false;
        }
        return true;
    }

    private boolean isAllowedIngredient(Ingredient ingredient) {
        CarriableType type = ingredient.getCarriable();
        if (type.equals(CarriableType.CHEESE) || type.equals(CarriableType.TOMATO)) {
            return isAllowedCheeseOrTomato(ingredient);
        } else {
            logger.info("For food ingredient, only accept cheese and tomato");
            throw new InvalidActionException("For food ingredient, only accept cheese and tomato");
        }
    }

    private boolean isAllowedCheeseOrTomato(Ingredient ingredient) {
        if (!ingredient.isRaw) {
            return isTypeDifferentFromCurrent(ingredient);
        } else {
            logger.info("For cheese and tomato only accept cooked");
            throw new InvalidActionException("For cheese and tomato only accept cooked");
        }
    }

    private boolean isTypeDifferentFromCurrent(Carriable item) {
        Carriable currentItem = getCurrentItem();
        if (currentItem == null) {
            return true;
        } else {
            return !item.getCarriable().equals(currentItem.getCarriable());
        }
    }

    public Map<String, Object> getPlateStatus() {
        Map<String, Object> plateRepresentation = new HashMap<>();
        plateRepresentation.put("currentItem", getCurrentItem());
        plateRepresentation.put("dish", getDish());
        plateRepresentation.put("carriable", getCarriable());
        plateRepresentation.put("isDirt", isDirt);
        return plateRepresentation;
    }
}
