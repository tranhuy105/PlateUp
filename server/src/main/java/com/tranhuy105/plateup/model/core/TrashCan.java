package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.GameObjectType;
import com.tranhuy105.plateup.exception.InvalidActionException;

public class TrashCan extends ContainerObject {
    public TrashCan(int x, int y) {
        super("TRASH_CAN", 0, GameObjectType.TRASH_CAN, x, y);
    }

    @Override
    public boolean addItem(Carriable item) {
        if (item instanceof Plate) {
            throw new InvalidActionException("Can not dispose a Plate");
        }
        // don't do anything, simply make the added item become null
        return true;
    }
}
