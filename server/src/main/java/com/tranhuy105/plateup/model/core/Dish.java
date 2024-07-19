package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.DishType;

public abstract class Dish {
    private boolean isCook;
    private final long cookTime;
    private final DishType name;

    public Dish(boolean isCook, long cookTime, DishType name) {
        this.isCook = isCook;
        this.cookTime = cookTime;
        this.name = name;
    }

    public long getCookTime() {
        return cookTime;
    }

    public DishType getName() {
        return name;
    }

    public boolean isCook() {
        return isCook;
    }

    public void setCook(boolean cook) {
        isCook = cook;
    }
}

