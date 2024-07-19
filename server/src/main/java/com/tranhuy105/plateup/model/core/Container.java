package com.tranhuy105.plateup.model.core;

import java.util.List;


public interface Container {
    boolean addItem(Carriable item);
    void removeItem(Carriable item);
    boolean hasItem(Carriable item);
    List<Carriable> getItems();
}