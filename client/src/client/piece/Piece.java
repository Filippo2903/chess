package client.piece;

import client.Client;
import client.Game;
import client.movements.Movement;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import modal.ErrorPopup;

import javax.swing.*;

import java.awt.Point;
import java.awt.Image;
import java.net.URL;
import java.util.Arrays;

public class Piece extends JLabel {
    public Point currentPosition = new Point(-1, -1);
    private final PlayerColor pieceColor;
    private final PieceMoves pieceMoves;
    private boolean hasMoved = false;

    public Piece(PlayerColor playerColor, PieceMoves pieceMoves) {
        super();

        this.pieceColor = playerColor;
        this.pieceMoves = pieceMoves;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        this.setVisible(true);
        
        DragAndDrop dragAndDrop = new DragAndDrop(this);

        this.addMouseListener(dragAndDrop);
        this.addMouseMotionListener(dragAndDrop);
    }

    /**
     * Move a piece to a point smoothly
     * @param to Arrival cell
     */
    public void animatedMove(Point to) {
        int fps = 60;

        double duration = 200;
        int frame = (int) (fps * (duration / 1000));

        Point animationPosition = new Point(currentPosition.x * Game.CELL_SIZE, currentPosition.y * Game.CELL_SIZE);
        Point goalAnimation = new Point(to.x * Game.CELL_SIZE, to.y * Game.CELL_SIZE);

        int widthDifference = goalAnimation.x - animationPosition.x;
        int heightDifference = goalAnimation.y - animationPosition.y;

        double stepWidth = (double)widthDifference / frame;
        double stepHeight = (double)heightDifference / frame;

        for (int stepCount = 0; stepCount < frame; stepCount++) {
            animationPosition.x += stepWidth;
            animationPosition.y += stepHeight;

            this.setLocation(animationPosition);

            try {
                Thread.sleep((long) duration / frame);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        this.setPosition(to);
    }

    public boolean canMove(Point to) {
        for (Movement movement : pieceMoves.movements) {
            if (movement.canMove(currentPosition, to))
                return true;
        }

        return false;
    }

    private boolean isKingInCheck(Piece[][] board) {
        for (int x = 0; x < Game.DIM_CHESSBOARD; x++) {
            for (int y = 0; y < Game.DIM_CHESSBOARD; y++) {
                if (board[y][x] != null &&
                    board[y][x].getType() == PieceType.KING &&
                    board[y][x].getColor() == pieceColor) {
//                    System.out.println("King x: " + x + " y: " + y);
//                    System.out.print(!Check.isCellAttacked(board[y][x].currentPosition, pieceColor, board) ? "Is not " : "Is ");
//                    System.out.println("in check");
//                    System.out.println();
//                    Game.printBoard(board);
//                    System.out.println();
//                    System.out.println();
                    return Check.isCellAttacked(new Point(x, y), pieceColor, board);
                }
            }
        }

        return false;
    }

    /**
     * Move a piece to a point
     * @param to Arrival cell
     */
    public void move(Point to) {
        // If it's not the client turn or the client tries to move a piece that isn't his, don't move
        if (CheckPlayerMove.isNotPlayerTurn() ||
            CheckPlayerMove.isNotPlayerPiece(this.getColor()) ||
            CheckPlayerMove.isMovingOnHisOwnPiece(this.getColor(), to)) {

            this.setLocation(currentPosition.x * Game.CELL_SIZE, currentPosition.y * Game.CELL_SIZE);
            return;
        }

        // If the piece is moved where he already is, don't move
        if (to.x == currentPosition.x && to.y == currentPosition.y) {
            this.setLocation(currentPosition.x * Game.CELL_SIZE, currentPosition.y * Game.CELL_SIZE);
            return;
        }

        // Check if the piece can move to the desired cell
        for (Movement movement : pieceMoves.movements) {
            if (movement.canMove(currentPosition, to)) {
                if (this.getType() == PieceType.KING && Check.isCellAttacked(to, pieceColor, Game.getBoard())) {
                    System.out.println("Socsa");
                    continue;
                }

                SpecialMovesMap.specialMovesMap.get(movement.getSpecialMove()).move(currentPosition, to);

                // Check if the piece is a pawn, and it's going to promote
                if (pieceMoves.type == PieceType.PAWN && to.y == 0) {
                    PieceMoves promoteType = Game.inputPromotionType();

                    // Add the from and to cells highlights
                    Game.setPositionFromCell(currentPosition);
                    Game.setPositionToCell(to);

                    Game.promote(this, promoteType, to);

                    // Send the server the move specifying a change in the piece's type
                    Client.sendMove(new Packet(currentPosition, to, PieceType.valueOf(promoteType.toString())));
                } else {
                    Piece[][] temporaryBoard = Arrays.stream(Game.getBoard())
                            .map(row -> Arrays.copyOf(row, row.length))
                            .toArray(Piece[][]::new);

                    temporaryBoard[currentPosition.y][currentPosition.x] = null;
                    temporaryBoard[to.y][to.x] = this;

                    if (isKingInCheck(temporaryBoard)) {
                        continue;
                    }

                    Client.sendMove(new Packet(currentPosition, to, movement.getSpecialMove()));

                    // Add the from and to cells highlights
                    Game.setPositionFromCell(currentPosition);
                    Game.setPositionToCell(to);

                    // Change the position in the client
                    this.setPosition(to);
                }

                if (!hasMoved) {
                    hasMoved = true;
                }

                Game.changePlayerTurn();

                // Start listening for the enemy move
                Thread recieveThread = new Thread(Client::receiveMove);
                recieveThread.start();

                Game.chessboardPanel.repaint();

                return;
            }
        }

        // If the piece can't move to the desired position, place him in his cell
        this.setPosition(currentPosition);
    }

    public void setPosition(Point newPosition) {
        // If the piece is inside the board
        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        // Set the new position to the JLabel
        this.setLocation(newPosition.x * Game.CELL_SIZE, newPosition.y * Game.CELL_SIZE);

        // Set the new position on the data board
        Game.editBoardCell(newPosition, this);

        // Set the new position at the piece
        currentPosition = newPosition;
    }

    /**
     * Kill the piece
     */
    public void kill() {
        Game.editBoardCell(currentPosition, null);

        Game.chessboardPanel.remove(this);
        Game.chessboardPanel.repaint();
    }

    public PlayerColor getColor() {
        return pieceColor;
    }
    public PieceType getType() {
        return pieceMoves.type;
    }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setImage() {
        String iconName =
                pieceColor.toString().toLowerCase() +
                pieceMoves.type.toString().substring(0, 1).toUpperCase() +
                pieceMoves.type.toString().substring(1).toLowerCase() +
                ".png";

        URL path = ClassLoader.getSystemResource(iconName);
        if (path == null) {
            ErrorPopup.show(7);
            System.exit(-1);
        }

        Image icon = new ImageIcon(path).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(Game.CELL_SIZE, Game.CELL_SIZE, Image.SCALE_SMOOTH)));
    }
}
