package client.piece;

import client.Client;
import client.Game;
import client.audio.AudioPlayer;
import client.audio.AudioType;
import client.movements.Movement;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import gameUtils.SpecialMoveType;
import modal.ErrorPopup;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;

/**
 * A class that represents a Piece and handles all his features
 */
public class Piece extends JLabel {
    private final PlayerColor pieceColor;
    private final PieceMoves pieceMoves;
    public Point currentPosition = new Point(-1, -1);
    private boolean hasMoved = false;

    /**
     * Create a piece with the given color and possible moves
     *
     * @param playerColor The piece color
     * @param pieceMoves  The moves that can the piece make
     */
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
     *
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

        double stepWidth = (double) widthDifference / frame;
        double stepHeight = (double) heightDifference / frame;

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

    /**
     * Check if the piece can move
     *
     * @param to The cell where the piece wants to move
     * @return <code>true</code> if the piece can move, <code>false</code> if it can't move
     */
    public boolean canMove(Point to, Piece[][] board) {
        for (Movement movement : pieceMoves.movements) {
            if (movement.canMove(currentPosition, to, board)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Move a piece to a point with all the needed checks
     *
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
            if (movement.canMove(currentPosition, to, Client.getGame().getBoard())) {
                if (this.getType() == PieceType.KING && Check.isCellAttacked(to, pieceColor, Client.getGame().getBoard())) {
                    continue;
                }

                if (Check.isCellAttacked(Client.getGame().findKing(Client.getGame().getBoard(), pieceColor), pieceColor, Client.getGame().getBoard()) &&
                        (movement.getSpecialMove() == SpecialMoveType.KINGSIDE_CASTLE ||
                                movement.getSpecialMove() == SpecialMoveType.QUEENSIDE_CASTLE)) {
                    continue;
                }

                Piece[][] temporaryBoard = Arrays.stream(Client.getGame().getBoard())
                        .map(row -> Arrays.copyOf(row, row.length))
                        .toArray(Piece[][]::new);
                temporaryBoard[currentPosition.y][currentPosition.x] = null;
                temporaryBoard[to.y][to.x] = this;

                if (Check.isCellAttacked(Client.getGame().findKing(temporaryBoard, pieceColor), pieceColor, temporaryBoard)) {
                    continue;
                }

                Client.getGame().movePiece(this, currentPosition, to, movement.getSpecialMove());

                hasMoved = true;

                return;
            }
        }

        // If the piece can't move to the desired position, place him in his cell
        this.setPosition(currentPosition);
    }

    /**
     * Set the piece position to a new position without checking for anything, except for checking if the piece is in
     * the board
     *
     * @param newPosition The new piece position
     */
    public void setPosition(Point newPosition) {
        // If the piece is inside the board
        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Client.getGame().editBoardCell(currentPosition, null);
        }

        // Set the new position to the JLabel
        this.setLocation(newPosition.x * Game.CELL_SIZE, newPosition.y * Game.CELL_SIZE);

        // Set the new position on the data board
        Client.getGame().editBoardCell(newPosition, this);

        // Set the new position at the piece
        currentPosition = newPosition;
    }

    /**
     * Kill the piece
     */
    public void kill() {
        Client.getGame().editBoardCell(currentPosition, null);

        Client.getGame().chessboardPanel.remove(this);
        Client.getGame().chessboardPanel.repaint();
    }

    /**
     * Get the piece color
     *
     * @return The piece color
     */
    public PlayerColor getColor() {
        return pieceColor;
    }

    /**
     * Get the piece type
     *
     * @return The piece type
     */
    public PieceType getType() {
        return pieceMoves.type;
    }

    /**
     * Get if the piece has moved
     *
     * @return <code>true</code> if the piece has moved, <code>false</code> if the piece has not moved
     */
    public boolean hasNotMoved() {
        return !hasMoved;
    }

    /**
     * Set the piece image
     */
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
