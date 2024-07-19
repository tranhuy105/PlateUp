package com.tranhuy105.plateup;

import com.tranhuy105.plateup.network.GameSocketServer;

public class Main {
//    public static void main(String[] args) throws InterruptedException {
//        Player player = new Player();
//        IngredientSource cheeseSource = new IngredientSource(CarriableType.CHEESE);
//        IngredientSource tomatoSource = new IngredientSource(CarriableType.TOMATO);
//        IngredientSource breadSource = new IngredientSource(CarriableType.BREAD);
//        IngredientSource sausageSource = new IngredientSource(CarriableType.SAUSAGE);
//        PlateSource plateSource = new PlateSource(4);
//        Oven oven = new Oven();
//
//        KitchenBlock kitchenBlock = new KitchenBlock();
//        KitchenBlock processBlock = new KitchenBlock();
//
//        player.pickUpFrom(plateSource);
//        player.pickUpFrom(plateSource);
//        player.pickUpFrom(plateSource);
//        System.out.println(plateSource.remainingPlateCount());
//        player.drop(kitchenBlock);
//
//        player.pickUpFrom(cheeseSource);
//        player.drop(processBlock);
//        player.processFoodIngredientOnKitchenBlock(processBlock);
//        Thread.sleep(5500);
//        player.pickUpFrom(processBlock);
//        player.drop(kitchenBlock);
//
//        player.pickUpFrom(tomatoSource);
//        player.drop(processBlock);
//        player.processFoodIngredientOnKitchenBlock(processBlock);
//        Thread.sleep(5500);
//        player.pickUpFrom(processBlock);
//        player.drop(kitchenBlock);
//
//        player.pickUpFrom(kitchenBlock);
//        player.drop(oven);
//        Thread.sleep(5500);
//        player.pickUpFrom(oven);
//
//        System.out.println(((Plate)player.getCarryingItem()).getDish().isCook());
//    }

//        public static void main(String[] args) {
//            String filename = "src/main/resources/map.txt";
//            Player player = new Player(0, 0);
//
//            try {
//                GameMap gameMap = MapLoader.loadMap(filename);
//                System.out.println("Map loaded successfully!");
//                System.out.println("Width: "+gameMap.getWidth());
//                System.out.println("Height: "+gameMap.getHeight());
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//                String input;
//
//                while (true) {
//                    System.out.print("Enter command (move x y, pickup, drop, process, exit, status): ");
//                    input = reader.readLine();
//
//                    if (input.equalsIgnoreCase("exit")) {
//                        break;
//                    }
//
//                    String[] commands = input.split(" ");
//                    switch (commands[0].toLowerCase()) {
//                        case "move":
//                            if (commands.length == 3) {
//                                try {
//                                    int deltaX = Integer.parseInt(commands[1]);
//                                    int deltaY = Integer.parseInt(commands[2]);
//                                    player.move(deltaX, deltaY, gameMap);
//                                    System.out.println("Moved to: (" + player.getX() + ", " + player.getY() + ")");
//                                } catch (NumberFormatException e) {
//                                    System.out.println("Invalid move command. Please enter valid numbers.");
//                                }
//                            } else {
//                                System.out.println("Please provide two numbers for movement.");
//                            }
//                            break;
//
//                        case "pickup":
//                            GameObject pickupObject = gameMap.getObjectAt(player.getX(), player.getY());
//                            player.pickUpFrom(pickupObject);
//                            break;
//
//                        case "drop":
//                            GameObject dropObject = gameMap.getObjectAt(player.getX(), player.getY());
//                            player.drop(dropObject);
//                            break;
//
//                        case "process":
//                            GameObject kitchenBlock = gameMap.getObjectAt(player.getX(), player.getY());
//                            player.processFoodIngredientOnKitchenBlock(kitchenBlock);
//                            break;
//                        case "status":
//                            player.printStatus();
//                            break;
//                        default:
//                            System.out.println("Unknown command. Please use 'move', 'pickup', 'drop', 'process', or 'exit'.");
//                    }
//
//                    GameObject currentObject = gameMap.getObjectAt(player.getX(), player.getY());
//                    System.out.println("Current Object at (" + player.getX() + ", " + player.getY() + "): " + currentObject.getGameObjectType());
//                }
//            } catch (IOException e) {
//                System.err.println("Error loading map: " + e.getMessage());
//            }
//        }

    public static void main(String[] args) {
        GameSocketServer server = GameSocketServer.getInstance();
        server.start();
    }
}
