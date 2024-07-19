# Introduction

**Plate Up** is a project inspired by the famous game with the same name, mostly because i want to improve my ~~broken~~ java core skills. Anyway, it focuses on:

- **Managing Game Objects**: Handling various game elements and their interactions.
- **WebSocket Connections**: Managing real-time communication between the server and clients. Just a little bit though, mostly focus on the Core Game Object.

Originally, this was just a terminal-based game, but I found it quite fun to work on. I thought, "Why not spice it up a bit?" And so, here we are.

This project isn’t intended to be a full-fledged game—it's more of a learning tool. So, fellow game devs, please don’t judge too harshly!


## Table of Contents

- Overview
- Features
- Installation
- Usage
- Contributing

## Overview

This project consists of a server that manages a game map and handles player interactions over WebSocket connections. The map is loaded from a file, and game objects are managed dynamically. The server uses SLF4J for logging and Jackson for JSON processing.

The game’s core functionality revolves around three main interfaces: `Container`, `Carriable`, and `Pickupable`.

## Features

- WebSocket server for real-time game communication
- Dynamic game map loading from a file
- Handling of multiple players
- Logging of game map and server actions
- Object serialization and deserialization using Jackson

## Installation

You can try the deployed version of the client. The link is available in this repository. But if you’d rather run it locally on your machine, you can find the setup instructions below!

**⚠️** Please note that since the server is deployed on Render, it may go to sleep after a period of inactivity. If you experience slow loading, please be patient as the instance may need to be wake up.

### Prerequisites

Ensure you have the following dependencies installed:

- Java Development Kit (JDK)
- Maven (for dependency management)

### Clone Repository

Clone the repository to your local machine:

    git clone https://github.com/tranhuy105/PlateUp.git
    cd GameSocketServer

### Build

Build the project using Maven:

    mvn clean install

## Usage

### Start the Server

To start the WebSocket server, run:

    java -jar .\target\plate-up-1.0-SNAPSHOT.jar 

The server will start on port 12345 by default. You can change the port by modifying the PORT variable in the GameSocketServer class.

### Game Map

The game map is loaded from a file specified in the mapFilePath variable in the GameSocketServer class. Ensure you have a valid map file at the specified path.

Example Map File (src/main/resources/map.txt)

    1 0 0 2
    0 0 2 1
    0 2 0 0
    2 1 0 0

The numbers represent different types of game objects, each gameObjectId can be found in the constant `GameObjectType` enum!

## Contributing

Contributions are welcome! Please fork this repository and submit pull requests.

1. Fork the repository.
2. Create a new branch with your feature or fix.
3. Commit your changes.
4. Push to the branch.
5. Create a new pull request.
