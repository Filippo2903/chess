package client;

import client.Movements.EnPassant;
import client.Movements.KingsideCastle;
import client.Movements.Movement;
import client.Movements.QueensideCastle;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;
import gameUtils.SpecialMove;

import javax.swing.*;

import java.awt.*;
import java.util.Objects;

public class Piece extends JLabel {
    public Point currentPosition = new Point(-1, -1);
    private final PlayerColor pieceColor;
    private final PieceType type;
    private boolean hasMoved = false;

    public Piece(PlayerColor playerColor, PieceType type) {
        super();

        this.pieceColor = playerColor;

        this.type = type;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        this.setVisible(true);
        
        DragAndDrop dragAndDrop = new DragAndDrop(this);
        
        this.addMouseListener(dragAndDrop);
        this.addMouseMotionListener(dragAndDrop);
    }

    public void move(Point to) {
        if (CanPlayerMove.isNotPlayerTurn() || CanPlayerMove.isNotPlayerPiece(this.getColor())) {
            return;
        }

        for (Movement movement : type.movements) {
            if (movement.canMove(currentPosition, to)) {
                Piece[][] board = Game.getBoard();

                if (movement.getClass() == EnPassant.class) {
                    board[to.y + 1][to.x].kill();

                    Client.sendMove(new Packet(currentPosition, to, SpecialMove.EN_PASSANT));
                }

                else if (movement.getClass() == KingsideCastle.class) {
                    final Point ROOK_START_POSITION = new Point(7, 7);
                    final Point ROOK_ARRIVAL_POSITION = new Point(5, 7);

                    Game.animatedMove(
                            ROOK_START_POSITION,
                            ROOK_ARRIVAL_POSITION,
                            board[ROOK_START_POSITION.y][ROOK_START_POSITION.x]
                    );

                    board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].setPosition(ROOK_ARRIVAL_POSITION);

                    Client.sendMove(new Packet(currentPosition, to, SpecialMove.KINGSIDE_CASTLE));
                }

                else if (movement.getClass() == QueensideCastle.class) {
                    final Point ROOK_START_POSITION = new Point(0, 7);
                    final Point ROOK_ARRIVAL_POSITION = new Point(3, 7);

                    board[ROOK_START_POSITION.y][ROOK_START_POSITION.x].setPosition(ROOK_ARRIVAL_POSITION);

                    Client.sendMove(new Packet(currentPosition, to, SpecialMove.QUEENSIDE_CASTLE));
                }

                else {
                    if (board[to.y][to.x] != null) {
                        if (board[to.y][to.x].getColor() == pieceColor) {
                            return;
                        }
                        board[to.y][to.x].kill();
                    }

                    Client.sendMove(new Packet(currentPosition, to));
                }

                this.setPosition(to);

                Thread recieveThread = new Thread(Client::receiveMove);
                recieveThread.start();

                Game.changePlayerTurn();

                hasMoved = true;

                break;
            }
        }
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

        JPanel chessboardPanel = (JPanel) this.getParent();
        chessboardPanel.remove(this);
        chessboardPanel.repaint();
    }

    public PlayerColor getColor() {
        return pieceColor;
    }
    public PieceType getType() {
        return type;
    }
    public boolean hasMoved() {
        return hasMoved;
    }

	public void setImage() {
        String path = "assets/" + pieceColor + type + ".png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

		this.setIcon(new ImageIcon(icon.getScaledInstance(Game.CELL_SIZE, Game.CELL_SIZE, Image.SCALE_SMOOTH)));
	}
}
