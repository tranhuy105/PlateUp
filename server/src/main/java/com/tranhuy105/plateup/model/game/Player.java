package com.tranhuy105.plateup.model.game;

import com.tranhuy105.plateup.constant.Direction;
import com.tranhuy105.plateup.model.core.*;
import com.tranhuy105.plateup.exception.InvalidActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private int x, y;
    private final String id;
    private boolean isBusy = false;
    private static final Logger logger = LoggerFactory.getLogger(Player.class);
    private Carriable carryingItem;
    private Direction facingDirection = Direction.DOWN;


    public Player(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    public void printStatus() {
        logger.info("Player Status:");
        logger.info("Position: ({}, {})", x, y);
        if (isCarryingItem()) {
            logger.info("Carrying Item: {}", carryingItem);
        } else {
            logger.info("Carrying Item: None");
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public Carriable getCarryingItem() {
        return carryingItem;
    }

    public boolean isCarryingItem() {
        return carryingItem != null;
    }

    public void setCarryingItem(Carriable carryingItem) {
        this.carryingItem = carryingItem;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }

    // action

    public void PickUpPlateItem(Plate plate) {
        plate.pickUpPlateItemBy(this);
    }

    public void move(int deltaX, int deltaY, GameMap gameMap) {
        setFacingDirection(deltaX, deltaY);
        int newX = x + deltaX;
        int newY = y + deltaY;

        if (isBusy()) {
            throw new InvalidActionException("Can not move while performing action");
        }

        if (!gameMap.getObjectAt(newX, newY).isWalkable()) {
//            throw new InvalidActionException("Can not move to the target position");
            return;
        }
        if (newX >= 0 && newX < gameMap.getWidth() && newY >= 0 && newY < gameMap.getHeight()) {
            x = newX;
            y = newY;
        } else {
            // throw new InvalidActionException("Can not move to the target position");
        }
    }

    public void processFoodIngredientOnKitchenBlock(GameObject obj) {
        if (obj instanceof KitchenBlock kitchenBlock) {
            Carriable curItem = kitchenBlock.getCurrentItem();
            if (curItem instanceof Ingredient ingredient) {
                ingredient.processRawFood(this, kitchenBlock);
                logger.info("Processed food ingredient: {}", curItem);
            } else {
                logger.warn("Only ingredients are processable. Current item: {}", curItem);
                String carriableType =  curItem != null ? curItem.getCarriable().toString() : null;
                throw new InvalidActionException("Only ingredients are processable. Current item: "+ carriableType);
            }
        } else {
            logger.warn("Cannot process food on: {}. It's not a kitchen block.", obj);
            throw new InvalidActionException("Can only process ingredient on kitchen block");
        }
    }

    public void pickUpFrom(GameObject obj) {
        if (obj instanceof PickUpAble pickUpAble) {
            pickUpAble.pickUpBy(this);
            logger.info("Picked up item from: {}", obj);
        } else {
            logger.warn("Cannot pick up from: {}. It's not pick-up-able.", obj);
            throw new InvalidActionException("You can not pick up this object");
        }
    }

    public void drop(GameObject receiveObject) {
        if (isCarryingItem()) {
            if (receiveObject instanceof ContainerObject container) {
                logger.info("Dropping item: {}", carryingItem);
                carryingItem.drop(this, container);
            } else {
                logger.warn("Cannot drop item to: {}. It's not a container.", receiveObject);
                throw new InvalidActionException("You can only drop item to an container");
            }
        } else {
            logger.warn("Can't drop, player isn't carrying any items at the moment");
            throw new InvalidActionException("You dont carrying any item to drop");
        }
    }

    private void setFacingDirection(int dx, int dy) {
        if (dx == 1 && dy == 0) {
            this.facingDirection = Direction.RIGHT;
        } else if (dx == -1 && dy == 0) {
            this.facingDirection = Direction.LEFT;
        } else if (dx == 0 && dy == 1) {
            this.facingDirection = Direction.DOWN;
        } else if (dx == 0 && dy == -1) {
            this.facingDirection = Direction.UP;
        }
    }

    public Position getFocusPosition() {
        int focusX = x;
        int focusY = y;

        switch (facingDirection) {
            case UP -> focusY--;
            case DOWN -> focusY++;
            case LEFT -> focusX--;
            case RIGHT -> focusX++;
        }

        return new Position(focusX, focusY);
    }

    public static Map<String, Object> getPlayerState(Player player) {
        Map<String, Object> playerState = new HashMap<>();
        playerState.put("position", new Position(player.getX(), player.getY()));
        Carriable carryingItem = player.getCarryingItem();
        if (carryingItem instanceof Plate plate) {
            playerState.put("carrying", plate.getPlateStatus());
        } else {
            playerState.put("carrying", carryingItem);
        }
        playerState.put("facingDirection", player.getFacingDirection());
        playerState.put("name", player.getId());
        playerState.put("isBusy", player.isBusy());
        return playerState;
    }

    public void resetPosition() {
        this.x = 2;
        this.y = 2;
        this.facingDirection = Direction.DOWN;
    }

}
