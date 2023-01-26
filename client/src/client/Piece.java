package client;

import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class Piece extends JLabel {
    private PieceType type;
    private final PlayerColor pieceColor;

    private final Point newPosition = new Point();
    private Point currentPosition = new Point(-1, -1);

    // Label cell size
    private static int cellSize;

    boolean promoted = false;

    public Piece(PieceType type, PlayerColor playerColor, Point startPosition) {
        super();

        this.type = type;
        this.pieceColor = playerColor;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        this.setPosition(startPosition.x, startPosition.y);
        currentPosition = startPosition;

        String colorName = playerColor.toString().toLowerCase();
        String typeName = this.type.toString().toLowerCase();

        String path = "assets/" + colorName + typeName.substring(0, 1).toUpperCase() + typeName.substring(1) + ".png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
        this.setVisible(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean undo;
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    undo = false;
                    Piece.this.getParent().setComponentZOrder(Piece.this, 0);
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

                    System.out.println("Sium");

                    Thread test = new Thread(()->{Piece.this.move(to);});
                    test.start();



//                    if (promoted) {
//                        Client.sendMove(new Packet(prevPosition, currentPosition, Piece.this.getType()));
//                    } else {
//                        Client.sendMove(new Packet(prevPosition, currentPosition));
//                    }
//
//                    Thread recieveThread = new Thread(Client::receiveMove);
//                    recieveThread.start();
//
//                    Game.changePlayerTurn();

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
                boolean canMove = false;
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
                        canMove = true;
                    }
                }

                // En passant
                for (Point move : takeMoves) {
                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                        board[cell.y + 1][cell.x] != null &&
                        board[cell.y + 1][cell.x].getType() == PieceType.PAWN &&
                        currentPosition.y == enPassantPositionY) {

                        board[cell.y + 1][cell.x].kill();
                        canMove = true;
                    }
                }

                // If the cell in front is occupied
                if (board[cell.y][cell.x] != null) {
                    return false;
                }

                // Standard move
                if (cell.x == currentPosition.x + standardMove.x && cell.y == currentPosition.y + standardMove.y) {
                    canMove = true;
                }

                // Double move
                if (cell.x == currentPosition.x + doubleMove.x && cell.y == currentPosition.y + doubleMove.y &&
                    currentPosition.y == startPositionY) {
                    canMove = true;
                }

                // Promote
                if (canMove && cell.y == 0) {
                    this.promote(PieceType.QUEEN);
                }

                return canMove;
            }

            case KNIGHT -> {
                boolean canMove = false;

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
                        canMove = true;
                        break;
                    }
                }

                if (canMove) {
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
            this.setLocation(x * cellSize, y * cellSize);
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

    public void move(Point to) {

        System.out.println("Move");

        int fps = 120;
        double duration = 1;
        int steps = (int) (fps * duration);

        to.x *= cellSize;
        to.y *= cellSize;

        currentPosition.x *= cellSize;
        currentPosition.y *= cellSize;

        double heightDifference = to.y - currentPosition.y;
        double widthDifference = to.x - currentPosition.x;

        double stepHeight = heightDifference / steps;
        double stepWidth = widthDifference / steps;

        System.out.println("from.x: " + currentPosition.x + " from.y: " + currentPosition.y);
        System.out.println("to.x: " + to.x + " to.y: " + to.y);
        System.out.println("cellSize: " + cellSize);

        System.out.println("heightDifference: " + heightDifference + " widthDifference: " + widthDifference);
        System.out.println("stepHeight: " + stepHeight + " stepWidth: " + stepWidth);


        for (int stepCount = 0; stepCount < steps; stepCount++) {
            System.out.println("currentPosition.x: " + currentPosition.x + " currentPosition.y: " + currentPosition.y);

            currentPosition.x += stepWidth;
            currentPosition.y += stepHeight;

            this.setBounds(currentPosition.x, currentPosition.y, cellSize, cellSize);

            try {
                Thread.sleep((long) (duration / steps));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        currentPosition.x /= cellSize;
        currentPosition.y /= cellSize;

        Game.editBoardCell(currentPosition, this);
    }

    public void promote(PieceType promotion) throws IllegalArgumentException{
        if (type != PieceType.PAWN || promotion == PieceType.KING || promotion == PieceType.PAWN)
            throw new IllegalArgumentException("Cannot promote " + type.toString().toLowerCase());

        promoted = true;

        type = promotion;

        String path = "assets/" + pieceColor + promotion.toString().substring(0, 1).toUpperCase() + promotion.toString().substring(1) + ".png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }

    public void kill() {
        Game.editBoardCell(currentPosition, null);

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
