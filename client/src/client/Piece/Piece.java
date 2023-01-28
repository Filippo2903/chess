package client.Piece;

import client.Client;
import client.Game;

import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public abstract class Piece extends JLabel {

    // Label cell size
    protected static int cellSize;
    protected Point currentPosition;
    protected final PlayerColor pieceColor;

    protected boolean promoted = false;
    private final Point newPosition = new Point();

    public Piece(PlayerColor playerColor, Point startPosition) {
        super();

        this.pieceColor = playerColor;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        currentPosition = startPosition;

        this.setVisible(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean undo;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    undo = false;
                    Piece.this.getParent().setComponentZOrder(Piece.this, 0);
                } else {
                    undo = true;

                    Piece.this.setPosition(
                            Piece.this.currentPosition.x,
                            Piece.this.currentPosition.y
                    );
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (undo) return;

                int x = (int) Math.floor(((double) Piece.this.newPosition.x + (cellSize >> 1)) / cellSize);
                int y = (int) Math.floor(((double) Piece.this.newPosition.y + (cellSize >> 1)) / cellSize);

                if (x < 0 || x > 7 || y < 0 || y > 7) {
                    undo = true;

                    Piece.this.setPosition(
                            Piece.this.currentPosition.x,
                            Piece.this.currentPosition.y
                    );
                }

                Piece.this.newPosition.x = Piece.this.getLocation().x + e.getX() - (cellSize >> 1);
                Piece.this.newPosition.y = Piece.this.getLocation().y + e.getY() - (cellSize >> 1);
                Piece.this.setLocation(Piece.this.newPosition.x, Piece.this.newPosition.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (undo) return;

                Point to = new Point(
                        (Piece.this.newPosition.x + (cellSize >> 1)) / cellSize,
                        (Piece.this.newPosition.y + (cellSize >> 1)) / cellSize
                );

                if (canMove(to)) {
                    Point prevPosition = new Point(currentPosition.x, currentPosition.y);

                    Piece.this.setPosition(to.x, to.y);

                    if (promoted) { // ! DEBUG THIS !
                        Client.sendMove(new Packet(prevPosition, currentPosition, PieceType.valueOf(Piece.this.getClass().getName())));
                    } else {
                        Client.sendMove(new Packet(prevPosition, currentPosition));
                    }

                    Thread recieveThread = new Thread(Client::receiveMove);
                    recieveThread.start();

                    Game.changePlayerTurn();

                    return;
                }

                // Modify the position
                Piece.this.setPosition(
                        Piece.this.currentPosition.x,
                        Piece.this.currentPosition.y
                );
            }
        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    public abstract boolean canMove(Point cell);

    public boolean isNotPlayerPiece() {
        return this.getColor() != Game.getPlayerColor();
    }

    public boolean isNotPlayerTurn() {
        return Game.getPlayerTurn() != Game.getPlayerColor();
    }

//        Piece[][] board = Game.getBoard();
//
//        // If is not the turn of the player turn
//        if (Game.getPlayerTurn() != Game.getPlayerColor()) {
//            return false;
//        }
//
//        // If the user tries to move a piece that isn't his
//        if (this.getColor() != Game.getPlayerColor()) {
//            return false;
//        }
//
//        switch (this.getType()) {
//            case PAWN -> {
//                boolean canMove = false;
//                int startPositionY = 6;
//                int enPassantPositionY = 3;
//
//                Point standardMove = new Point(0, -1);
//                Point doubleMove = new Point(0, -2);
//                Point[] takeMoves = {
//                        new Point(-1, -1),
//                        new Point(1, -1)
//                };
//
//                // Take move
//                for (Point move : takeMoves) {
//                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
//                            board[cell.y][cell.x] != null) {
//
//                        board[cell.y][cell.x].kill();
//                        canMove = true;
//                    }
//                }
//
//                // En passant
//                for (Point move : takeMoves) {
//                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
//                        board[cell.y + 1][cell.x] != null &&
//                        board[cell.y + 1][cell.x].getType() == PieceType.PAWN &&
//                        currentPosition.y == enPassantPositionY) {
//
//                        board[cell.y + 1][cell.x].kill();
//                        canMove = true;
//                    }
//                }
//
//                // If the cell in front is occupied
//                if (board[cell.y][cell.x] != null) {
//                    return false;
//                }
//
//                // Standard move
//                if (cell.x == currentPosition.x + standardMove.x && cell.y == currentPosition.y + standardMove.y) {
//                    canMove = true;
//                }
//
//                // Double move
//                if (cell.x == currentPosition.x + doubleMove.x && cell.y == currentPosition.y + doubleMove.y &&
//                    currentPosition.y == startPositionY) {
//                    canMove = true;
//                }
//
//                // Promote
//                if (canMove && cell.y == 0) {
//                    this.promote(PieceType.QUEEN);
//                }
//
//                return canMove;
//            }
//
//            case KNIGHT -> {
//                boolean canMove = false;
//
//                Point[] moves = {
//                        new Point(-2, -1),
//                        new Point(-1, -2),
//                        new Point(1, -2),
//                        new Point(2, -1),
//                        new Point(2, 1),
//                        new Point(1, 2),
//                        new Point(-1, 2),
//                        new Point(-2, 1)
//                };
//
//                for (Point move : moves) {
//                    if (cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) {
//                        canMove = true;
//                        break;
//                    }
//                }
//
//                if (canMove) {
//                    if (board[cell.y][cell.x] != null) {
//                        board[cell.y][cell.x].kill();
//                    }
//
//                    return true;
//                }
//            }
//
//            case BISHOP -> {
//                Point ghostBishop = new Point(currentPosition.x, currentPosition.y);
//                Point direction = diagonalMove(cell);
//
//                if (direction.x == 0 && direction.y == 0) {
//                    return false;
//                }
//
//                while (ghostBishop.x != cell.x - direction.x || ghostBishop.y != cell.y - direction.y) {
//                    ghostBishop.x += direction.x;
//                    ghostBishop.y += direction.y;
//
//                    if (board[ghostBishop.y][ghostBishop.x] != null) {
//                        return false;
//                    }
//                }
//
//                if (board[cell.y][cell.x] != null) {
//                    board[cell.y][cell.x].kill();
//                }
//
//                return true;
//            }
//
//            case ROOK -> {
//                Point ghostRook = new Point(currentPosition.x, currentPosition.y);
//                Point direction = straightMove(cell);
//
//                if (direction.x == 0 && direction.y == 0) {
//                    return false;
//                }
//
//                while (ghostRook.x != cell.x - direction.x || ghostRook.y != cell.y - direction.y) {
//                    ghostRook.x += direction.x;
//                    ghostRook.y += direction.y;
//
//                    if (board[ghostRook.y][ghostRook.x] != null) {
//                        return false;
//                    }
//                }
//
//                if (board[cell.y][cell.x] != null) {
//                    board[cell.y][cell.x].kill();
//                }
//
//                return true;
//            }
//
//            case QUEEN -> {
//                Point ghostQueen = new Point(currentPosition.x, currentPosition.y);
//                Point direction = straightMove(cell);
//                if (direction.x == 0 && direction.y == 0) {
//                    direction = diagonalMove(cell);
//                    if (direction.x == 0 && direction.y == 0) {
//                        return false;
//                    }
//                }
//
//                while (ghostQueen.x != cell.x - direction.x || ghostQueen.y != cell.y - direction.y) {
//                    ghostQueen.x += direction.x;
//                    ghostQueen.y += direction.y;
//
//                    if (board[ghostQueen.y][ghostQueen.x] != null) {
//                        return false;
//                    }
//                }
//
//                if (board[cell.y][cell.x] != null) {
//                    board[cell.y][cell.x].kill();
//                }
//
//                return true;
//            }
//
//            case KING -> {
//                Point direction = straightMove(cell);
//                if (direction.x == 0 && direction.y == 0) {
//                    direction = diagonalMove(cell);
//                    if (direction.x == 0 && direction.y == 0) {
//                        return false;
//                    }
//                }
//
//                if (currentPosition.x + direction.x == cell.x && currentPosition.y + direction.y == cell.y) {
//                    if (board[cell.y][cell.x] != null) {
//                        board[cell.y][cell.x].kill();
//                    }
//                    return true;
//                }
//            }
//        }
//
//        return false;

    public void setPosition(int x, int y) {

        if (x == currentPosition.x && y == currentPosition.y) {
            this.setLocation(x * cellSize, y * cellSize);
            return;
        }

        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        currentPosition.x = x;
        currentPosition.y = y;

        this.setLocation(x * cellSize, y * cellSize);

        Game.editBoardCell(currentPosition, this);
    }

    public void animatedMove(Point to) {
        int fps = 60;

        if (fps > cellSize)
            throw new IllegalArgumentException("FPS cannot be greater than cell size");

        double duration = 1;
        int frame = (int) (fps * duration);

        Point animationPosition = new Point(currentPosition.x * cellSize, currentPosition.y * cellSize);
        Point goalAnimation = new Point(to.x * cellSize, to.y * cellSize);

        int widthDifference = goalAnimation.x - animationPosition.x;
        int heightDifference = goalAnimation.y - animationPosition.y;

        double stepWidth = (double) widthDifference / frame;
        double stepHeight = (double) heightDifference / frame;

        for (int stepCount = 0; stepCount < frame; stepCount++) {
            animationPosition.x += stepWidth;
            animationPosition.y += stepHeight;

            this.setLocation(animationPosition);

            try {
                Thread.sleep((long) (duration * 100) / frame);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        currentPosition = to;
        Game.editBoardCell(to, this);
    }

    public void kill() {
        Game.editBoardCell(currentPosition, null);

        JPanel chessboardPanel = (JPanel) this.getParent();
        chessboardPanel.remove(this);
        chessboardPanel.repaint();
    }

    public PlayerColor getColor() {
        return pieceColor;
    }
    public static void setCellSize(int cellSize) {
        Piece.cellSize = cellSize;
    }
}
