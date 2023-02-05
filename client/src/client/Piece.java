package client;

import client.Movements.EnPassant;
import client.Movements.KingsideCastle;
import client.Movements.Movement;
import client.Movements.QueensideCastle;
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
     * Move a piece smoothly
     *
     * @param to Arrival cell
     */
    public void animatedMove(Point to) {
        int fps = 60;

        if (fps > Game.CELL_SIZE)
            throw new IllegalArgumentException("FPS cannot be greater than cell size");

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

    public void move(Point to) {
        if (CheckPlayerMove.isNotPlayerTurn() || CheckPlayerMove.isNotPlayerPiece(this.getColor())) {
            this.setPosition(currentPosition);
            return;
        }

        if (to.x == currentPosition.x && to.y == currentPosition.y) {
            this.setPosition(currentPosition);
            return;
        }

        for (Movement movement : pieceMoves.movements) {
            if (movement.canMove(currentPosition, to)) {
                Game.setFromCellVisible();
                Game.setToCellVisible();
                Game.setPositionFromCell(currentPosition);
                Game.setPositionToCell(to);

                Piece[][] board = Game.getBoard();

                if (movement.getClass() == EnPassant.class) {
                    board[to.y + 1][to.x].kill();

                    Client.sendMove(new Packet(currentPosition, to, SpecialMove.EN_PASSANT));
                }

                else if (movement.getClass() == KingsideCastle.class) {
                    final Point ROOK_START_POSITION = new Point(7, 7);
                    final Point ROOK_ARRIVAL_POSITION = new Point(5, 7);

                    Thread animatedMove = new Thread(()-> {
                        board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);
                    });

                    animatedMove.start();

                    Client.sendMove(new Packet(currentPosition, to, SpecialMove.KINGSIDE_CASTLE));
                }

                else if (movement.getClass() == QueensideCastle.class) {
                    final Point ROOK_START_POSITION = new Point(0, 7);
                    final Point ROOK_ARRIVAL_POSITION = new Point(3, 7);

                    Thread animatedMove = new Thread(()-> {
                        board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].animatedMove(ROOK_ARRIVAL_POSITION);
                    });

                    animatedMove.start();

                    Client.sendMove(new Packet(currentPosition, to, SpecialMove.QUEENSIDE_CASTLE));
                }

                else {
                    if (board[to.y][to.x] != null) {
                        if (board[to.y][to.x].getColor() == pieceColor) {
                            return;
                        }
                        board[to.y][to.x].kill();
                    }

                    if (pieceMoves.type == PieceType.PAWN && to.y == 0) {
                        PieceType promoteType = Game.promote(this, to);
                        Client.sendMove(new Packet(currentPosition, to, promoteType));
                    } else {
                        Client.sendMove(new Packet(currentPosition, to));
                    }
                }

                this.setPosition(to);

                hasMoved = true;

                Game.changePlayerTurn();

                Thread recieveThread = new Thread(Client::receiveMove);
                recieveThread.start();

                return;
            }
        }

        this.setPosition(currentPosition);
    }

    public void setPosition(Point newPosition) {
        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        this.setLocation(newPosition.x * Game.CELL_SIZE, newPosition.y * Game.CELL_SIZE);

        Game.editBoardCell(newPosition, this);

        currentPosition = newPosition;
    }

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
