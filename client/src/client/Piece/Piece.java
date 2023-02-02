package client.Piece;

import client.CanPlayerMove;
import client.Client;
import client.DragAndDrop;
import client.Game;
import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import javax.swing.*;

import java.awt.*;
import java.util.Objects;

public class Piece extends JLabel {
    private final int cellSize = Game.CELL_SIZE;

    public Point currentPosition = new Point(-1, -1);
    private final PlayerColor pieceColor;
    private final PieceType type;
    private final Movement movement;

    public Piece(PlayerColor playerColor, PieceType type, Movement movement) {
        super();

        this.pieceColor = playerColor;

        this.type = type;

        this.movement = movement;

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

        if (movement.canMove(currentPosition, to)) {
            Piece[][] board = Game.getBoard();

            if (board[to.y][to.x] != null) {
                if (board[to.y][to.x].getColor() == pieceColor) {
                    return;
                }
                board[to.y][to.x].kill();
            }

            Client.sendMove(new Packet(currentPosition, to));

            this.setPosition(to);

            Thread recieveThread = new Thread(Client::receiveMove);
            recieveThread.start();

            Game.changePlayerTurn();
        }
    }

    public void setPosition(Point newPosition) {
        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        this.setLocation(newPosition.x * cellSize, newPosition.y * cellSize);

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

	public Image getImage() {
		String path = "assets/" + pieceColor + type + ".png";

        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
	}

	public void setImage() {
        Image icon = getImage();
		this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
	}
}
