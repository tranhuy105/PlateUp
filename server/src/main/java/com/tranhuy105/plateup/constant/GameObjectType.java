package com.tranhuy105.plateup.constant;

public enum GameObjectType {
    EMPTY_SPACE(0),
    WALL_NORTH(1),
    WALL_SOUTH(2),
    WALL_EAST(3),
    WALL_WEST(4),
    WALL_NORTH_WEST(5),
    WALL_NORTH_EAST(6),
    WALL_SOUTH_EAST(8),
    WALL_SOUTH_WEST(7),
    STOVE(9),
    OVEN(10),
    TOMATO_SOURCE(11),
    CHEESE_SOURCE(12),
    SAUSAGE_SOURCE(13),
    BREAD_SOURCE(14),
    PLATE_SOURCE(15),
    KITCHEN_BLOCK(16),
    WALL_EMPTY(17),
    DISH_WASHER(18),
    TRASH_CAN(19),
    CUSTOMER_TABLE(20);
    private final byte id;

    GameObjectType(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
