const canvas = document.getElementById("gameCanvas");
const ctx = canvas.getContext("2d");
const TILE_SIZE = 48;
let gameState = {
  players: [],
  gameMap: [],
};

const images = {};
const imageKeys = [
  0,
  1,
  2,
  3,
  4,
  5,
  6,
  7,
  8,
  9,
  17,
  16,
  11,
  10,
  12,
  13,
  14,
  15,
  18,
  20,
  19,
  99,
  "PLATE",
  "PLATE_DIRT",
  "BREAD",
  "HOTDOG",
  "SAUSAGE",
  "RAW_CHEESE",
  "COOKED_CHEESE",
  "RAW_TOMATO",
  "COOKED_TOMATO",
  "RAW_PIZZA",
  "COOKED_PIZZA",
  "PLAYER_UP",
  "PLAYER_UP_BUSY",
  "PLAYER_DOWN",
  "PLAYER_LEFT",
  "PLAYER_RIGHT",
  "PLAYER_DOWN_BUSY",
  "PLAYER_LEFT_BUSY",
  "PLAYER_RIGHT_BUSY",
  "DISH_WASHER_WATER",
];

function loadImage(key) {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.src = `sprites/${key}.png`;
    img.onload = () => resolve({ key, img });
    img.onerror = reject;
  });
}

function connectWebSocket() {
  const socket = new WebSocket(
    import.meta.env.VITE_SOCKET_URL
  );

  socket.onopen = () => {
    console.log("Connected to WebSocket server");
  };

  socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    // console.log(data);

    switch (data.type) {
      case "INITIAL_STATE":
        gameState = data;
        renderGame();
        break;
      case "PLAYER_UPDATE":
        updatePlayer(data.player);
        renderGame();
        break;
      case "PLAYER_ACTION":
        updatePlayer(data.player);
        updateGameObject(data.gameObject);
        renderGame();
        break;
      case "PLAYER_LEFT":
        removePlayer(data.player);
        renderGame();
        break;
      case "INVALID_ACTION":
        showToast(data.message);
        break;
      case "ASYNC_PROCESS_COMPLETE":
        showToast(data.message);
        updateGameObject(data.data);
        renderGame();
        break;
      case "GAME_RESET":
        gameState = data;
        renderGame();
        showToast("Game has been reset!");
        break;
      default:
        console.warn("Unknown message type:", data.type);
    }
  };

  socket.onclose = () => {
    console.log("Disconnected from WebSocket server");
  };

  socket.onerror = (error) => {
    console.error("WebSocket Error:", error);
  };

  document.addEventListener("keydown", (event) => {
    let dx = 0;
    let dy = 0;
    switch (event.key) {
      case "ArrowUp":
        dy = -1;
        break;
      case "ArrowDown":
        dy = 1;
        break;
      case "ArrowLeft":
        dx = -1;
        break;
      case "ArrowRight":
        dx = 1;
        break;
      case "p":
        sendPickup();
        return;
      case "d":
        sendDrop();
        return;
      case "o":
        sendProcess();
        return;
      case "r":
        sendReset();
        return;
      default:
        return;
    }
    sendMove(dx, dy);
  });

  function sendMove(dx, dy) {
    const message = JSON.stringify({
      action: "MOVE",
      deltaX: dx,
      deltaY: dy,
    });
    socket.send(message);
  }

  function sendReset() {
    const message = JSON.stringify({
      action: "RESET",
    });
    socket.send(message);
  }

  function sendPickup() {
    const message = JSON.stringify({
      action: "PICKUP",
    });
    socket.send(message);
  }

  function sendDrop() {
    const message = JSON.stringify({
      action: "DROP",
    });
    socket.send(message);
  }

  function sendProcess() {
    const message = JSON.stringify({
      action: "PROCESS",
    });
    socket.send(message);
  }
}

Promise.all(imageKeys.map(loadImage))
  .then((loadedImages) => {
    loadedImages.forEach(({ key, img }) => {
      images[key] = img;
    });
    renderGame();
    connectWebSocket();
  })
  .catch((error) => {
    console.error("Error loading images:", error);
  });

function renderGame() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  renderMap(gameState.gameMap);
  renderPlayers(gameState.players);
}

function updateGameObject(gameObjectUpdate) {
  const gameObject = gameState.gameMap.find(
    (obj) =>
      obj.position.x === gameObjectUpdate.position.x &&
      obj.position.y === gameObjectUpdate.position.y
  );
  if (gameObject) {
    Object.assign(gameObject, gameObjectUpdate);
  } else {
    gameState.gameMap.push(gameObjectUpdate);
  }
}

function updatePlayer(playerUpdate) {
  const player = gameState.players.find(
    (p) => p.name === playerUpdate.name
  );
  if (player) {
    player.position = playerUpdate.position;
    player.carrying = playerUpdate.carrying;
    player.facingDirection = playerUpdate.facingDirection;
    player.isBusy = playerUpdate.isBusy;
  } else {
    gameState.players.push(playerUpdate);
  }
}

function removePlayer(playerUpdate) {
  const playerIndex = gameState.players.findIndex(
    (p) => p.name === playerUpdate.name
  );
  showToast(
    "player " +
      gameState.players[playerIndex].name +
      " left the game!"
  );
  if (playerIndex !== -1) {
    gameState.players.splice(playerIndex, 1);
  }
}

function getTileImage(id) {
  return images[id];
}

function renderMap(gameMap) {
  gameMap.forEach((tile) => {
    renderGameObject(tile);
  });
}

function renderPlayers(players) {
  players.forEach((player) => {
    renderPlayer(player);
  });
}

function showToast(message) {
  Toastify({
    text: message,
    duration: 1500,
    newWindow: true,
    close: true,
    gravity: "top",
    position: "right",
    textColor: "#e0e0e0",
  }).showToast();
}

function renderGameObject(tile) {
  const thisX = tile.position.x;
  const thisY = tile.position.y;

  const img = getTileImage(tile.id);
  if (!tile.isContainer && !tile.isPickupAble) {
    if (img) {
      ctx.drawImage(
        img,
        thisX * TILE_SIZE,
        thisY * TILE_SIZE,
        TILE_SIZE,
        TILE_SIZE
      );
    }
  } else {
    // render the floor
    drawImage(0, thisX, thisY);

    // render the actual object on the floor
    if (tile.type === "DISH_WASHER") {
      if (tile.isBusy) {
        drawImage("DISH_WASHER_WATER", thisX, thisY);
      } else {
        drawImage(tile.id, thisX, thisY);
      }
    } else if (img) {
      ctx.drawImage(
        img,
        thisX * TILE_SIZE,
        thisY * TILE_SIZE,
        TILE_SIZE,
        TILE_SIZE
      );
    }

    // if the object actually conataing an item then render it on top as well
    if (
      tile.items !== null &&
      tile.items.length > 0 &&
      tile.type !== "PLATE_SOURCE"
    ) {
      const item = tile.items[0];
      drawCarriable(item, thisX, thisY);
    } else if (tile.type === "PLATE_SOURCE") {
      if (tile.items.length === 0) {
        drawImage(99, thisX, thisY);
      } else {
        drawImage(tile.id, thisX, thisY);
      }
    }
  }
}

function renderPlayer(player) {
  const thisX = player.position.x;
  const thisY = player.position.y;
  const dir = player.facingDirection;
  switch (dir) {
    case "UP":
    case "DOWN":
    case "LEFT":
    case "RIGHT":
      const busy = player.isBusy ? "_BUSY" : "";
      // console.log("PLAYER_" + dir + busy);
      drawImage("PLAYER_" + dir + busy, thisX, thisY);
      break;
    default:
      console.warn("Unknown direction: " + dir);
  }

  if (player.carrying != null) {
    drawCarriable(player.carrying, thisX, thisY);
  }
}

function drawImage(
  id,
  x,
  y,
  offsetX = 0,
  offsetY = 0,
  size = TILE_SIZE
) {
  const img = getTileImage(id);
  if (img) {
    ctx.drawImage(
      img,
      x * TILE_SIZE + offsetX,
      y * TILE_SIZE + offsetY,
      size,
      size
    );
  } else {
    console.warn(`image with id ${id} not found!`);
  }
}

function drawCarriable(item, thisX, thisY) {
  const carriableType = item.carriable;
  switch (carriableType) {
    case "PLATE":
      drawImage(
        item.isDirt ? "PLATE_DIRT" : "PLATE",
        thisX,
        thisY
      );
      if (item.currentItem != null && item.dish == null) {
        if (item.currentItem.raw) {
          drawImage(
            "RAW_" + item.currentItem.carriable,
            thisX,
            thisY
          );
        } else {
          drawImage(
            "COOKED_" + item.currentItem.carriable,
            thisX,
            thisY
          );
        }
      } else if (
        item.currentItem == null &&
        item.dish != null
      ) {
        if (item.dish.name === "HOTDOG") {
          drawImage(item.dish.name, thisX, thisY);
        } else if (item.dish.name === "PIZZA") {
          drawImage(
            item.dish.cook ? "COOKED_PIZZA" : "RAW_PIZZA",
            thisX,
            thisY
          );
        }
      }
      break;
    case "CHEESE":
    case "TOMATO":
      if (item.raw) {
        drawImage("RAW_" + carriableType, thisX, thisY);
      } else {
        drawImage("COOKED_" + carriableType, thisX, thisY);
      }
      break;
    case "SAUSAGE":
    case "BREAD":
    case "HOTDOG":
      drawImage(carriableType, thisX, thisY);
      break;
    default:
      break;
  }
}

document.addEventListener("DOMContentLoaded", (event) => {
  const popup = document.getElementById("tutorialPopup");
  const closeButton =
    document.querySelector(".close-button");

  popup.style.display = "flex";

  closeButton.addEventListener("click", () => {
    popup.style.display = "none";
  });
});
