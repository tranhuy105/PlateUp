package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.CarriableType;

public class IngredientFactory {
    private static Ingredient tomato() {
        return new Ingredient(CarriableType.TOMATO, true, 5000L);
    }

    private static Ingredient cheese() {
        return new Ingredient(CarriableType.CHEESE, true, 3000L);
    }

    private static Ingredient sausage() {
        return new Ingredient(CarriableType.SAUSAGE, false, 0L);
    }

    private static Ingredient bread() {
        return new Ingredient(CarriableType.BREAD, false, 0L);
    }

    public static Ingredient createIngredient(CarriableType type) {
        return switch (type) {
            case SAUSAGE -> sausage();
            case TOMATO -> tomato();
            case BREAD -> bread();
            case CHEESE -> cheese();
            default -> throw new IllegalArgumentException("Invalid Carriable Type");
        };
    }
}
