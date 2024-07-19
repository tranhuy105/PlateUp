package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.model.game.GameObject;
import com.tranhuy105.plateup.constant.GameObjectType;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerObject extends GameObject implements Container {
    protected List<Carriable> items = new ArrayList<>();
    protected String containerType;
    protected int maxItem;

    public ContainerObject(String containerType, int maxItem, GameObjectType gameObjectType, int x, int y) {
        super(x,y,gameObjectType, false);
        this.containerType = containerType;
        this.maxItem = maxItem;
    }

    @Override
    public boolean addItem(Carriable item) {
        if (items.size() >= maxItem) {
            System.out.println("Max size reached, can not add more item to this "+ containerType);
            return false;
        }
        checkAndPerformAction();
        return this.items.add(item);
    }

    public void checkAndPerformAction() {
        System.out.println("WARNING: checkAndPerformAction should be override by child of container");
    }

    @Override
    public void removeItem(Carriable item) {
        this.items.remove(item);
    }

    @Override
    public boolean hasItem(Carriable carriable) {
        return this.items.contains(carriable);
    }

    @Override
    public List<Carriable> getItems() {
        return this.items;
    }

}
