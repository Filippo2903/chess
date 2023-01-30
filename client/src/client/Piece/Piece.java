package client.Piece;

import client.Game;

import gameUtils.PlayerColor;

import javax.swing.*;

import java.awt.*;

public abstract class Piece extends JLabel {
    // Label cell size
    protected static int cellSize = Game.CELL_SIZE;
    public Point currentPosition = new Point(-1, -1);
    protected final PlayerColor pieceColor;

//    protected boolean promoted = false;
//    protected Class<?> typePromotion = null;
//    private final Point newPosition = new Point();

    public Piece(PlayerColor playerColor) {
        super();

        this.pieceColor = playerColor;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        this.setVisible(true);

//        MouseAdapter mouseAdapter = new MouseAdapter() {
//            boolean undo;
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                if (e.getButton() == MouseEvent.BUTTON1) {
//                    undo = false;
//                    Piece.this.getParent().setComponentZOrder(Piece.this, 0);
//                } else {
//                    undo = true;
//
//                    Piece.this.setPosition(Piece.this.currentPosition);
//                }
//            }
//
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                if (undo) return;
//
//                int x = (int) Math.floor(((double) Piece.this.newPosition.x + (cellSize >> 1)) / cellSize);
//                int y = (int) Math.floor(((double) Piece.this.newPosition.y + (cellSize >> 1)) / cellSize);
//
//                if (x < 0 || x > 7 || y < 0 || y > 7) {
//                    undo = true;
//
//                    Piece.this.setPosition(Piece.this.currentPosition);
//                }
//
//                Piece.this.newPosition.x = Piece.this.getLocation().x + e.getX() - (cellSize >> 1);
//                Piece.this.newPosition.y = Piece.this.getLocation().y + e.getY() - (cellSize >> 1);
//                Piece.this.setLocation(Piece.this.newPosition.x, Piece.this.newPosition.y);
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (undo) return;
//
//                Point to = new Point(
//                        (Piece.this.newPosition.x + (cellSize >> 1)) / cellSize,
//                        (Piece.this.newPosition.y + (cellSize >> 1)) / cellSize
//                );
//
//                if (canMove(to)) {
//                    Point prevPosition = new Point(currentPosition.x, currentPosition.y);
//
//                    Piece.this.setPosition(to);
//
//                    if (promoted) { // ! DEBUG THIS !
//                        Client.sendMove(
//                            new Packet(
//                                prevPosition,
//                                currentPosition,
//                                    typePromotion
//                            )
//                        );
//                    } else {
//                        Client.sendMove(new Packet(prevPosition, currentPosition));
//                    }
//
//                    Thread recieveThread = new Thread(Client::receiveMove);
//                    recieveThread.start();
//
//                    Game.changePlayerTurn();
//
//                    return;
//                }
//
//                // Modify the position
//                Piece.this.setPosition(Piece.this.currentPosition);
//            }
//        };
//
//        this.addMouseListener(mouseAdapter);
//        this.addMouseMotionListener(mouseAdapter);
    }

    public void setPosition(Point newPosition) {
        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        this.setLocation(newPosition.x * cellSize, newPosition.y * cellSize);

        Game.editBoardCell(newPosition, this);

        currentPosition = newPosition;
    }

    public abstract boolean canMove(Point cell);

    public void kill() {
        Game.editBoardCell(currentPosition, null);

        JPanel chessboardPanel = (JPanel) this.getParent();
        chessboardPanel.remove(this);
        chessboardPanel.repaint();
    }

    public PlayerColor getColor() {
        return pieceColor;
    }
}
