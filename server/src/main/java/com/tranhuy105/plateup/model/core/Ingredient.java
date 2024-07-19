package com.tranhuy105.plateup.model.core;

import com.tranhuy105.plateup.constant.CarriableType;
import com.tranhuy105.plateup.exception.InvalidActionException;
import com.tranhuy105.plateup.model.game.Player;
import com.tranhuy105.plateup.network.GameSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Ingredient implements Carriable{
    protected CarriableType type;
    protected boolean isRaw;
    // time to process if is raw
    protected long processTime;
    protected boolean isProcessing = false;
    private final ScheduledExecutorService executors;
    private static final Logger logger = LoggerFactory.getLogger(Ingredient.class);

    public Ingredient(CarriableType type, boolean isRaw, long processTime) {
        this.type = type;
        this.isRaw = isRaw;
        this.processTime = processTime;
        if (isRaw) {
            this.executors = Executors.newSingleThreadScheduledExecutor();
        } else {
            this.executors = null;
        }
    }

    @Override
    public void drop(Player player, ContainerObject containerObject) {
        if (player.isCarryingItem()) {
            if (containerObject.addItem(this)){
                player.setCarryingItem(null);
            } else {
                logger.warn("Fail to add item to "+ containerObject.containerType);
                throw new InvalidActionException("Fail to perform action, reason unknown");
            }
        } else {
            logger.warn("This player is not carrying any item!");
            throw new InvalidActionException("You don't carrying any item!");
        }
    }

    private void shutDownExecutor() {
        if (executors != null && !executors.isShutdown()) {
            logger.info("Food process complete, shutting down executor...");
            executors.shutdownNow();
        }
    }

    public void processRawFood(Player player, KitchenBlock block) {
        if (isProcessing) {
            throw new InvalidActionException("Currently Processing, please wait");
        }
        if (isRaw) {
            player.setBusy(true);
            isProcessing = true;
            executors.schedule(() -> {
                this.isRaw = false;
                this.isProcessing = false;
                player.setBusy(false);
                String message = type.name() + " process completed 💁";
                GameSocketServer.getInstance().broadcastTimingEvent(message, block);
                GameSocketServer.getInstance().broadcastPlayerState(player);
                logger.info(message);
                shutDownExecutor();
            }, processTime, TimeUnit.MILLISECONDS);
        } else {
            logger.warn("This food is not raw and dont need to be processed");
            throw new InvalidActionException("This food is not raw and don't need to processed");
        }
    };

    @Override
    public CarriableType getCarriable() {
        return this.type;
    }

    public boolean isRaw() {
        return this.isRaw;
    }

    public boolean isProcessing() {
        return isProcessing;
    }
}
