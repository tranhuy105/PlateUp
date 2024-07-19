package com.tranhuy105.plateup.model.game;

import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.model.core.*;

public class GameObjectFactory {
    private static GameObject kitchenBlock(int x, int y, GameObjectType type) {
        return new KitchenBlock(x, y, type);
    }
    private static GameObject emptySpace(int x, int y) {
        return new EmptySpace(x, y) {};
    }

    private static GameObject wall(int x, int y, GameObjectType type) {
        return new Wall(x, y, type) {};
    }

    private static GameObject stove(int x, int y) {
        return new Stove(x, y);
    }

    private static GameObject oven(int x, int y) {
        return new Oven(x, y);
    }

    private static GameObject source(int x, int y, GameObjectType type) {
        if (type.equals(GameObjectType.PLATE_SOURCE)) {
            return new PlateSource(4, x, y);
        }

        CarriableType sourceType = switch (type) {
            case TOMATO_SOURCE -> CarriableType.TOMATO;
            case SAUSAGE_SOURCE -> CarriableType.SAUSAGE;
            case CHEESE_SOURCE -> CarriableType.CHEESE;
            case BREAD_SOURCE -> CarriableType.BREAD;
            default -> throw new IllegalArgumentException("Invalid ingredient source");
        };
        return new IngredientSource(sourceType, type, x,y) {};
    }

    private static GameObject dishWasher(int x, int y) {
        return new DishWasher(x, y);
    }

    private static GameObject table(int x, int y) {
        return new CustomerTable(x, y);
    }

    private static GameObject trashCan(int x, int y) {
        return new TrashCan(x, y);
    }

    public static GameObject createGameObject(GameObjectType type, int x, int y) {
        return switch (type) {
            case EMPTY_SPACE -> emptySpace(x, y);
            case WALL_NORTH, WALL_SOUTH, WALL_EAST, WALL_WEST,
                    WALL_NORTH_WEST, WALL_NORTH_EAST, WALL_SOUTH_EAST, WALL_SOUTH_WEST, WALL_EMPTY
                    -> wall(x, y, type);
            case STOVE -> stove(x, y);
            case OVEN -> oven(x, y);
            case TOMATO_SOURCE, CHEESE_SOURCE, SAUSAGE_SOURCE, BREAD_SOURCE, PLATE_SOURCE ->
                    source(x, y, type);
            case KITCHEN_BLOCK -> kitchenBlock(x, y, type);
            case TRASH_CAN -> trashCan(x, y);
            case CUSTOMER_TABLE -> table(x, y);
            case DISH_WASHER -> dishWasher(x, y);
        };
    }
}
