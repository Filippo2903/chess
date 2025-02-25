# Chess

**Chess** is a multiplayer chess application that enables players to compete online using real-time interactions. The application employs a client-server architecture to manage game state and communication. 

## Features

### Client-Side Functionality
- **Server Communication**:
  - Connects to the server using WebSockets.
  - Requests the server to start a game session.
  - Receives player color (white or black) when another client connects.
  - Synchronizes board state with the server during gameplay.
- **Chess Logic Implementation**:
  - Drag-and-drop functionality lets players interactively move pieces on the board.
  - Piece movement is implemented using an object-oriented approach with inheritance for defining specific movement rules of each chess piece.
  - The code validates the legality of moves, checking for:
    - Basic rule compliance (e.g., allowed moves for each piece).
    - Special moves like castling and en passant.
    - Board state conditions like checks.

### Server-Side Functionality
- **Game Management**:
  - Handles connections from two clients and assigns player colors randomly.
  - Listens for moves from one client and mirrors them to the other client, ensuring consistent game state across clients.
  - Manages game sessions until the match concludes.
  - Closes communication at the end of the match.

## Installation

### Usage
 - Launch the server script.
 - Start two clients.
 - Press the `Start` button to find a game.
 - Once both players are connected, the server assigns colors and starts the game.
 - Drag and drop pieces to make a move. The server ensures synchronization between clients.
 - The game ends when one player checkmates.

### Folder Structure
```
chess-master/
 ├── client/
 │ ├── assets/ # Static resources (images)
 │ ├── src/ # Client-side logic (piece movement, drag-and-drop, sounds)
 │ └── client.iml # IntelliJ IDEA module file
 ├── server/
 │ ├── src/ # Server-side scripts for managing connections and game state
 | └── server.iml # IntelliJ IDEA module file
 ├── lib/ # Graphics Library
 ├── utils/ # Utility scripts
 ├── .gitignore # Git ignore rules
 ├── README.md # Project documentation
 └── chess.iml # IntelliJ IDEA module file
```
