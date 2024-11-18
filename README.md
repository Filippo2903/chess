# Chess Master

**Chess Master** is a multiplayer chess application that enables players to compete online using real-time interactions. The application employs a client-server architecture to manage game state and communication. 

## Features

### Client-Side Functionality
- **Chess Logic Implementation**:
  - Piece movement is implemented using an object-oriented approach with inheritance for defining specific movement rules of each chess piece.
  - The code validates the legality of moves, checking for:
    - Basic rule compliance (e.g., allowed moves for each piece).
    - Special moves like castling and en passant.
    - Board state conditions like checks.
  - Drag-and-drop functionality lets players interactively move pieces on the board.
- **Server Communication**:
  - Connects to the server using WebSockets.
  - Requests the server to start a game session.
  - Receives player color (white or black) when another client connects.
  - Synchronizes board state with the server during gameplay.

### Server-Side Functionality
- **Game Management**:
  - Handles connections from two clients and assigns player colors randomly.
  - Listens for moves from one client and mirrors them to the other client, ensuring consistent game state across clients.
  - Manages game sessions until the match concludes.
  - Closes communication at the end of the match.

## Installation

\
