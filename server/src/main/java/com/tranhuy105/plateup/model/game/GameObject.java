package com.tranhuy105.plateup.model.game;

import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.model.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class GameObject {
    protected Position position;
    protected GameObjectType type;
    protected boolean walkable;

    public GameObject(int x, int y, GameObjectType type, boolean walkable) {
        this.position = new Position(x, y);
        this.type = type;
        this.walkable = walkable;
    }

    public Position getPosition() {
        return position;
    }

    public byte getId() {
        return type.getId();
    }

    public String getObjectType() {
        return type.name();
    }

    public boolean isWalkable() {
        return this.walkable;
    }

    public static Map<String, Object> getObjectRepresentation(GameObject gameObject) {
        Map<String, Object> objectRepresentation = new HashMap<>();
        objectRepresentation.put("position", gameObject.getPosition());
        objectRepresentation.put("id", gameObject.getId());
        objectRepresentation.put("type", gameObject.getObjectType());
        objectRepresentation.put("isContainer", false);
        objectRepresentation.put("items", null);
        objectRepresentation.put("isPickupAble", false);

        if (gameObject instanceof ContainerObject o) {
            objectRepresentation.put("isContainer", true);
            if (o instanceof DishWasher dishWasher) {
                // maybe check stove and oven as well if want to render busy state
                objectRepresentation.put("isBusy", dishWasher.isWashing());
            }

            if (!o.getItems().isEmpty() && o.getItems().get(0) instanceof Plate plate) {
                List<Object> items = new ArrayList<>();
                items.add(plate.getPlateStatus());
                objectRepresentation.put("items", items);
            } else {
                objectRepresentation.put("items", o.getItems());
            }
        }

        if (gameObject instanceof PickUpAble o) {
            objectRepresentation.put("isPickupAble", true);
            if (o instanceof PlateSource p) {
                objectRepresentation.put("items", p.getItems().stream().map(Plate::getPlateStatus).toList());
            }
        }

        return objectRepresentation;
    }

}
