package com.tranhuy105.plateup.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.core.Carriable;
import com.tranhuy105.plateup.model.core.Plate;
import com.tranhuy105.plateup.model.game.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.*;

public class GameSocketServer extends WebSocketServer {
    private static volatile GameSocketServer instance;
    private static final int PORT = 12345;
    private static final String mapFilePath = "src/main/resources/map.txt";
    private static final List<WebSocket> clients = Collections.synchronizedList(new ArrayList<>());
    private static final Logger logger = LoggerFactory.getLogger(GameSocketServer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private GameMap gameMap;
    private final Map<WebSocket, Player> playerMap = Collections.synchronizedMap(new HashMap<>());

    private GameSocketServer(String mapFilePath, int PORT) {
        super(new InetSocketAddress(PORT), Collections.singletonList(new Draft_6455()));
        try {
            gameMap = MapLoader.loadMap(mapFilePath);
        } catch (IOException e) {
            logger.error("Error loading map", e);
            System.exit(1);
        }
    }

    public static GameSocketServer getInstance() {
        if (instance == null) {
            synchronized (GameSocketServer.class) {
                if (instance == null) {
                    instance = new GameSocketServer(mapFilePath, PORT);
                }
            }
        }
        return instance;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        Player newPlayer = new Player(2, 2, String.valueOf(conn.getRemoteSocketAddress().getPort()));
        playerMap.put(conn, newPlayer);
        logger.info("New connection: " + conn.getRemoteSocketAddress());

        // Send current game state to the new player and broadcast the new player change
        sendInitialGameState(conn);
        broadcastPlayerState(newPlayer);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        Player leftPlayer = playerMap.remove(conn);
        broadcastPlayerLeft(leftPlayer);
        logger.info("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.debug("Received: " + message);

        try {
            GameMessage gameMessage = objectMapper.readValue(message, GameMessage.class);
            handleGameMessage(conn, gameMessage);
        } catch (IOException e) {
            logger.error("Error parsing message", e);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error("Error on client: " + webSocket.getRemoteSocketAddress(), e);
    }

    @Override
    public void onStart() {
        logger.info("Socket Server Started at Port " + PORT);
    }

    private void handleGameMessage(WebSocket conn, GameMessage gameMessage) {
        Player player = playerMap.get(conn);
        if (player == null) {
            return;
        }

        try {
            switch (gameMessage.getAction()) {
                case "MOVE":
                    int deltaX = gameMessage.getDeltaX();
                    int deltaY = gameMessage.getDeltaY();
                    player.move(deltaX, deltaY, gameMap);
                    broadcastPlayerState(player);
                    break;

                case "PICKUP":
                    GameObject pickupObject = gameMap.getObjectAt(player.getFocusPosition());
                    player.pickUpFrom(pickupObject);
                    broadCastPlayerAction(pickupObject, player);
                    break;

                case "DROP":
                    GameObject dropObject = gameMap.getObjectAt(player.getFocusPosition());
                    player.drop(dropObject);
                    broadCastPlayerAction(dropObject, player);
                    break;

                case "PROCESS":
                    GameObject kitchenBlock = gameMap.getObjectAt(player.getFocusPosition());
                    player.processFoodIngredientOnKitchenBlock(kitchenBlock);
                    broadCastPlayerAction(kitchenBlock, player);
                    break;

                default:
                    logger.warn("Unknown command: " + gameMessage.getAction());
                    sendNotification(conn, "Unknown command: " + gameMessage.getAction());
            }
        } catch (InvalidActionException ex) {
            sendNotification(conn, ex.getMessage());
        }
    }

    public void sendNotification(WebSocket conn, String message) {
        try {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "INVALID_ACTION");
            msg.put("message", message);
            // only sending to that player
            conn.send(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            logger.error("Error notify invalid action", e);
        }
    }

    public void broadcastTimingEvent(String message, GameObject updatedGameObject) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "ASYNC_PROCESS_COMPLETE");
            update.put("message", message);
            update.put("data", GameObject.getObjectRepresentation(updatedGameObject));
            String updateJson = objectMapper.writeValueAsString(update);
            synchronized (clients) {
                for (WebSocket client : clients) {
                    client.send(updateJson);
                }
            }
        } catch (IOException e) {
            logger.error("Error broadcasting notification", e);
        }
    }

    private void sendInitialGameState(WebSocket conn) {
        try {
            Map<String, Object> gameState = getGameState();
            gameState.put("type", "INITIAL_STATE");
            conn.send(objectMapper.writeValueAsString(gameState));
            synchronized (clients) {
                for (WebSocket client: clients) {
                    sendNotification(client, "A new player has join the room!");
                }
            }
        } catch (IOException e) {
            logger.error("Error sending initial game state", e);
        }
    }

    public void broadcastPlayerState(Player player) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "PLAYER_UPDATE");
            update.put("player", Player.getPlayerState(player));
            String updateJson = objectMapper.writeValueAsString(update);
            synchronized (clients) {
                for (WebSocket client : clients) {
                    client.send(updateJson);
                }
            }
        } catch (IOException e) {
            logger.error("Error broadcasting player update", e);
        }
    }

    private void broadcastPlayerLeft(Player player) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "PLAYER_LEFT");
            update.put("player", Player.getPlayerState(player));
            String updateJson = objectMapper.writeValueAsString(update);
            synchronized (clients) {
                for (WebSocket client : clients) {
                    client.send(updateJson);
                }
            }
        } catch (IOException e) {
            logger.error("Error broadcasting player update", e);
        }
    }

    private void broadCastPlayerAction(GameObject gameObject, Player player) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("type", "PLAYER_ACTION");
            update.put("player", Player.getPlayerState(player));
            update.put("gameObject", GameObject.getObjectRepresentation(gameObject));
            String updateJson = objectMapper.writeValueAsString(update);
            synchronized (clients) {
                for (WebSocket client : clients) {
                    client.send(updateJson);
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Error broadcasting player action update", e);
        }
    }

    private Map<String, Object> getGameState() {
        Map<String, Object> gameState = new HashMap<>();
        gameState.put("gameMap", gameMap.getMapState());
        List<Map<String, Object>> playerStates = getPlayerStates();
        gameState.put("players", playerStates);
        return gameState;
    }

    private List<Map<String, Object>> getPlayerStates() {
        List<Map<String, Object>> playerStates = new ArrayList<>();
        for (Player player : playerMap.values()) {
            playerStates.add(Player.getPlayerState(player));
        }
        return playerStates;
    }



    private void printGameMap() {
        if (gameMap != null) {
            logger.info("Game Map:");
            for (int y = 0; y < gameMap.getHeight(); y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < gameMap.getWidth(); x++) {
                    GameObject obj = gameMap.getObjectAt(x, y);
                    if (obj == null) {
                        row.append("null ");
                    } else {
                        row.append(obj.getObjectType()).append(" ");
                    }
                }
                logger.info(row.toString());
            }
        } else {
            logger.error("Game map is null. Unable to print.");
        }
    }
}

