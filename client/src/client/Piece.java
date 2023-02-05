package client;

import client.audio.AudioPlayer;
import client.audio.AudioType;
import client.movements.EnPassant;
import client.movements.KingsideCastle;
import client.movements.Movement;
import client.movements.QueensideCastle;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import gameUtils.SpecialMove;
import modal.ErrorPopup;

import javax.swing.*;

import java.awt.Point;
import java.awt.Image;
import java.net.URL;

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

        double duration = 1;
        int frame = (int) (fps * duration);

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
                Thread.sleep((long) (duration * 100) / frame);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        this.setPosition(to);
    }

    /**
     * Move a piece to a point
     * @param to Arrival cell
     */
    public void move(Point to) {
        // If it's not the client turn or the client tries to move a piece that isn't his, don't move
        if (CheckPlayerMove.isNotPlayerTurn() || CheckPlayerMove.isNotPlayerPiece(this.getColor())) {
            this.setPosition(currentPosition);
            return;
        }

        // If the piece is moved where he already is, don't move
        if (to.x == currentPosition.x && to.y == currentPosition.y) {
            this.setPosition(currentPosition);
            return;
        }

        // Check if the piece can move to the desired cell
        for (Movement movement : pieceMoves.movements) {
            if (movement.canMove(currentPosition, to)) {
                SpecialMove moveStyle = null;

                // Add the from and to cells highlights
                Game.setPositionFromCell(currentPosition);
                Game.setPositionToCell(to);

                // Fetch the board from the game
                Piece[][] board = Game.getBoard();

                // Check if the move is an En Passant
                if (movement.getClass() == EnPassant.class) {
                    // Kill the passed pawn
                    board[to.y + 1][to.x].kill();

                    moveStyle = SpecialMove.EN_PASSANT;
                }

                // Check if the move is a Kingside Castle
                else if (movement.getClass() == KingsideCastle.class) {
                    final Point ROOK_START_POSITION = new Point(7, 7);
                    final Point ROOK_ARRIVAL_POSITION = new Point(5, 7);

                    // Move the rook
                    board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);

                    moveStyle = SpecialMove.KINGSIDE_CASTLE;
                }

                // Check if the move is a Queenside Castle
                else if (movement.getClass() == QueensideCastle.class) {
                    final Point ROOK_START_POSITION = new Point(0, 7);
                    final Point ROOK_ARRIVAL_POSITION = new Point(3, 7);

                    // Move the rook
                    board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);

                    moveStyle =  SpecialMove.QUEENSIDE_CASTLE;
                }

                // The move is not a special move
                else {
                    // Check if the piece is moving to an occupied cell
                    if (board[to.y][to.x] != null) {
                        AudioPlayer.play(AudioType.TAKE);

                        // If the cell is occupied by an allied piece just do nothing
                        if (board[to.y][to.x].getColor() == pieceColor) {
                            return;
                        }

                        // Eat the piece
                        board[to.y][to.x].kill();
                    } else {
                        AudioPlayer.play(AudioType.MOVE);
                    }
                }

                // Check if the piece is a pawn, and it's going to promote
                if (pieceMoves.type == PieceType.PAWN && to.y == 0) {
                    PieceMoves promoteType = Game.inputPromotionType();
                    Game.promote(this, promoteType, to);

                    // Send the server the move specifying a change in the piece's type
                    Client.sendMove(new Packet(currentPosition, to, PieceType.valueOf(promoteType.toString())));
                } else {
                    Client.sendMove(new Packet(currentPosition, to, moveStyle));

                    // Change the position in the client
                    this.setPosition(to);
                }

                hasMoved = true;

                Game.changePlayerTurn();

                // Start listening for the enemy move
                Thread recieveThread = new Thread(Client::receiveMove);
                recieveThread.start();

                return;
            }
        }

        // If the piece can't move to the desired position, place him in his cell
        this.setPosition(currentPosition);
    }

    public void setPosition(Point newPosition) {
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
//        System.out.println(this.getType());
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
