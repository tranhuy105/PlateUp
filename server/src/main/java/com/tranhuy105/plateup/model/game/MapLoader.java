package com.tranhuy105.plateup.model.game;

import com.tranhuy105.plateup.constant.GameObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MapLoader {
    private static final Logger logger = LoggerFactory.getLogger(MapLoader.class);
    public static GameMap loadMap(String filename) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            int height = 0;
            int width = 0;

            // First pass to determine the dimensions
            while ((line = br.readLine()) != null) {
                height++;
                String[] parts = line.trim().split("\\s+");
                width = Math.max(width, parts.length);
            }

            GameMap gameMap = new GameMap(width, height);

            // Second pass to load the objects
            br.close();
            br = new BufferedReader(new FileReader(filename));
            int y = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                for (int x = 0; x < parts.length; x++) {
                    int id = Integer.parseInt(parts[x]);
                    GameObjectType type = GameObjectType.values()[id];
                    logger.debug("Creating object at ({}, {}): {}", x, y, type);
                    gameMap.setObjectAt(x, y, GameObjectFactory.createGameObject(type, x, y));
                }
                y++;
            }

            logger.info("Load Map Complete");
            return gameMap;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }
}
