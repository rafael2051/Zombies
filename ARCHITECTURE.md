# Zombies Game - Architecture Documentation

## System Overview

The Zombies game is a **2D top-down multiplayer survival game** built with a **client-server architecture** in Java. Players must survive against waves of zombies while communicating with a central server to synchronize game state with other players.

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           GAME INFRASTRUCTURE                               │
└─────────────────────────────────────────────────────────────────────────────┘

                           ┌──────────────────┐
                           │  Main Server     │
                           │  (ServerApp)     │
                           │  Port: 8080      │
                           └────────┬─────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
              ┌──────────┐    ┌──────────┐    ┌──────────┐
              │ Client 1 │    │ Client 2 │    │ Client N │
              │(App.exe) │    │(App.exe) │    │(App.exe) │
              └────┬─────┘    └────┬─────┘    └────┬─────┘
                   │               │               │
     ┌─────────────┴───────────────┴───────────────┴─────────────┐
     │                                                             │
     ▼                                                             ▼
┌─────────────────────────────────────┐     ┌─────────────────────────────────┐
│      TCP Socket Connection          │     │   UDP Datagram Communication    │
│         (Synchronization)           │     │    (Player Movements/Bullets)   │
└─────────────────────────────────────┘     └─────────────────────────────────┘
     │                                             │
     ▼                                             ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                         GAME CLIENT LAYER (gameSrc/)                         │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────┐  ┌──────────────────────────────────────────┐  │
│  │  Window/Rendering      │  │  Game Logic (game/logic/)                │  │
│  │  - Window.java         │  │  ┌─────────────────────────────────────┐│  │
│  │  - Handles rendering   │  │  │ Player Management                   ││  │
│  │  - Input handling      │  │  │ - Player.java                       ││  │
│  │  - Menu system         │  │  │ - Movement & collision              ││  │
│  │                        │  │  └─────────────────────────────────────┘│  │
│  └────────────────────────┘  │  ┌─────────────────────────────────────┐│  │
│                              │  │ Zombie Management                   ││  │
│  ┌────────────────────────┐  │  │ - Zombie.java                       ││  │
│  │  Networking (APIs)     │  │  │ - ZombieStandard.java               ││  │
│  │  - ApiPlayerClient     │  │  │ - AI behavior                       ││  │
│  │  - ApiZombieClient     │  │  └─────────────────────────────────────┘│  │
│  │                        │  │  ┌─────────────────────────────────────┐│  │
│  └────────────────────────┘  │  │ Weapon/Ammunition System            ││  │
│                              │  │ - Bullet.java                       ││  │
│  ┌────────────────────────┐  │  │ - BulletStandard.java               ││  │
│  │  Networking (Sockets)  │  │  │ - Projectile physics                ││  │
│  │  - GameClient.java     │  │  └─────────────────────────────────────┘│  │
│  │  - GameServer.java     │  │                                          │  │
│  │  - TCP/UDP handlers    │  │  ┌─────────────────────────────────────┐│  │
│  │                        │  │  │ Threading & Concurrency             ││  │
│  │                        │  │  │ - MoveLocalPlayerTask               ││  │
│  │                        │  │  │ - ControlOtherPlayersTask           ││  │
│  │                        │  │  │ - ControlShootTask                  ││  │
│  │                        │  │  │ - ZombieMoveTask                    ││  │
│  └────────────────────────┘  │  └─────────────────────────────────────┘│  │
│                              │                                          │  │
│  ┌────────────────────────┐  │  ┌─────────────────────────────────────┐│  │
│  │  Game Assets           │  │  │ Key Manager                         ││  │
│  │  - Images (Top-Down-   │  │  │ - Keys.java                         ││  │
│  │    Survivor assets)    │  │  │ - Input processing                  ││  │
│  │  - Animations          │  │  └─────────────────────────────────────┘│  │
│  │  - Sprites             │  │                                          │  │
│  └────────────────────────┘  └──────────────────────────────────────────┘  │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────────────────────────────────┐
│                    MAIN SERVER LAYER (mainServer/)                           │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────┐  ┌────────────────────────────────────────────┐  │
│  │  Server.java         │  │  Game Control & Logic                      │  │
│  │  - Singleton pattern  │  │  ┌──────────────────────────────────────┐│  │
│  │  - Listens on port    │  │  │ ControlRound.java                   ││  │
│  │    8080               │  │  │ - Round management                  ││  │
│  │  - Accepts client     │  │  │ - Zombie spawning logic             ││  │
│  │    connections        │  │  │ - Game state control                ││  │
│  │  - Maintains client   │  │  └──────────────────────────────────────┘│  │
│  │    list               │  │                                            │  │
│  └──────────────────────┘  │  ┌──────────────────────────────────────┐│  │
│                            │  │ Threading                            ││  │
│  ┌──────────────────────┐  │  │ ┌──────────────────────────────────┐││  │
│  │ ThreadClientSocket   │  │  │ │ ThreadClientSocket per client   │││  │
│  │ - One per client     │  │  │ │ - Handles client messages       │││  │
│  │ - Processes messages │  │  │ │ - Broadcasts game state         │││  │
│  │ - Broadcasts updates │  │  │ │ - Synchronizes players          │││  │
│  │                      │  │  │ └──────────────────────────────────┘││  │
│  └──────────────────────┘  │  └──────────────────────────────────────┘│  │
│                            │                                            │  │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## Component Description

### **1. Client Layer (gameSrc/)**

#### **Window & Rendering**
- **Window.java**: Handles the game window, rendering, and main game loop
  - Resolution: 1000x1000 pixels
  - Menu system management
  - Input event handling

#### **Networking - Client Side**
- **GameClient.java**: 
  - Connects to main server on TCP (port 8080)
  - Sends "InitialConnection" message to server
  - Receives list of other players' IPs
  - Handles game state synchronization
  - Uses Singleton pattern

- **GameServer.java**: 
  - Receives UDP datagrams from other players
  - Uses dynamic port assignment: `9765 + playerId`
  - Listens for player movement and weapon data
  - Processes incoming messages from other clients

#### **Game Logic**
- **Player.java**: Represents a player character
  - Position (x, y), dimensions (width, height)
  - Animation states: walking, shooting, reloading
  - Health points (HP) and ammunition tracking
  - Image assets for different states

- **Zombie.java & ZombieStandard.java**: Enemy AI
  - Spawns in waves
  - Movement behavior
  - Attack mechanics
  - Animation handling

- **Bullet.java & BulletStandard.java**: Projectile system
  - Bullet trajectory and physics
  - Collision detection
  - Speed and size parameters

#### **Threading**
Multi-threaded architecture for concurrent operations:
- **MoveLocalPlayerTask**: Handles local player movement input
- **ControlOtherPlayersTask**: Updates other players' positions from server
- **ControlShootTask**: Processes shooting mechanics
- **ZombieMoveTask**: Controls zombie AI and movement

#### **Game Assets**
- **images/** folder containing:
  - Top-Down Survivor sprite sheets (feet, weapons, etc.)
  - Zombie animations (attack, move)
  - Menu images
  - Demo assets

---

### **2. Server Layer (mainServer/)**

#### **Core Server**
- **Server.java** (Singleton):
  - Listens on TCP port **8080**
  - Accepts client connections
  - Maintains list of connected clients
  - Spawns `ThreadClientSocket` for each client
  - Manages game running state

#### **Client Connection Handler**
- **ThreadClientSocket.java**:
  - One thread per connected client
  - Receives messages from client
  - Broadcasts game state updates to all other clients
  - Handles player synchronization
  - Manages player disconnections

#### **Game Control**
- **ControlRound.java** (Singleton):
  - Manages game rounds/waves
  - Controls zombie spawning logic
  - Coordinates game progression
  - Broadcasts game state changes

---

## Communication Flow

### **Initial Connection**
1. Client launches `App.java`
2. `GameClient` connects to server on **TCP port 8080**
3. Sends "InitialConnection" message
4. Server responds with list of other connected players' IPs
5. Client allocates UDP port: `9765 + clientId`

### **During Gameplay**
- **TCP (Port 8080)**: Server broadcasts synchronized game state
  - Player positions
  - Zombie spawns
  - Game status updates

- **UDP (Port 9765+N)**: P2P communication between clients
  - Local player movements
  - Bullet positions
  - Immediate action updates

### **Game State Synchronization**
1. Server tracks all active players via `ThreadClientSocket` threads
2. Client updates received through TCP
3. Local player updates sent via TCP to server
4. Server broadcasts to other clients
5. P2P UDP communication for low-latency actions

---

## Key Patterns & Design Decisions

| Pattern | Usage |
|---------|-------|
| **Singleton** | Server, GameClient, GameServer, ControlRound - Single instance across application |
| **Thread-based Concurrency** | Each client connection and game task runs in separate thread |
| **TCP/UDP Hybrid** | TCP for reliable game state, UDP for fast player updates |
| **Producer-Consumer** | Message queues between networking and game logic |
| **Observer Pattern** | Window updates when game state changes |

---

## Execution Flow

```
1. Start Main Server (ServerApp.java)
   └─> Server.getInstance().start()
   └─> Listen on port 8080

2. Start Game Client (App.java)
   └─> Create Window
   └─> Create GameClient
   └─> Connect to server (TCP)
   └─> Receive player list
   └─> Start GameServer (UDP listener)
   └─> Create player and zombie instances
   └─> Launch game tasks
   └─> Main game loop (render, update, handle input)

3. Gameplay Loop
   ├─> MoveLocalPlayerTask: Get keyboard input, update player position
   ├─> ControlShootTask: Handle shooting mechanics
   ├─> ZombieMoveTask: Update zombie AI
   ├─> ControlOtherPlayersTask: Fetch remote player positions
   ├─> Window.render(): Draw all entities
   └─> Repeat at ~25 FPS (40ms per frame)

4. Server Updates
   ├─> ThreadClientSocket receives player actions
   ├─> ControlRound updates zombie spawns
   ├─> Broadcasts state to all clients
   └─> Maintains game synchronization
```

---

## Technology Stack

- **Language**: Java
- **Graphics**: AWT (Abstract Window Toolkit)
- **Networking**: 
  - Sockets (TCP)
  - Datagram Sockets (UDP)
  - BufferedReader/PrintWriter for messages
- **Concurrency**: Java Threads
- **Assets**: PNG images (sprite sheets)
- **Build**: javac compilation, executed with JRE

---

## Scalability Considerations

- **Horizontal Scaling**: Multiple clients can connect to single server
- **Message Broadcasting**: Server uses list of `ThreadClientSocket` to broadcast updates
- **Thread Per Client**: Each connection gets dedicated thread for isolation
- **UDP for Performance**: Direct player-to-player UDP communication reduces server load

