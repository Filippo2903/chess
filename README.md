# Chess - Multiplayer Online

A real-time multiplayer Chess application built in **Java** using a **Client-Server** architecture. This project enables two players to compete online with synchronized game states, move validation, and an interactive GUI.

## Features

### Client-Side
* **Interactive GUI:** Drag-and-drop interface for moving pieces.
* **Real-time Communication:** Connects to the server via Java Sockets.
* **Move Validation:**
    * Legal moves for all pieces.
    * Special moves: Castling and En Passant.
    * Check/Checkmate detection.
* **State Synchronization:** Automatically updates the board when the opponent moves.

### Server-Side
* **Session Management:** Handles multiple client connections.
* **Matchmaking:** Pairs two clients into a game session.
* **State Mirroring:** Relays move between players to maintain game consistency.
* **Color Assignment:** Randomly assigns White/Black to players.

## Tech Stack

* **Language:** Java
* **Networking:** Java Sockets (TCP)
* **GUI:** Java Swing / AWT
* **IDE:** IntelliJ IDEA

## Project Structure
```text
chess/
├── client/          # Client source code
│   ├── src/         # Game logic, input handling, and GUI
│   └── assets/      # Static resources (piece images, sounds)
├── server/          # Server source code
│   ├── src/         # Connection handling and game state management
├── lib/             # External graphics libraries/dependencies
├── utils/           # Shared utility classes
└── README.md        # Project documentation
```

## Installation & Usage

### Prerequisites
* Java Development Kit (JDK) 8 or higher.
* IntelliJ IDEA (recommended).

### Option 1: Running with JARs (Recommended)

1.  **Download the Release**
    Go to the **Releases** section of this repository and download the latest version files:
    * `server.jar`
    * `client.jar`

2.  **Start the Server**
    Open a terminal/command prompt in the folder where you downloaded the files and run:
    ```bash
    java -jar server.jar
    ```
    The server will start listening for connections.

3.  **Start the Clients**
    Open a new terminal window and run:
    ```bash
    java -jar client.jar
    ```
    Repeat this step in another terminal to launch the second player.

### Option 2: Building from Source

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/Filippo2903/chess
    ```

2.  **Open in IDE**
    Open the project folder in IntelliJ IDEA. Ensure the project JDK is configured correctly.

3.  **Run the Server**
    Navigate to the server source package and run the main server class.

4.  **Run the Clients**
    Navigate to the client source package and run the main client class. Run it a second time to simulate the opponent.

## Playing the Game
1. Click **Start** to connect.
2. Once two players are connected, the game begins automatically.