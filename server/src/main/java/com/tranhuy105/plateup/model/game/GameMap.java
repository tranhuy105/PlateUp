package com.tranhuy105.plateup.model.game;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameMap {
    private final GameObject[][] map;

    public GameMap(int width, int height) {
        map = new GameObject[height][width];
    }

    public GameObject getObjectAt(int x, int y) {
        return map[y][x];
    }

    public GameObject getObjectAt(Position position) {
        return map[position.getY()][position.getX()];
    }

    public void setObjectAt(int x, int y, GameObject object) {
        map[y][x] = object;
    }

    public int getWidth() {
        return map[0].length;
    }

    public int getHeight() {
        return map.length;
    }

    public List<Map<String, Object>> getMapState() {
        List<Map<String, Object>> flatList = new ArrayList<>();

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                GameObject gameObject = getObjectAt(x, y);
                if (gameObject != null) {
                    flatList.add(GameObject.getObjectRepresentation(gameObject));
                }
            }
        }

        return flatList;
    }


}
