package client;

import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import modal.ErrorPopup;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Piece extends JLabel {
    private PieceType type;
    private final PlayerColor pieceColor;
    private boolean promoted = false;

    private final Point newPosition = new Point();
    private final Point currentPosition = new Point(-1, -1);

    // Label cell size
    private static int cellSize;

    public Piece(PieceType type, PlayerColor playerColor) {
        super();

        this.type = type;
        this.pieceColor = playerColor;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        String colorName = playerColor.toString().toLowerCase();
        String typeName = this.type.toString().toLowerCase();

        String path = "assets/" + colorName + typeName.substring(0, 1).toUpperCase() + typeName.substring(1) + ".png";

        BufferedImage icon = null;

        try {
            icon = ImageIO.read(Objects.requireNonNull(Piece.class.getResource(path)));
        } catch (IOException e) {
            ErrorPopup.show(300);
            System.exit(-1);
        }

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
        this.setVisible(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean undo = false;
            @Override
            public void mousePressed(MouseEvent e) {
                Piece piece = (Piece) e.getSource();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    undo = false;
                    piece.getParent().setComponentZOrder(piece, 0);

                    Piece.this.currentPosition.x =
                            (piece.getLocation().x + e.getX() - (cellSize >> 1)  + (cellSize >> 1)) / cellSize;
                    Piece.this.currentPosition.y =
                            (piece.getLocation().y + e.getY() - (cellSize >> 1)  + (cellSize >> 1)) / cellSize;
                }
                else {
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

                Piece piece = (Piece) e.getSource();
                Piece.this.newPosition.x = piece.getLocation().x + e.getX() - (cellSize >> 1);
                Piece.this.newPosition.y = piece.getLocation().y + e.getY() - (cellSize >> 1);
                piece.setLocation(Piece.this.newPosition.x, Piece.this.newPosition.y);
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

                    if (promoted) {
                        Client.sendMove(new Packet(prevPosition, currentPosition, type));
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

    private Point diagonalMove(Point cell) {
        Point direction = new Point();

        if (Math.abs(cell.x - currentPosition.x) == Math.abs(cell.y - currentPosition.y) ||
            Math.abs(cell.x + currentPosition.x) == Math.abs(cell.y - currentPosition.y) ||
            Math.abs(cell.x + currentPosition.x) == Math.abs(cell.y + currentPosition.y) ||
            Math.abs(cell.x - currentPosition.x) == Math.abs(cell.y + currentPosition.y) ) {

            direction.x = currentPosition.x - cell.x > 0 ? -1 : 1;
            direction.y = currentPosition.y - cell.y > 0 ? -1 : 1;
        }

        return direction;
    }
    private Point straightMove(Point cell) {
        Point direction = new Point();

        if (cell.x == currentPosition.x) {
            direction.x = 0;
            direction.y = currentPosition.y > cell.y ? -1 : 1;
        }
        else if (cell.y == currentPosition.y) {
            direction.x = currentPosition.x > cell.x ? -1 : 1;
            direction.y = 0;
        }

        return direction;
    }

    public boolean canMove(Point cell) {
        Piece[][] board = Game.getBoard();

        if (Game.getPlayerTurn() != Game.getPlayerColor())
            return false;


        // If is not the turn of the player turn
        if (this.getColor() != Game.getPlayerColor()) {
            return false;
        }

        // If the user tries to move a piece that isn't his
        if (board[cell.y][cell.x] != null && board[cell.y][cell.x].getColor() == Game.getPlayerColor()) {
            return false;
        }

        switch (this.getType()) {
            case PAWN -> {
                boolean can = false;
                int startPositionY = 6;
                int enPassantPositionY = 3;

                Point standardMove = new Point(0, -1);
                Point doubleMove = new Point(0, -2);
                Point[] takeMoves = {
                        new Point(-1, -1),
                        new Point(1, -1)
                };

                // Take move
                for (Point move : takeMoves) {
                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                            board[cell.y][cell.x] != null) {

                        board[cell.y][cell.x].kill();
                        can = true;
                    }
                }

                // En passant
                for (Point move : takeMoves) {
                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                        board[cell.y + 1][cell.x] != null &&
                        board[cell.y + 1][cell.x].getType() == PieceType.PAWN &&
                        currentPosition.y == enPassantPositionY) {

                        board[cell.y + 1][cell.x].kill();
                        can = true;
                    }
                }

                // If the cell in front is occupied
                if (board[cell.y][cell.x] != null) {
                    return false;
                }

                // Standard move
                if (cell.x == currentPosition.x + standardMove.x && cell.y == currentPosition.y + standardMove.y) {
                    can = true;
                }

                // Double move
                if (cell.x == currentPosition.x + doubleMove.x && cell.y == currentPosition.y + doubleMove.y &&
                    currentPosition.y == startPositionY) {
                    can = true;
                }

                // Promote
                if (can && cell.y == 0) {
                    this.promote(PieceType.QUEEN, cell);
                }

                return can;
            }

            case KNIGHT -> {
                boolean can = false;

                Point[] moves = {
                        new Point(-2, -1),
                        new Point(-1, -2),
                        new Point(1, -2),
                        new Point(2, -1),
                        new Point(2, 1),
                        new Point(1, 2),
                        new Point(-1, 2),
                        new Point(-2, 1)
                };

                for (Point move : moves) {
                    if (cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) {
                        can = true;
                        break;
                    }
                }

                if (can) {
                    if (board[cell.y][cell.x] != null) {
                        board[cell.y][cell.x].kill();
                    }

                    return true;
                }
            }

            case BISHOP -> {
                Point ghostBishop = new Point(currentPosition.x, currentPosition.y);
                Point direction = diagonalMove(cell);

                if (direction.x == 0 && direction.y == 0) {
                    return false;
                }

                while (ghostBishop.x != cell.x - direction.x || ghostBishop.y != cell.y - direction.y) {
                    ghostBishop.x += direction.x;
                    ghostBishop.y += direction.y;

                    if (board[ghostBishop.y][ghostBishop.x] != null) {
                        return false;
                    }
                }

                if (board[cell.y][cell.x] != null) {
                    board[cell.y][cell.x].kill();
                }

                return true;
            }

            case ROOK -> {
                Point ghostRook = new Point(currentPosition.x, currentPosition.y);
                Point direction = straightMove(cell);

                if (direction.x == 0 && direction.y == 0) {
                    return false;
                }

                while (ghostRook.x != cell.x - direction.x || ghostRook.y != cell.y - direction.y) {
                    ghostRook.x += direction.x;
                    ghostRook.y += direction.y;

                    if (board[ghostRook.y][ghostRook.x] != null) {
                        return false;
                    }
                }

                if (board[cell.y][cell.x] != null) {
                    board[cell.y][cell.x].kill();
                }

                return true;
            }

            case QUEEN -> {
                Point ghostQueen = new Point(currentPosition.x, currentPosition.y);
                Point direction = straightMove(cell);
                if (direction.x == 0 && direction.y == 0) {
                    direction = diagonalMove(cell);
                    if (direction.x == 0 && direction.y == 0) {
                        return false;
                    }
                }

                while (ghostQueen.x != cell.x - direction.x || ghostQueen.y != cell.y - direction.y) {
                    ghostQueen.x += direction.x;
                    ghostQueen.y += direction.y;

                    if (board[ghostQueen.y][ghostQueen.x] != null) {
                        return false;
                    }
                }

                if (board[cell.y][cell.x] != null) {
                    board[cell.y][cell.x].kill();
                }

                return true;
            }

            case KING -> {
                Point direction = straightMove(cell);
                if (direction.x == 0 && direction.y == 0) {
                    direction = diagonalMove(cell);
                    if (direction.x == 0 && direction.y == 0) {
                        return false;
                    }
                }

                if (currentPosition.x + direction.x == cell.x && currentPosition.y + direction.y == cell.y) {
                    if (board[cell.y][cell.x] != null) {
                        board[cell.y][cell.x].kill();
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public void setPosition(int x, int y) {
        if (x == currentPosition.x && y == currentPosition.y) {
            this.setBounds(x * cellSize, y * cellSize, cellSize, cellSize);
            return;
        }

        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        currentPosition.x = x;
        currentPosition.y = y;

        this.setBounds(x * cellSize, y * cellSize, cellSize, cellSize);

        Game.editBoardCell(currentPosition, this);
    }

    public void promote(PieceType promotion, Point promotedCell) throws IllegalArgumentException{
        if (type != PieceType.PAWN || promotion == PieceType.KING || promotion == PieceType.PAWN)
            throw new IllegalArgumentException("Cannot promote " + type.toString().toLowerCase());

        type = promotion;

        Container chessboardPanel = this.getParent();

        this.kill();

        Piece piece = new Piece(PieceType.QUEEN, Game.getPlayerColor());
        piece.setPosition(promotedCell.x, promotedCell.y);

        Piece[][] board = Game.getBoard();
        board[promotedCell.y][promotedCell.x] = piece;

        chessboardPanel.add(piece);
        chessboardPanel.repaint();

        promoted = true;
    }

    public void kill() {
        Piece[][] board = Game.getBoard();
        board[currentPosition.y][currentPosition.x] = null;

        Container chessboardPanel = this.getParent();
        chessboardPanel.remove(this);
        chessboardPanel.repaint();
    }

    public PieceType getType() {
        return type;
    }
    public PlayerColor getColor() {
        return pieceColor;
    }
    public static void setCellSize(int cellSize) {
        Piece.cellSize = cellSize;
    }
}
