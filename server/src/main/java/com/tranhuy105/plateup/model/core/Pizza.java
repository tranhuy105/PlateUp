package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.DishType;

public class Pizza extends Dish {
    public Pizza(boolean isCook) {
        super(isCook, 5000L, DishType.PIZZA);
    }
}
