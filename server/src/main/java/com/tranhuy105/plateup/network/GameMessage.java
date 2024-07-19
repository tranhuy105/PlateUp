package com.tranhuy105.plateup.network;

public class GameMessage {
    private String action;
    private int deltaX;
    private int deltaY;
    private int x;
    private int y;
    private String itemType;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
